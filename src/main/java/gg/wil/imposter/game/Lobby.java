package gg.wil.imposter.game;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import gg.wil.imposter.api.messages.websocket.WebSocketReceiveMessageType;
import gg.wil.imposter.api.messages.websocket.WebSocketSendMessage;
import gg.wil.imposter.api.messages.websocket.send.SendPlayerJoinMessage;
import gg.wil.imposter.api.messages.websocket.send.SendPlayerListMessage;
import gg.wil.imposter.exception.LobbyException;
import gg.wil.imposter.exception.lobby.AlreadyInLobbyException;
import gg.wil.imposter.exception.lobby.InProgressException;
import gg.wil.imposter.repo.LobbyRepo;
import gg.wil.imposter.util.Scheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.util.Collection;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class Lobby {

    private final Logger logger;
    private final String lobbyCode;
    private LobbyState state;
    private final Player host;
    private final ConcurrentHashMap<UUID, Player> players = new ConcurrentHashMap<>();

    private final Game game;

    private Lobby(String lobbyCode, Player host) {
        logger = LoggerFactory.getLogger("Lobby-" + lobbyCode);
        logger.info("Lobby created with code {}", lobbyCode);
        this.lobbyCode = lobbyCode;
        this.state = LobbyState.WAITING;
        this.host = host;
        this.game = new Game(this);

        // If the host doesn't connect to the websocket within 5 seconds, delete the lobby
        Scheduler.INSTANCE.runTaskLater(() -> {
            if(host.isConnected()) return;
            logger.info("Lobby {} timed out due to the host not connecting", lobbyCode);
            LobbyRepo.getInstance().removeLobby(lobbyCode);
        }, 5000);
    }

    public final String getLobbyCode() {
        return this.lobbyCode;
    }

    public final LobbyState getState() {
        return this.state;
    }

    public final Player getHost() {
        return this.host;
    }

    public Collection<Player> getPlayers() {
        return this.players.values();
    }

    public boolean hasPlayer(UUID uuid) {
        return this.players.containsKey(uuid);
    }

    public final void addPlayer(Player player) throws LobbyException {
        if(this.state != LobbyState.WAITING) throw new InProgressException(this.lobbyCode);
        if(this.players.containsKey(player.getUUID())) throw new AlreadyInLobbyException(this.lobbyCode);

        this.players.put(player.getUUID(), player);
        // remove player from the lobby if they don't connect to the websocket within 5 seconds
        Scheduler.INSTANCE.runTaskLater(() -> {
            if(player.isConnected()) return;
            this.players.remove(player.getUUID());
        }, 5000);
    }

    public final void playerConnected(UUID playerID, WebSocketSession session, Sinks.Many<String> outgoingSink) {
        Player player;
        if(!this.players.containsKey(playerID)) {
            if(!host.getUUID().equals(playerID)) return;
            player = this.host;
        } else {
            player = this.players.get(playerID);
        }
        logger.info("Player {} connected to the lobby with username '{}'", player.getUUID(), player.getUsername());
        player.playerConnected(session, outgoingSink);
    }

    public final void playerDisconnected(UUID playerID) {
        Player player = players.get(playerID);
        if(player == null) {
            if(playerID.equals(host.getUUID())) {
                logger.info("Host disconnected from the lobby");
                player = host;
            } else return;
        }
        logger.info("Player {} disconnected from the lobby", player.getUUID());
        player.playerDisconnected();
    }

    public final Mono<Void> receiveMessage(UUID playerID, String message) {
        if(!players.containsKey(playerID)) return Mono.empty();
        try {
            JsonObject object = JsonParser.parseString(message).getAsJsonObject();
            String typeString = object.get("type").getAsString();
            if(typeString == null) return Mono.empty();

            WebSocketReceiveMessageType type = WebSocketReceiveMessageType.valueOf(typeString);
            JsonObject data = object.getAsJsonObject("data");
            game.receiveMessage(type.create(players.get(playerID), data));
        } catch (Exception ignored) {}
        return Mono.empty();
    }

    public final void broadcast(WebSocketSendMessage message) {
        host.sendMessage(message);
        players.values().forEach(player -> player.sendMessage(message));
    }

    public final void broadcastToPlayers(WebSocketSendMessage message) {
        players.values().forEach(player -> player.sendMessage(message));
    }

    public final void broadcastExcludePlayer(WebSocketSendMessage message, UUID exclude) {
        if(!host.getUUID().equals(exclude)) host.sendMessage(message);
        players.values().forEach(player -> {
            if(!player.getUUID().equals(exclude)) player.sendMessage(message);
        });
    }

    ///  STATIC

    private static final char[] allowedChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();

    public static Lobby create(Player host) {
        String code = generateNewLobbyCode();
        if(code == null) return null;
        return new Lobby(generateNewLobbyCode(), host);
    }

    private static String generateNewLobbyCode() {
        boolean success = false;
        StringBuilder code = new StringBuilder();
        int attempts = 0;
        while(!success) {
            if(attempts >= 100) return null;
            attempts++;

            code = new StringBuilder();
            for(int i = 0; i < 6; i++) {
                code.append(allowedChars[(int) (Math.random() * allowedChars.length)]);
            }
            success = LobbyRepo.getInstance().getLobby(code.toString()) == null;
        }
        return code.toString();
    }

    public enum LobbyState {
        WAITING,
        PLAYING,
        ENDED
    }
}
