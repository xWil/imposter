package gg.wil.imposter.websocket.messages.receive;

import com.google.gson.JsonObject;
import gg.wil.imposter.websocket.messages.WebSocketReceiveMessage;
import gg.wil.imposter.websocket.messages.WebSocketReceiveMessageType;
import gg.wil.imposter.session.Player;

public class ReceiveIntroFinishedMessage extends WebSocketReceiveMessage {

    public ReceiveIntroFinishedMessage(Player from, JsonObject data) {
        super(WebSocketReceiveMessageType.INTRO_FINISHED, from);
    }
}
