package gg.wil.imposter.api.messages.websocket.send;

import gg.wil.imposter.api.messages.websocket.WebSocketSendMessageType;

public class SendHostLeaveMessage extends SendEmptyDataMessage {

    public SendHostLeaveMessage() {
        super(WebSocketSendMessageType.HOST_LEAVE);
    }
}
