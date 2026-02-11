package gg.wil.imposter.api.messages.websocket.send;

import gg.wil.imposter.api.messages.websocket.WebSocketSendMessageType;

public class SendGameEndMessage extends SendEmptyDataMessage{

    public SendGameEndMessage() {
        super(WebSocketSendMessageType.GAME_END);
    }
}
