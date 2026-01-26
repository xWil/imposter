package gg.wil.imposter.api.messages.websocket;

public abstract class WebSocketReceiveMessage extends WebSocketMessage {

    public WebSocketReceiveMessage(WebSocketMessageType type) {
        super(type);
    }

    public abstract WebSocketReceiveMessage fromJson(String json);
}
