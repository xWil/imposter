package gg.wil.imposter.api.messages.websocket.send;

import gg.wil.imposter.api.messages.websocket.WebSocketSendMessageType;

public class SendTimesUpMessage extends SendEmptyDataMessage {
    public SendTimesUpMessage() {
        super(WebSocketSendMessageType.TIMES_UP);
    }
}
