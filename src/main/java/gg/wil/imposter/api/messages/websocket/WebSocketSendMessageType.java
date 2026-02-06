package gg.wil.imposter.api.messages.websocket;

import gg.wil.imposter.api.messages.websocket.send.SendHostLeaveMessage;
import gg.wil.imposter.api.messages.websocket.send.SendPlayerJoinMessage;
import gg.wil.imposter.api.messages.websocket.send.SendPlayerLeaveMessage;
import gg.wil.imposter.api.messages.websocket.send.SendPlayerListMessage;

public enum WebSocketSendMessageType {
    HOST_LEAVE(SendHostLeaveMessage.class),
    PLAYER_JOIN(SendPlayerJoinMessage.class),
    PLAYER_LEAVE(SendPlayerLeaveMessage.class),
    PLAYER_LIST(SendPlayerListMessage.class);

    private final Class<?> messageClass;
    public Class<?> getMessageClass() { return messageClass; }

    WebSocketSendMessageType(Class<?> messageClass) {
        this.messageClass = messageClass;
    }
}
