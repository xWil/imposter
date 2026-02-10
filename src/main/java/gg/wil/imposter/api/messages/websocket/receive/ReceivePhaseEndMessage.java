package gg.wil.imposter.api.messages.websocket.receive;

import com.google.gson.JsonObject;
import gg.wil.imposter.api.messages.websocket.WebSocketReceiveMessage;
import gg.wil.imposter.api.messages.websocket.WebSocketReceiveMessageType;
import gg.wil.imposter.game.Player;

public class ReceivePhaseEndMessage extends WebSocketReceiveMessage {

    public ReceivePhaseEndMessage(Player from, JsonObject data) {
        super(WebSocketReceiveMessageType.PHASE_END, from);
    }
}
