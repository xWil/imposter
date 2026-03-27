package gg.wil.imposter.websocket.messages.send;

import com.google.gson.JsonObject;
import gg.wil.imposter.websocket.messages.WebSocketSendMessage;
import gg.wil.imposter.websocket.messages.WebSocketSendMessageType;

import java.util.Map;
import java.util.UUID;

public class SendScoresMessage extends WebSocketSendMessage {

    private final Map<UUID, Integer> scores;
    private final boolean hasMoreRounds;

    public SendScoresMessage(Map<UUID, Integer> scores, boolean hasMoreRounds) {
        super(WebSocketSendMessageType.SCORES);
        this.scores = scores;
        this.hasMoreRounds = hasMoreRounds;
    }

    @Override
    public String toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("type", getType().toString());

        JsonObject data = new JsonObject();
        data.addProperty("hasMoreRounds", hasMoreRounds);

        JsonObject scoresObject = new JsonObject();
        scores.forEach((uuid, score) -> scoresObject.addProperty(uuid.toString(), score));

        data.add("scores", scoresObject);
        jsonObject.add("data", data);
        return jsonObject.toString();
    }
}
