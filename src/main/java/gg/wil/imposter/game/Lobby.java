package gg.wil.imposter.game;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import gg.wil.imposter.api.messages.websocket.WebSocketReceiveMessageType;
import gg.wil.imposter.api.messages.websocket.WebSocketSendMessage;
import gg.wil.imposter.api.messages.websocket.send.SendPlayerLeaveMessage;
import gg.wil.imposter.exception.LobbyException;
import gg.wil.imposter.exception.MessageException;
import gg.wil.imposter.exception.lobby.AlreadyInLobbyException;
import gg.wil.imposter.exception.lobby.InProgressException;
import gg.wil.imposter.exception.lobby.LobbyFullException;
import gg.wil.imposter.exception.message.InvalidDataException;
import gg.wil.imposter.exception.message.InvalidTypeException;
import gg.wil.imposter.exception.message.MissingFieldException;
import gg.wil.imposter.repo.LobbyRepo;
import gg.wil.imposter.repo.SessionRepo;
import gg.wil.imposter.util.ImposterUtil;
import gg.wil.imposter.util.Scheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.security.SecureRandom;
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
        this.logger = LoggerFactory.getLogger("Lobby - " + lobbyCode);
        this.logger.info("Lobby created with code {}", lobbyCode);
        this.lobbyCode = lobbyCode;
        this.state = LobbyState.WAITING;
        this.host = host;
        this.game = new Game(this);

        // If the host doesn't connect to the websocket within 5 seconds, delete the lobby
        Scheduler.INSTANCE.runTaskLater(() -> {
            if(host.isConnected()) return;
            this.logger.info("Lobby {} timed out due to the host not connecting", lobbyCode);
            this.game.stopGame();
        }, 5000);
    }

    public final String getLobbyCode() {
        return this.lobbyCode;
    }

    public final LobbyState getState() {
        return this.state;
    }

    public final void setState(LobbyState state) {
        this.state = state;
    }

    public final Player getHost() {
        return this.host;
    }

    public Collection<Player> getPlayers() {
        return this.players.values();
    }

    public Collection<Player> getConnectedPlayers() {
        return this.players.values().stream().filter(Player::isConnected).toList();
    }

    public Player getPlayer(UUID uuid) {
        return this.players.get(uuid);
    }

    public boolean hasPlayer(UUID uuid) {
        return this.players.containsKey(uuid);
    }

    public final void addPlayer(Player player) throws LobbyException {
        if(this.state != LobbyState.WAITING) throw new InProgressException(this.lobbyCode);
        if(this.players.size() >= 8) throw new LobbyFullException(this.lobbyCode);
        if(this.players.containsKey(player.getUUID())) throw new AlreadyInLobbyException(this.lobbyCode);

        this.players.put(player.getUUID(), player);
        // remove player from the lobby if they don't connect to the websocket within 5 seconds
        Scheduler.INSTANCE.runTaskLater(() -> {
            if(player.isConnected()) return;
            this.players.remove(player.getUUID());
        }, 5000);
    }

    public final void removePlayer(UUID uuid) {
        this.players.remove(uuid);
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
                this.game.stopGame();
                return;
            } else return;
        }
        logger.info("Player {} disconnected from the lobby", player.getUUID());
        player.playerDisconnected();
        if(this.state != LobbyState.PLAYING) {
            removePlayer(playerID);
            SessionRepo.getInstance().removeSession(player);
            broadcast(new SendPlayerLeaveMessage(playerID));
        }
    }

    public final Mono<Void> receiveMessage(UUID playerID, String message) {
        if(!players.containsKey(playerID) && !playerID.equals(host.getUUID())) return Mono.empty();
        try {
            // parse message to JSON
            JsonObject object = JsonParser.parseString(message).getAsJsonObject();

            // check if the 'type' field is present and valid
            if(!object.has("type")) throw new MissingFieldException("Message is missing field: 'type'");
            JsonElement typeField = object.get("type");
            if(!typeField.isJsonPrimitive() || !typeField.getAsJsonPrimitive().isString()) throw new InvalidTypeException("Field 'type' is of an invalid type, should be of type STRING");
            String typeString = typeField.getAsString().trim();

            WebSocketReceiveMessageType type;
            try {
                type = WebSocketReceiveMessageType.valueOf(typeString);
            } catch (IllegalArgumentException ex) {
                throw new InvalidDataException("type", ex);
            }

            // check if the 'data' field is present and valid
            if(!object.has("data")) throw new MissingFieldException("Message is missing field: 'data'");
            if(!object.get("data").isJsonObject()) throw new InvalidTypeException("Field 'data' is of an invalid type, should be of type object");
            JsonObject data = object.getAsJsonObject("data");

            // check if player is in the lobby
            Player player = players.get(playerID);
            if(player == null) {
                if(!playerID.equals(host.getUUID())) {
                    this.logger.warn("Received message from player {} while not in the lobby", playerID);
                    return Mono.empty();
                }
                player = host;
            }

            this.game.receiveMessage(type.create(player, data));

        } catch (MessageException me) {
            this.logger.warn("Invalid message received from player {}: {}", playerID, me.getType());
            this.logger.warn("Reason: {}", me.getMessage());
        } catch (JsonSyntaxException jse) {
            this.logger.warn("Invalid JSON received from player {}: {}", playerID, jse.getMessage());
        } catch (Exception ex) {
            this.logger.error("An error occurred while processing a message from player {}", playerID, ex);
        }

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

    public final void closeLobby() {
        logger.info("Closing lobby...");
        this.state = LobbyState.ENDED;
        for(Player player : players.values()) {
            player.disconnectPlayer();
            SessionRepo.getInstance().removeSession(player);
        }
        host.disconnectPlayer();
        SessionRepo.getInstance().removeSession(host);
        LobbyRepo.getInstance().removeLobby(this.lobbyCode);
    }

    ///  STATIC

    private static final char[] allowedChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ".toCharArray();
    private static final SecureRandom lobbyRandom = ImposterUtil.generateSecureRandom();

    public static Lobby create(Player host) {
        String code = generateNewLobbyCode();
        if(code == null) return null;
        return new Lobby(code, host);
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
                code.append(allowedChars[lobbyRandom.nextInt(allowedChars.length)]);
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
