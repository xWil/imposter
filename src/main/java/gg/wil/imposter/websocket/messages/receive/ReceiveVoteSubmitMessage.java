package gg.wil.imposter.websocket.messages.receive;

import com.google.gson.JsonObject;
import gg.wil.imposter.websocket.messages.WebSocketReceiveMessage;
import gg.wil.imposter.websocket.messages.WebSocketReceiveMessageType;
import gg.wil.imposter.exception.message.InvalidDataException;
import gg.wil.imposter.session.Player;

import java.util.UUID;

public class ReceiveVoteSubmitMessage extends WebSocketReceiveMessage {

    private final UUID playerID;

    public UUID getPlayerID() {
        return playerID;
    }

    public ReceiveVoteSubmitMessage(Player from, JsonObject data) {
        super(WebSocketReceiveMessageType.VOTE_SUBMIT, from);
        try {
            this.playerID = UUID.fromString(super.getString(data, "playerID"));
        } catch (IllegalArgumentException ex) {
            throw new InvalidDataException("Field 'playerID' does not contain a valid UUID", ex);
        }
    }
}
