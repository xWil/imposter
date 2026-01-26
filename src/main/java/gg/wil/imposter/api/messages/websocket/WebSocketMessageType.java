package gg.wil.imposter.api.messages.websocket;

import gg.wil.imposter.api.messages.websocket.send.SendPlayerListMessage;

public enum WebSocketMessageType {
    SEND_PLAYER_LIST(SendPlayerListMessage.class);

    private final Class<?> messageClass;
    public Class<?> getMessageClass() { return messageClass; }

    WebSocketMessageType(Class<?> messageClass) {
        this.messageClass = messageClass;
    }
}
