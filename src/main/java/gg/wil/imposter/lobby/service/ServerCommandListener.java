package gg.wil.imposter.lobby.service;

import com.google.gson.Gson;
import gg.wil.imposter.exception.LobbyException;
import gg.wil.imposter.lobby.Lobby;
import gg.wil.imposter.lobby.repo.LobbyRepo;
import gg.wil.imposter.session.Player;
import gg.wil.imposter.session.repo.SessionRepo;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@ConditionalOnExpression("'${app.server.mode}'.toUpperCase() == 'GAME_SERVER'")
public class ServerCommandListener {

    private final Logger logger = LoggerFactory.getLogger(ServerCommandListener.class);
    private final Gson gson = new Gson();

    private final ReactiveStringRedisTemplate redis;
    private final LobbyRepo lobbyRepo;
    private final SessionRepo sessionRepo;

    private final String serverID = "gs-" + UUID.randomUUID().toString().substring(0, 8);

    public ServerCommandListener(ReactiveStringRedisTemplate redis, LobbyRepo lobbyRepo, SessionRepo sessionRepo) {
        this.redis = redis;
        this.lobbyRepo = lobbyRepo;
        this.sessionRepo = sessionRepo;
    }

    @PostConstruct
    public void startListening() {
        final String url = "ws://localhost:" + System.getProperty("server.port", "8080");

        this.redis.opsForHash().put("game_servers", serverID, url)
                .doOnSuccess(_ -> this.logger.info("Added game server with ID: {} and URL: {}", serverID, url))
                .subscribe();

        final String channel = "server-commands:" + serverID;
        redis.listenToChannel(channel)
                .map(message -> gson.fromJson(message.getMessage(), ServerCommand.class))
                .doOnNext(this::processCommand)
                .subscribe();
        this.logger.info("Listening for server commands on channel: {}", channel);
    }

    @PreDestroy
    public void stopListening() {
        this.redis.opsForHash().remove("game_servers", this.serverID).block();
        this.logger.info("Removed game server {} from Redis", this.serverID);
    }

    private void processCommand(ServerCommand command) {
        this.logger.info("Received server command: {} for lobby: {}", command.action(), command.lobbyCode());
        try {
            switch (command.action().toUpperCase()) {
                case "CREATE" -> this.handleCreateCommand(command);
                case "JOIN" -> this.handleJoinCommand(command);
                default -> this.logger.warn("Unknown command action: {}", command.action());
            }
        } catch (Exception e) {
            this.logger.error("Error processing command", e);
        }
    }

    private void handleCreateCommand(ServerCommand command) {
        Player host = Player.create(command.sessionID(), command.playerID(), "");
        Lobby lobby = Lobby.create(lobbyRepo, sessionRepo, command.lobbyCode(), host);
        this.sessionRepo.addSession(host, lobby);
        this.lobbyRepo.addLobby(lobby);
    }

    private void handleJoinCommand(ServerCommand command) {
        Lobby lobby = lobbyRepo.getLobby(command.lobbyCode());
        if(lobby == null) {
            this.logger.warn("Proxy told player to join lobby with code: {}, but it doesn't exist.", command.lobbyCode());
            return;
        }

        Player player = Player.create(command.sessionID(), command.playerID(), command.username());
        try {
            lobby.addPlayer(player);
        } catch (LobbyException e) {
            this.logger.error("Error adding player to lobby: {}", e.getMessage());
        }
        this.sessionRepo.addSession(player, lobby);
    }

}
