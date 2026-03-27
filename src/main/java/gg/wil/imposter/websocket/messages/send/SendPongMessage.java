package gg.wil.imposter.websocket.messages.send;

import gg.wil.imposter.websocket.messages.WebSocketSendMessageType;

public class SendPongMessage extends SendEmptyDataMessage {

    public SendPongMessage() {
        super(WebSocketSendMessageType.PONG);
    }
}
