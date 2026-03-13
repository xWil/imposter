package gg.wil.imposter.services;

import gg.wil.imposter.ImposterApplication;
import gg.wil.imposter.api.messages.LobbyResponse;
import gg.wil.imposter.exception.LobbyException;
import gg.wil.imposter.exception.WebSocketException;
import gg.wil.imposter.exception.lobby.CantCreateLobbyException;
import gg.wil.imposter.exception.lobby.InvalidUsernameException;
import gg.wil.imposter.exception.lobby.LobbyNotFoundException;
import gg.wil.imposter.exception.lobby.PlayerNotAllowedException;
import gg.wil.imposter.exception.websocket.InvalidLobbyCodeException;
import gg.wil.imposter.exception.websocket.InvalidPlayerIdException;
import gg.wil.imposter.game.Player;
import gg.wil.imposter.game.Lobby;
import gg.wil.imposter.repo.LobbyRepo;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.util.UUID;

@Service
public class LobbyService {

    private final LobbyRepo lobbyRepo;

    public LobbyService(LobbyRepo lobbyRepo) {
        this.lobbyRepo = lobbyRepo;
        LobbyRepo.setInstance(lobbyRepo);
    }

    public final Mono<LobbyResponse> createLobby() {
        Player host = Player.create("");
        Lobby lobby = Lobby.create(host);
        if(lobby == null) return Mono.error(new CantCreateLobbyException());
        lobbyRepo.addLobby(lobby);
        String websocketURL = ImposterApplication.WEBSOCKET_URL + lobby.getLobbyCode();
        return Mono.just(new LobbyResponse(lobby.getLobbyCode(), host.getUUID().toString(), websocketURL));
    }

    public final Mono<LobbyResponse> joinLobby(String lobbyCode, String username) {
        Lobby lobby = lobbyRepo.getLobby(lobbyCode);
        if(lobby == null) return Mono.error(new LobbyNotFoundException(lobbyCode));
        if(!checkUsername(username)) return Mono.error(new InvalidUsernameException(lobbyCode));

        Player player = Player.create(username);
        try {
            lobby.addPlayer(player);
        } catch (LobbyException e) {
            return Mono.error(e);
        }
        String websocketURL = ImposterApplication.WEBSOCKET_URL + lobby.getLobbyCode();
        return Mono.just(new LobbyResponse(lobby.getLobbyCode(), player.getUUID().toString(), websocketURL));
    }

    public final Mono<LobbyResponse> rejoinLobby(String lobbyCode, UUID playerID) {
        Lobby lobby = lobbyRepo.getLobby(lobbyCode);
        if(lobby == null) return Mono.error(new LobbyNotFoundException(lobbyCode));
        if(!lobby.hasPlayer(playerID)) return Mono.error(new PlayerNotAllowedException(lobbyCode));

        Player player = lobby.getPlayer(playerID);
        String websocketURL = ImposterApplication.WEBSOCKET_URL + lobby.getLobbyCode();
        return Mono.just(new LobbyResponse(lobby.getLobbyCode(), player.getUUID().toString(), websocketURL));
    }

    public final void playerConnected(String lobbyCode, UUID playerID, WebSocketSession session, Sinks.Many<String> outgoingSink) {
        Lobby lobby = lobbyRepo.getLobby(lobbyCode);
        lobby.playerConnected(playerID, session, outgoingSink);
    }

    public final Mono<Void> handleMessage(String lobbyCode, UUID playerID, String message) {
        Lobby lobby = lobbyRepo.getLobby(lobbyCode);
        if(lobby == null) return Mono.empty();
        return lobby.receiveMessage(playerID, message);
    }

    public final void playerDisconnected(String lobbyCode, UUID playerID) {
        Lobby lobby = lobbyRepo.getLobby(lobbyCode);
        if(lobby == null) return;
        lobby.playerDisconnected(playerID);
    }

    public final void checkCredentials(String lobbyCode, UUID playerID) throws WebSocketException {
        Lobby lobby = lobbyRepo.getLobby(lobbyCode);
        if(lobby == null) throw new InvalidLobbyCodeException();
        if(!lobby.hasPlayer(playerID) && !lobby.getHost().getUUID().equals(playerID)) throw new InvalidPlayerIdException();
    }

    public final boolean checkUsername(String username) {
        if(username == null) return false;

        username = username.trim();
        if(username.length() > 16 || username.isEmpty()) return false;
        if(username.chars().anyMatch(Character::isISOControl)) return false;
        if(username.chars().anyMatch(c ->
                c == 0x200B || // zero-width space
                c == 0x200C || // zero-width non-joiner
                c == 0x200D || // zero-width joiner
                c == 0xFEFF    // zero-width no-break space
        )) return false;

        // a
        return username.matches("^[a-zA-Z0-9_ .!$-]+$");
    }
}
