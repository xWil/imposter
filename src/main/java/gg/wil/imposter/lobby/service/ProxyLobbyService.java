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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.util.UUID;

@Service
@ConditionalOnExpression("'${app.server.mode}'.toUpperCase() == 'PROXY'")
public class ProxyLobbyService implements LobbyService {

    private final Logger logger;
    private final LobbyRepo lobbyRepo;
    private final SessionRepo sessionRepo;
    private final ReactiveStringRedisTemplate redis;
    private final Gson gson = new Gson();

    public ProxyLobbyService(LobbyRepo lobbyRepo, SessionRepo sessionRepo, ReactiveStringRedisTemplate redis) {
        this.logger = LoggerFactory.getLogger(ProxyLobbyService.class);
        this.lobbyRepo = lobbyRepo;
        this.sessionRepo = sessionRepo;
        this.redis = redis;
    }

    private Mono<Pair<String, String>> getHostServer() {
        return redis.keys("game_server:*")
                .collectList()
                .flatMap(keys -> {
                    // no servers available
                    if (keys.isEmpty()) {
                        this.logger.warn("Someone has tried to create a lobby, but there are no servers available");
                        return Mono.empty();
                    }

                    return redis.opsForValue().multiGet(keys)
                            .mapNotNull(values -> {
                                ServerHeartbeat bestServer = values.stream()
                                        .map(json -> gson.fromJson(json, ServerHeartbeat.class))
                                        .min(java.util.Comparator.comparingInt(ServerHeartbeat::activeLobbies))
                                        .orElse(null);
                                if (bestServer == null) {
                                    this.logger.warn("Someone has tried to create a lobby, but there are no servers available");
                                    return null;
                                }
                                return new Pair<>(bestServer.serverID(), bestServer.serverURL());
                            });
                });
    }

    @Override
    public Mono<LobbyResponse> createLobby() {
        return getHostServer().flatMap(server -> {
            String serverID = server.getLeft();
            String serverUrl = server.getRight();

            return Lobby.generateNewLobbyCodeProxy(this.lobbyRepo)
                    .switchIfEmpty(Mono.error(new CantCreateLobbyException()))
                    .flatMap(lobbyCode -> {
                        Player host = Player.create("");
                        SessionData hostData = new SessionData(host.getSessionID().toString(), host.getUUID().toString(), host.getUsername(), lobbyCode.toUpperCase());

                        LobbyData data = new LobbyData(lobbyCode, serverID, serverUrl, host.getUUID().toString(), 0, Lobby.LobbyState.WAITING.toString());

                        // send command to game server
                        ServerCommand command = new ServerCommand("CREATE", lobbyCode, host.getSessionID(), host.getUUID(), "");
                        final String channel = "server-commands:" + serverID;
                        final String websocketUrl = serverUrl + "/ws/lobby/" + lobbyCode;

                        return this.sessionRepo.addSession(hostData)
                                .then(this.lobbyRepo.addLobbyData(data))
                                .then(this.redis.convertAndSend(channel, gson.toJson(command)))
                                .thenReturn(new LobbyResponse(lobbyCode, host.getSessionID().toString(), host.getUUID().toString(), websocketUrl));
                    });
        }).switchIfEmpty(Mono.error(new CantCreateLobbyException()));
    }

    @Override
    public Mono<LobbyResponse> joinLobby(String lobbyCode, String username) {
        if (!checkUsername(username)) return Mono.error(new InvalidUsernameException(lobbyCode));

        return this.lobbyRepo.getLobbyData(lobbyCode)
                .switchIfEmpty(Mono.error(new LobbyNotFoundException(lobbyCode)))
                .flatMap(lobbyData -> {
                    if (!Lobby.LobbyState.WAITING.toString().equals(lobbyData.state())) {
                        return Mono.error(new InProgressException(lobbyCode));
                    }
                    if (lobbyData.playerCount() >= 8) {
                        return Mono.error(new LobbyFullException(lobbyCode));
                    }

                    Player player = Player.create(username);
                    SessionData sessionData = new SessionData(player.getSessionID().toString(), player.getUUID().toString(),  player.getUsername(), lobbyCode.toUpperCase());

                    final ServerCommand command = new ServerCommand("JOIN", lobbyCode, player.getSessionID(), player.getUUID(), username);
                    final String websocketURL = lobbyData.gameServerURL() + "/ws/lobby/" + lobbyCode;

                    return this.sessionRepo.addSession(sessionData)
                            .then(this.redis.convertAndSend("server-commands:" + lobbyData.serverID(), gson.toJson(command)))
                            .thenReturn(new LobbyResponse(lobbyCode, player.getSessionID().toString(), player.getUUID().toString(), websocketURL));
                });
    }

    @Override
    public Mono<LobbyResponse> rejoinLobby(UUID sessionID) {
        return this.sessionRepo.getSessionData(sessionID)
                .switchIfEmpty(Mono.error(new InvalidSessionIdException()))
                .flatMap(sessionData -> this.lobbyRepo.getLobbyData(sessionData.lobbyCode())
                         .switchIfEmpty(Mono.error(new InvalidSessionIdException()))
                         .map(lobbyData -> {
                             final String websocketURL = lobbyData.gameServerURL() + "/ws/lobby/" + lobbyData.lobbyCode();
                             return new LobbyResponse(sessionData.lobbyCode(), sessionData.sessionID(), sessionData.playerID(), websocketURL);
                         }));
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
