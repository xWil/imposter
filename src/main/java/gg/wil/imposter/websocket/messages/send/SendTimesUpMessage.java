package gg.wil.imposter.websocket.messages.send;

import gg.wil.imposter.websocket.messages.WebSocketSendMessageType;

public class SendTimesUpMessage extends SendEmptyDataMessage {
    public SendTimesUpMessage() {
        super(WebSocketSendMessageType.TIMES_UP);
    }
}
