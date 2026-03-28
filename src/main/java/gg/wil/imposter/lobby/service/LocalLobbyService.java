package gg.wil.imposter.lobby.service;

import gg.wil.imposter.Config;
import gg.wil.imposter.api.messages.LobbyResponse;
import gg.wil.imposter.exception.LobbyException;
import gg.wil.imposter.exception.WebSocketException;
import gg.wil.imposter.exception.lobby.CantCreateLobbyException;
import gg.wil.imposter.exception.lobby.InvalidUsernameException;
import gg.wil.imposter.exception.lobby.LobbyNotFoundException;
import gg.wil.imposter.exception.websocket.AlreadyConnectedException;
import gg.wil.imposter.exception.websocket.InvalidLobbyCodeException;
import gg.wil.imposter.exception.websocket.InvalidSessionIdException;
import gg.wil.imposter.lobby.Lobby;
import gg.wil.imposter.lobby.repo.LobbyRepo;
import gg.wil.imposter.session.Player;
import gg.wil.imposter.session.repo.SessionRepo;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.util.UUID;

@Service
@ConditionalOnExpression("'${app.server.mode}'.toUpperCase() == 'BOTH' || '${app.server.mode}'.toUpperCase() == 'GAME_SERVER'")
public class LocalLobbyService implements LobbyService {

    private final LobbyRepo lobbyRepo;
    private final SessionRepo sessionRepo;

    public LocalLobbyService(LobbyRepo lobbyRepo, SessionRepo sessionRepo) {
        this.lobbyRepo = lobbyRepo;
        this.sessionRepo = sessionRepo;
    }

    @Override
    public final Mono<LobbyResponse> createLobby() {
        Player host = Player.create("");
        Lobby lobby = Lobby.create(this.lobbyRepo, this.sessionRepo, host);
        if(lobby == null) return Mono.error(new CantCreateLobbyException());
        lobbyRepo.addLobby(lobby);
        sessionRepo.addSession(host, lobby);
        String websocketURL = Config.WEBSOCKET_URL + lobby.getLobbyCode();
        return Mono.just(new LobbyResponse(lobby.getLobbyCode(), host.getSessionID().toString(), host.getUUID().toString(), websocketURL));
    }

    @Override
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
        sessionRepo.addSession(player, lobby);
        String websocketURL = Config.WEBSOCKET_URL + lobby.getLobbyCode();
        return Mono.just(new LobbyResponse(lobby.getLobbyCode(), player.getSessionID().toString(), player.getUUID().toString(), websocketURL));
    }

    @Override
    public final Mono<LobbyResponse> rejoinLobby(UUID sessionID) {
        Player player = this.sessionRepo.getSession(sessionID);
        if(player == null) return Mono.error(new InvalidSessionIdException());

        Lobby lobby = this.sessionRepo.getLobby(player.getUUID());
        if(lobby == null) return Mono.error(new InvalidSessionIdException());

        String websocketURL = Config.WEBSOCKET_URL + lobby.getLobbyCode();
        return Mono.just(new LobbyResponse(lobby.getLobbyCode(), player.getSessionID().toString(), player.getUUID().toString(), websocketURL));
    }

    @Override
    public final void playerConnected(String lobbyCode, UUID playerID, WebSocketSession session, Sinks.Many<String> outgoingSink) {
        Lobby lobby = lobbyRepo.getLobby(lobbyCode);
        lobby.playerConnected(playerID, session, outgoingSink);
    }

    @Override
    public final Mono<Void> handleMessage(String lobbyCode, UUID playerID, String message) {
        Lobby lobby = lobbyRepo.getLobby(lobbyCode);
        if(lobby == null) return Mono.empty();
        return lobby.receiveMessage(playerID, message);
    }

    @Override
    public final void playerDisconnected(String lobbyCode, UUID playerID) {
        Lobby lobby = lobbyRepo.getLobby(lobbyCode);
        if(lobby == null) return;
        lobby.playerDisconnected(playerID);
    }

    @Override
    public final void checkCredentials(String lobbyCode, UUID sessionID) throws WebSocketException {
        Lobby lobby = lobbyRepo.getLobby(lobbyCode);
        if(lobby == null) throw new InvalidLobbyCodeException();

        if(sessionID == null) throw new InvalidSessionIdException();

        Player player = this.sessionRepo.getSession(sessionID);
        if(player == null) throw new InvalidSessionIdException();
        if(player.isConnected()) throw new AlreadyConnectedException();
    }
}
