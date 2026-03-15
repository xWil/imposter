package gg.wil.imposter.api.messages.websocket.receive;

import com.google.gson.JsonObject;
import gg.wil.imposter.api.messages.websocket.WebSocketReceiveMessage;
import gg.wil.imposter.api.messages.websocket.WebSocketReceiveMessageType;
import gg.wil.imposter.exception.MessageException;
import gg.wil.imposter.exception.message.InvalidDataException;
import gg.wil.imposter.game.Player;
import gg.wil.imposter.game.gamemode.GameMode;

public class ReceiveGameStartMessage extends WebSocketReceiveMessage {

    private final GameMode.Mode mode;
    public GameMode.Mode getMode() {
        return mode;
    }

    public ReceiveGameStartMessage(Player from, JsonObject data) throws MessageException {
        super(WebSocketReceiveMessageType.GAME_START, from);
        String gameMode = super.getString(data, "gamemode");
        try {
            this.mode = GameMode.Mode.valueOf(gameMode);
        } catch (IllegalArgumentException ex) {
            throw new InvalidDataException("gamemode", ex);
        }
    }
}
