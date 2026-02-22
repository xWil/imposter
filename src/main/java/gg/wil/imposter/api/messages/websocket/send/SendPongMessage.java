package gg.wil.imposter.api.messages.websocket.send;

import gg.wil.imposter.api.messages.websocket.WebSocketSendMessageType;

public class SendPongMessage extends SendEmptyDataMessage {

    public SendPongMessage() {
        super(WebSocketSendMessageType.PONG);
    }
}
