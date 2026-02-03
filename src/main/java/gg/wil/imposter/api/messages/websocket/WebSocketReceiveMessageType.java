package gg.wil.imposter.api.messages.websocket;

import com.google.gson.JsonObject;
import gg.wil.imposter.api.messages.websocket.receive.ReceiveIconChangeMessage;
import gg.wil.imposter.api.messages.websocket.receive.ReceivePlayerJoinMessage;
import gg.wil.imposter.game.Player;

public enum WebSocketReceiveMessageType {
    ICON_CHANGE(ReceiveIconChangeMessage::new),
    PLAYER_JOIN(ReceivePlayerJoinMessage::new);

    private final ReceiveMessageFactory factory;
    public WebSocketReceiveMessage create(Player from, JsonObject json) { return factory.create(from, json); }

    WebSocketReceiveMessageType(ReceiveMessageFactory factory) {
        this.factory = factory;
    }

    private interface ReceiveMessageFactory {
        WebSocketReceiveMessage create(Player from, JsonObject json);
    }
}