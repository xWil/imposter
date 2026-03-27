package gg.wil.imposter.websocket.messages.receive;

import com.google.gson.JsonObject;
import gg.wil.imposter.websocket.messages.WebSocketReceiveMessage;
import gg.wil.imposter.websocket.messages.WebSocketReceiveMessageType;
import gg.wil.imposter.session.Player;

public class ReceiveScoresGetMessage extends WebSocketReceiveMessage {

    public ReceiveScoresGetMessage(Player from, JsonObject data) {
        super(WebSocketReceiveMessageType.SCORES_GET, from);
    }
}
