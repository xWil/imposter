package gg.wil.imposter.websocket.messages.send;

import com.google.gson.JsonObject;
import gg.wil.imposter.websocket.messages.WebSocketSendMessage;
import gg.wil.imposter.websocket.messages.WebSocketSendMessageType;

public class SendQuestionMessage extends WebSocketSendMessage {

    private final String question;
    private final int roundNumber;
    private final int maxRounds;
    private final int time;

    public String getQuestion() {
        return question;
    }

    public int getRoundNumber() {
        return roundNumber;
    }

    public int getMaxRounds() {
        return maxRounds;
    }

    public int getTime() {
        return time;
    }

    public SendQuestionMessage(String question, int roundNumber, int maxRounds, int time) {
        super(WebSocketSendMessageType.QUESTION);
        this.question = question;
        this.roundNumber = roundNumber;
        this.maxRounds = maxRounds;
        this.time = time;
    }

    @Override
    public String toJson() {
        JsonObject message = new JsonObject();
        message.addProperty("type", getType().toString());

        JsonObject data = new JsonObject();
        data.addProperty("question", super.sanitizeString(this.question));
        data.addProperty("roundNumber", this.roundNumber);
        data.addProperty("maxRounds", this.maxRounds);
        data.addProperty("time", this.time);

        message.add("data", data);
        return message.toString();
    }
}
