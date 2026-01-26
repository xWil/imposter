package gg.wil.imposter.api.messages.websocket;

public abstract class WebSocketSendMessage extends WebSocketMessage {
    public WebSocketSendMessage(WebSocketMessageType type) {
        super(type);
    }

    public abstract String toJson();
}
