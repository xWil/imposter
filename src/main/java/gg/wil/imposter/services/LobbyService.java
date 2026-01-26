package gg.wil.imposter.services;

import gg.wil.imposter.api.messages.LobbyResponse;
import gg.wil.imposter.exception.LobbyException;
import gg.wil.imposter.exception.WebSocketException;
import gg.wil.imposter.exception.lobby.CantCreateLobbyException;
import gg.wil.imposter.exception.lobby.LobbyNotFoundException;
import gg.wil.imposter.exception.websocket.InvalidLobbyCodeException;
import gg.wil.imposter.exception.websocket.InvalidPlayerIdException;
import gg.wil.imposter.game.Player;
import gg.wil.imposter.game.lobby.Lobby;
import gg.wil.imposter.repo.LobbyRepo;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Service
public class LobbyService {

    private final LobbyRepo lobbyRepo;

    public LobbyService(LobbyRepo lobbyRepo) {
        this.lobbyRepo = lobbyRepo;
        LobbyRepo.setInstance(lobbyRepo);
    }

    public final Mono<LobbyResponse> createLobby(String username) {
        Player host = Player.create(username);
        Lobby lobby = Lobby.create(host);
        if(lobby == null) return Mono.error(new CantCreateLobbyException());
        lobbyRepo.addLobby(lobby);
        return Mono.just(new LobbyResponse(lobby.getLobbyCode(), "ws://localhost:8080/ws/lobby/", host.getUUID().toString()));
    }

    public final Mono<LobbyResponse> joinLobby(String lobbyCode, String username) {
        Lobby lobby = lobbyRepo.getLobby(lobbyCode);
        if(lobby == null) return Mono.error(new LobbyNotFoundException(lobbyCode));
        Player player = Player.create(username);
        try {
            lobby.addPlayer(player);
        } catch (LobbyException e) {
            return Mono.error(e);
        }
        return Mono.just(new LobbyResponse(lobby.getLobbyCode(), "ws://localhost:8080/ws/lobby/", player.getUUID().toString()));
    }

    public final void playerConnected(String lobbyCode, UUID playerID, WebSocketSession session) {
        Lobby lobby = lobbyRepo.getLobby(lobbyCode);
        lobby.playerConnected(playerID, session);
        lobbyRepo.getLobby(lobbyCode).playerConnected(playerID, session);
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
        if(!lobby.hasPlayer(playerID)) throw new InvalidPlayerIdException();
    }
}
