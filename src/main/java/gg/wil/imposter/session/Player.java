package gg.wil.imposter.session;

import gg.wil.imposter.game.IconData;
import gg.wil.imposter.websocket.messages.WebSocketSendMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Sinks;

import java.util.UUID;

public class Player {

    private final UUID sessionID;
    private final UUID uuid;
    private final String username;

    private IconData iconData;

    private String websocketIP;
    private WebSocketSession session;
    private Sinks.Many<String> outgoingSink;

    private Player(String username) {
        this(UUID.randomUUID(), UUID.randomUUID(), username);
    }

    private Player(UUID sessionID, UUID uuid, String username) {
        this.sessionID = sessionID;
        this.uuid = uuid;
        this.username = username;
    }

    public UUID getSessionID() {
        return this.sessionID;
    }

    public UUID getUUID() {
        return this.uuid;
    }

    public String getUsername() {
        return this.username;
    }

    public boolean isConnected() {
        return this.session != null;
    }

    public WebSocketSession getSession() {
        return this.session;
    }

    public Sinks.Many<String> getOutgoingSink() {
        return this.outgoingSink;
    }

    public void setIconData(IconData iconData) {
        this.iconData = iconData;
    }

    public IconData getIconData() {
        return this.iconData;
    }

    public void setWebsocketIP(String websocketIP) {
        this.websocketIP = websocketIP;
    }

    public String getWebsocketIP() {
        return this.websocketIP;
    }

    public void playerConnected(WebSocketSession session, Sinks.Many<String> outgoingSink) {
        this.session = session;
        this.outgoingSink = outgoingSink;
    }

    public void disconnectPlayer() {
        if(this.session == null) return;
        this.session.close().subscribe();
        playerDisconnected();
    }

    public void playerDisconnected() {
        this.session = null;
        this.outgoingSink = null;
    }

    public void sendMessage(WebSocketSendMessage message) {
        if(this.outgoingSink == null) return;
        this.outgoingSink.tryEmitNext(message.toJson());
    }

    public static Player create(String username) {
        return new Player(username);
    }

    public static Player create(UUID sessionID, UUID uuid, String username) {
        return new Player(sessionID, uuid, username);
    }
}
