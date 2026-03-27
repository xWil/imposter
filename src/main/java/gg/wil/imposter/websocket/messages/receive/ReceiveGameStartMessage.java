package gg.wil.imposter.websocket.messages.receive;

import com.google.gson.JsonObject;
import gg.wil.imposter.websocket.messages.WebSocketReceiveMessage;
import gg.wil.imposter.websocket.messages.WebSocketReceiveMessageType;
import gg.wil.imposter.exception.MessageException;
import gg.wil.imposter.exception.message.InvalidDataException;
import gg.wil.imposter.session.Player;
import gg.wil.imposter.game.Settings;
import gg.wil.imposter.game.gamemode.GameMode;

public class ReceiveGameStartMessage extends WebSocketReceiveMessage {

    private final GameMode.Mode mode;
    private final Settings settings;

    public GameMode.Mode getMode() {
        return mode;
    }

    public Settings getSettings() {
        return settings;
    }

    public ReceiveGameStartMessage(Player from, JsonObject data) throws MessageException {
        super(WebSocketReceiveMessageType.GAME_START, from);
        String gameMode = super.getString(data, "gamemode");
        try {
            this.mode = GameMode.Mode.valueOf(gameMode);
        } catch (IllegalArgumentException ex) {
            throw new InvalidDataException("Field 'gamemode' does not contain a valid value", ex);
        }
        this.settings = new Settings(super.getJsonObject(data, "settings"));
    }
}
