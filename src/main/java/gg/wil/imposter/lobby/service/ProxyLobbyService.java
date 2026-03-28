package gg.wil.imposter.lobby.service;

import com.google.gson.Gson;
import gg.wil.imposter.api.messages.LobbyResponse;
import gg.wil.imposter.exception.lobby.*;
import gg.wil.imposter.exception.websocket.InvalidSessionIdException;
import gg.wil.imposter.lobby.Lobby;
import gg.wil.imposter.lobby.LobbyData;
import gg.wil.imposter.lobby.repo.LobbyRepo;
import gg.wil.imposter.session.Player;
import gg.wil.imposter.session.SessionData;
import gg.wil.imposter.session.repo.SessionRepo;
import gg.wil.imposter.data.Pair;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.util.Map;
import java.util.UUID;

@Service
@ConditionalOnExpression("'${app.server.mode}'.toUpperCase() == 'PROXY'")
public class ProxyLobbyService implements LobbyService {

    private final LobbyRepo lobbyRepo;
    private final SessionRepo sessionRepo;
    private final ReactiveStringRedisTemplate redis;
    private final Gson gson = new Gson();

    public ProxyLobbyService(LobbyRepo lobbyRepo, SessionRepo sessionRepo, ReactiveStringRedisTemplate redis) {
        this.lobbyRepo = lobbyRepo;
        this.sessionRepo = sessionRepo;
        this.redis = redis;
    }

    private Mono<Pair<String, String>> getHostServer() {
        return redis.opsForHash().entries("game_servers")
                .collectList()
                .mapNotNull(servers -> {
                    if (servers.isEmpty()) return null; // no servers available

                    // TODO: implement load balancing
                    Map.Entry<Object, Object> first = servers.getFirst();
                    return new Pair<>(first.getKey().toString(), first.getValue().toString());
                });
    }

    @Override
    public Mono<LobbyResponse> createLobby() {
        return getHostServer().flatMap(server -> {
            String serverID = server.getLeft();
            String serverUrl = server.getRight();

            String lobbyCode = Lobby.generateNewLobbyCode(this.lobbyRepo);
            if(lobbyCode == null) return Mono.error(new CantCreateLobbyException());

            Player host = Player.create("");
            SessionData hostData = new SessionData(host.getUUID().toString(), host.getSessionID().toString(), host.getUsername(), lobbyCode.toUpperCase());
            this.sessionRepo.addSession(hostData);

            LobbyData data = new LobbyData(lobbyCode, serverID, serverUrl, host.getUUID().toString(), 0, Lobby.LobbyState.WAITING.toString());
            this.lobbyRepo.addLobbyData(data);

            // send command to game server
            ServerCommand command = new ServerCommand("CREATE", lobbyCode, host.getSessionID(), host.getUUID(), "");
            final String channel = "server-commands:" + serverID;
            final String websocketUrl = serverUrl + "/ws/lobby/" + lobbyCode;

            return redis.convertAndSend(channel, gson.toJson(command))
                    .then(Mono.just(new LobbyResponse(lobbyCode, host.getSessionID().toString(), host.getUUID().toString(), websocketUrl)));
        }).switchIfEmpty(Mono.error(new CantCreateLobbyException()));
    }

    @Override
    public Mono<LobbyResponse> joinLobby(String lobbyCode, String username) {
        if (!checkUsername(username)) return Mono.error(new InvalidUsernameException(lobbyCode));

        LobbyData lobbyData = this.lobbyRepo.getLobbyData(lobbyCode);
        if (lobbyData == null) return Mono.error(new LobbyNotFoundException(lobbyCode));

        if (!Lobby.LobbyState.WAITING.toString().equals(lobbyData.state())) {
            return Mono.error(new InProgressException(lobbyCode));
        }
        if (lobbyData.playerCount() >= 8) {
            return Mono.error(new LobbyFullException(lobbyCode));
        }

        Player player = Player.create(username);
        SessionData sessionData = new SessionData(player.getUUID().toString(), player.getSessionID().toString(), player.getUsername(), lobbyCode.toUpperCase());
        this.sessionRepo.addSession(sessionData);
        final ServerCommand command = new ServerCommand("JOIN", lobbyCode, player.getSessionID(), player.getUUID(), username);
        final String websocketURL = lobbyData.gameServerURL() + "/ws/lobby/" + lobbyCode;

        return this.redis.convertAndSend("server-commands:" + lobbyData.serverID(), gson.toJson(command))
                .then(Mono.just(new LobbyResponse(lobbyCode, player.getSessionID().toString(), player.getUUID().toString(), websocketURL)));
    }

    @Override
    public Mono<LobbyResponse> rejoinLobby(UUID sessionID) {
        SessionData sessionData = this.sessionRepo.getSessionData(sessionID);
        if(sessionData == null) return Mono.error(new InvalidSessionIdException());

        LobbyData lobbyData = this.lobbyRepo.getLobbyData(sessionData.lobbyCode());
        if(lobbyData == null) return Mono.error(new InvalidSessionIdException());

        final String websocketURL = lobbyData.gameServerURL() + "/ws/lobby/" + lobbyData.lobbyCode();
        return Mono.just(new LobbyResponse(sessionData.lobbyCode(), sessionData.sessionID(), sessionData.playerID(), websocketURL));
    }

    @Override
    public void playerConnected(String lobbyCode, UUID playerID, WebSocketSession session, Sinks.Many<String> outgoingSink) {
        throw new UnsupportedOperationException("ProxyLobbyService does not support playerConnected");
    }

    @Override
    public Mono<Void> handleMessage(String lobbyCode, UUID playerID, String message) {
        throw new UnsupportedOperationException("ProxyLobbyService does not support handleMessage");
    }

    @Override
    public void playerDisconnected(String lobbyCode, UUID playerID) {
        throw new UnsupportedOperationException("ProxyLobbyService does not support playerDisconnected");
    }

    @Override
    public void checkCredentials(String lobbyCode, UUID sessionID) {
        throw new UnsupportedOperationException("ProxyLobbyService does not support checkCredentials");
    }
}
