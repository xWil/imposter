package gg.wil.imposter.websocket.messages.send;

import gg.wil.imposter.websocket.messages.WebSocketSendMessageType;

public class SendGameEndMessage extends SendEmptyDataMessage{

    public SendGameEndMessage() {
        super(WebSocketSendMessageType.GAME_END);
    }
}
