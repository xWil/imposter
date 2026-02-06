package gg.wil.imposter.api.messages.websocket;

import gg.wil.imposter.api.messages.websocket.send.*;

public enum WebSocketSendMessageType {
    GAME_START_ERROR(SendGameStartErrorMessage.class),
    HOST_LEAVE(SendHostLeaveMessage.class),
    ICON_CHANGE(SendIconChangeMessage.class),
    PLAYER_JOIN(SendPlayerJoinMessage.class),
    PLAYER_LEAVE(SendPlayerLeaveMessage.class),
    PLAYER_LIST(SendPlayerListMessage.class);

    private final Class<?> messageClass;
    public Class<?> getMessageClass() { return messageClass; }

    WebSocketSendMessageType(Class<?> messageClass) {
        this.messageClass = messageClass;
    }
}
