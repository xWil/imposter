package gg.wil.imposter.game;

import gg.wil.imposter.api.messages.websocket.WebSocketSendMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import reactor.core.publisher.Sinks;

import java.util.UUID;

public class Player {

    private final UUID uuid;
    private final String username;

    private WebSocketSession session;
    private Sinks.Many<String> outgoingSink;

    private Player(UUID uuid, String username) {
        this.uuid = uuid;
        this.username = username;
    }

    public UUID getUUID() {
        return this.uuid;
    }

    public String getUsername() {
        return this.username;
    }

    public WebSocketSession getSession() {
        return this.session;
    }

    public Sinks.Many<String> getOutgoingSink() {
        return this.outgoingSink;
    }

    public void playerConnected(WebSocketSession session, Sinks.Many<String> outgoingSink) {
        this.session = session;
        this.outgoingSink = outgoingSink;
    }

    public void playerDisconnected() {
        this.session = null;
        this.outgoingSink = null;
    }

    public void sendMessage(WebSocketSendMessage message) {
        this.outgoingSink.tryEmitNext(message.toJson());
    }

    public static Player create(String username) {
        return new Player(UUID.randomUUID(), username);
    }
}
