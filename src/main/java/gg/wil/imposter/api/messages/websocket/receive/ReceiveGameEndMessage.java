package gg.wil.imposter.api.messages.websocket.receive;

import com.google.gson.JsonObject;
import gg.wil.imposter.api.messages.websocket.WebSocketReceiveMessage;
import gg.wil.imposter.api.messages.websocket.WebSocketReceiveMessageType;
import gg.wil.imposter.game.Player;

public class ReceiveGameEndMessage extends WebSocketReceiveMessage {

    public ReceiveGameEndMessage(Player from, JsonObject data) {
        super(WebSocketReceiveMessageType.GAME_END, from);
    }
}
