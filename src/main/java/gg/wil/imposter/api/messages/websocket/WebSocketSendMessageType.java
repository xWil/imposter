package gg.wil.imposter.api.messages.websocket;

import gg.wil.imposter.api.messages.websocket.send.SendPlayerJoinMessage;
import gg.wil.imposter.api.messages.websocket.send.SendPlayerListMessage;

public enum WebSocketSendMessageType {
    PLAYER_JOIN(SendPlayerJoinMessage.class),
    PLAYER_LIST(SendPlayerListMessage.class);

    private final Class<?> messageClass;
    public Class<?> getMessageClass() { return messageClass; }

    WebSocketSendMessageType(Class<?> messageClass) {
        this.messageClass = messageClass;
    }
}
