package gg.wil.imposter.api.messages.websocket.send;

import com.google.gson.JsonObject;
import gg.wil.imposter.api.messages.websocket.WebSocketSendMessage;
import gg.wil.imposter.api.messages.websocket.WebSocketSendMessageType;

import java.util.Map;
import java.util.UUID;

public class SendVotingStartMessage extends WebSocketSendMessage {

    private final String question;
    private final String imposterQuestion;
    private final int time;
    private final Map<UUID, String> answers;

    public int getTime() {
        return time;
    }

    public Map<UUID, String> getAnswers() {
        return answers;
    }

    public SendVotingStartMessage(String question, String imposterQuestion, int time, Map<UUID, String> answers) {
        super(WebSocketSendMessageType.VOTING_START);
        this.question = question;
        this.imposterQuestion = imposterQuestion;
        this.time = time;
        this.answers = answers;
    }

    @Override
    public String toJson() {
        JsonObject message = new JsonObject();
        message.addProperty("type", getType().toString());

        JsonObject data = new JsonObject();
        data.addProperty("question", super.sanitizeString(this.question));
        data.addProperty("imposterQuestion", super.sanitizeString(this.imposterQuestion));
        data.addProperty("time", this.time);

        JsonObject answersObject = new JsonObject();
        this.answers.forEach((uuid, answer) -> answersObject.addProperty(uuid.toString(), super.sanitizeString(answer)));

        data.add("answers", answersObject);
        message.add("data", data);
        return message.toString();
    }
}
