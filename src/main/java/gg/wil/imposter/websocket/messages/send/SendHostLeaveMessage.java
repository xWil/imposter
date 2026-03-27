package gg.wil.imposter.websocket.messages.send;

import gg.wil.imposter.websocket.messages.WebSocketSendMessageType;

public class SendHostLeaveMessage extends SendEmptyDataMessage {

    public SendHostLeaveMessage() {
        super(WebSocketSendMessageType.HOST_LEAVE);
    }
}
