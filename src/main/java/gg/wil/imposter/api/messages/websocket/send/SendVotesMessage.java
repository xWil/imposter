package gg.wil.imposter.api.messages.websocket.send;

import com.google.gson.JsonObject;
import gg.wil.imposter.api.messages.websocket.WebSocketSendMessage;
import gg.wil.imposter.api.messages.websocket.WebSocketSendMessageType;

import java.util.Map;
import java.util.UUID;

public class SendVotesMessage extends WebSocketSendMessage {

    private final Map<UUID, UUID> votes;

    public SendVotesMessage(Map<UUID, UUID> votes) {
        super(WebSocketSendMessageType.VOTES);
        this.votes = votes;
    }

    @Override
    public String toJson() {
        JsonObject message = new JsonObject();
        message.addProperty("type", getType().toString());

        JsonObject data = new JsonObject();

        JsonObject votesObject = new JsonObject();
        votes.forEach((uuid, vote) -> votesObject.addProperty(uuid.toString(), vote.toString()));
        data.add("votes", votesObject);

        message.add("data", data);
        return message.toString();
    }
}
