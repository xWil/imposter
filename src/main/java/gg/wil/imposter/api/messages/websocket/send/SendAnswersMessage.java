package gg.wil.imposter.api.messages.websocket.send;

import com.google.gson.JsonObject;
import gg.wil.imposter.api.messages.websocket.WebSocketSendMessage;
import gg.wil.imposter.api.messages.websocket.WebSocketSendMessageType;

import java.util.Map;
import java.util.UUID;

public class SendAnswersMessage extends WebSocketSendMessage {

    private final Map<UUID, String> answers;

    public Map<UUID, String> getAnswers() {
        return answers;
    }

    public SendAnswersMessage(Map<UUID, String> answers) {
        super(WebSocketSendMessageType.ANSWERS);
        this.answers = answers;
    }

    @Override
    public String toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("type", getType().toString());

        JsonObject data = new JsonObject();
        answers.forEach((uuid, answer) -> data.addProperty(uuid.toString(), answer));

        jsonObject.add("data", data);
        return jsonObject.toString();
    }
}
