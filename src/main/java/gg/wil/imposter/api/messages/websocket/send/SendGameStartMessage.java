package gg.wil.imposter.api.messages.websocket.send;

import gg.wil.imposter.api.messages.websocket.WebSocketSendMessageType;

public class SendGameStartMessage extends SendEmptyDataMessage {

    public SendGameStartMessage() {
        super(WebSocketSendMessageType.GAME_START);
    }
}
