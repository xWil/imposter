package gg.wil.imposter.websocket.messages.send;

import com.google.gson.JsonObject;
import gg.wil.imposter.websocket.messages.WebSocketSendMessage;
import gg.wil.imposter.websocket.messages.WebSocketSendMessageType;

import java.util.Map;
import java.util.UUID;

public class SendVotesMessage extends WebSocketSendMessage {

    private final Map<UUID, UUID> votes;
    private final UUID imposter;

    public SendVotesMessage(Map<UUID, UUID> votes, UUID imposter) {
        super(WebSocketSendMessageType.VOTES);
        this.votes = votes;
        this.imposter = imposter;
    }

    @Override
    public String toJson() {
        JsonObject message = new JsonObject();
        message.addProperty("type", getType().toString());

        JsonObject data = new JsonObject();

        JsonObject votesObject = new JsonObject();
        votes.forEach((uuid, vote) -> votesObject.addProperty(uuid.toString(), vote.toString()));
        data.add("votes", votesObject);
        data.addProperty("imposter", imposter.toString());

        message.add("data", data);
        return message.toString();
    }
}
