package gg.wil.imposter.api.messages.websocket;

public abstract class WebSocketMessage {

    protected final WebSocketMessageType type;

    public WebSocketMessageType getType() {
        return type;
    }

    public WebSocketMessage(WebSocketMessageType type) {
        this.type = type;
    }
}
