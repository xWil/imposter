package gg.wil.imposter.api.messages.websocket;

import gg.wil.imposter.game.Player;

public abstract class WebSocketReceiveMessage {

    private final WebSocketReceiveMessageType type;
    public WebSocketReceiveMessageType getType() {
        return type;
    }

    private final Player from;
    public Player getFrom() {
        return from;
    }

    public WebSocketReceiveMessage(WebSocketReceiveMessageType type, Player from) {
        this.type = type;
        this.from = from;
    }
}
