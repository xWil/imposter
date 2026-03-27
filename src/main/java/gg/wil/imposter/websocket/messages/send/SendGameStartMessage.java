package gg.wil.imposter.websocket.messages.send;

import gg.wil.imposter.websocket.messages.WebSocketSendMessageType;

public class SendGameStartMessage extends SendEmptyDataMessage {

    public SendGameStartMessage() {
        super(WebSocketSendMessageType.GAME_START);
    }
}
