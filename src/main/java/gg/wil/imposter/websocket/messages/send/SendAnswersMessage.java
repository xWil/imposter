package gg.wil.imposter.websocket.messages.send;

import com.google.gson.JsonObject;
import gg.wil.imposter.websocket.messages.WebSocketSendMessage;
import gg.wil.imposter.websocket.messages.WebSocketSendMessageType;

import java.util.Map;
import java.util.UUID;

public class SendAnswersMessage extends WebSocketSendMessage {

    private final String question;
    private final String imposterQuestion;
    private final int time;
    private final Map<UUID, String> answers;

    public Map<UUID, String> getAnswers() {
        return answers;
    }

    public SendAnswersMessage(String question, String imposterQuestion, int time, Map<UUID, String> answers) {
        super(WebSocketSendMessageType.ANSWERS);
        this.question = question;
        this.imposterQuestion = imposterQuestion;
        this.time = time;
        this.answers = answers;
    }

    @Override
    public String toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("type", getType().toString());

        JsonObject data = new JsonObject();
        data.addProperty("question", super.sanitizeString(question));
        data.addProperty("imposterQuestion", super.sanitizeString(imposterQuestion));
        data.addProperty("time", time);

        JsonObject answersObject = new JsonObject();
        answers.forEach((uuid, answer) -> answersObject.addProperty(uuid.toString(), super.sanitizeString(answer)));

        data.add("answers", answersObject);
        jsonObject.add("data", data);
        return jsonObject.toString();
    }
}
