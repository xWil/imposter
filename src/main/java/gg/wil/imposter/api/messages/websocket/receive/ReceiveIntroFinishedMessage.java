package gg.wil.imposter.api.messages.websocket.receive;

import com.google.gson.JsonObject;
import gg.wil.imposter.api.messages.websocket.WebSocketReceiveMessage;
import gg.wil.imposter.api.messages.websocket.WebSocketReceiveMessageType;
import gg.wil.imposter.game.Player;

public class ReceiveIntroFinishedMessage extends WebSocketReceiveMessage {

    public ReceiveIntroFinishedMessage(Player from, JsonObject data) {
        super(WebSocketReceiveMessageType.INTRO_FINISHED, from);
    }
}
