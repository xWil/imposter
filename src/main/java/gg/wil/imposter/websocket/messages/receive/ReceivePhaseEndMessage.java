package gg.wil.imposter.websocket.messages.receive;

import com.google.gson.JsonObject;
import gg.wil.imposter.websocket.messages.WebSocketReceiveMessage;
import gg.wil.imposter.websocket.messages.WebSocketReceiveMessageType;
import gg.wil.imposter.session.Player;

public class ReceivePhaseEndMessage extends WebSocketReceiveMessage {

    public ReceivePhaseEndMessage(Player from, JsonObject data) {
        super(WebSocketReceiveMessageType.PHASE_END, from);
    }
}
