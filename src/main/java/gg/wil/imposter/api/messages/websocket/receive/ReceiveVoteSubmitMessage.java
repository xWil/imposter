package gg.wil.imposter.api.messages.websocket.receive;

import com.google.gson.JsonObject;
import gg.wil.imposter.api.messages.websocket.WebSocketReceiveMessage;
import gg.wil.imposter.api.messages.websocket.WebSocketReceiveMessageType;
import gg.wil.imposter.game.Player;

import java.util.UUID;

public class ReceiveVoteSubmitMessage extends WebSocketReceiveMessage {

    private final UUID playerID;

    public UUID getPlayerID() {
        return playerID;
    }

    public ReceiveVoteSubmitMessage(Player from, JsonObject data) {
        super(WebSocketReceiveMessageType.VOTE_SUBMIT, from);
        this.playerID = UUID.fromString(data.get("playerID").getAsString());
    }
}
