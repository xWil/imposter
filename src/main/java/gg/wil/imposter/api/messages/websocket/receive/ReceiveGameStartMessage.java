package gg.wil.imposter.api.messages.websocket.receive;

import com.google.gson.JsonObject;
import gg.wil.imposter.api.messages.websocket.WebSocketReceiveMessage;
import gg.wil.imposter.api.messages.websocket.WebSocketReceiveMessageType;
import gg.wil.imposter.game.Player;
import gg.wil.imposter.game.gamemode.GameMode;

public class ReceiveGameStartMessage extends WebSocketReceiveMessage {

    private final GameMode.Mode mode;
    public GameMode.Mode getMode() {
        return mode;
    }

    public ReceiveGameStartMessage(Player from, JsonObject data) {
        super(WebSocketReceiveMessageType.GAME_START, from);
        this.mode = GameMode.Mode.valueOf(data.get("gamemode").getAsString());
    }
}
