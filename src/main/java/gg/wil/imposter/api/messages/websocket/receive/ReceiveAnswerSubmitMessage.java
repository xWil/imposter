package gg.wil.imposter.api.messages.websocket.receive;

import com.google.gson.JsonObject;
import gg.wil.imposter.api.messages.websocket.WebSocketReceiveMessage;
import gg.wil.imposter.api.messages.websocket.WebSocketReceiveMessageType;
import gg.wil.imposter.game.Player;

public class ReceiveAnswerSubmitMessage extends WebSocketReceiveMessage {

    private final String answer;

    public String getAnswer() {
        return answer;
    }

    public ReceiveAnswerSubmitMessage(Player from, JsonObject data) {
        super(WebSocketReceiveMessageType.ANSWER_SUBMIT, from);
        this.answer = data.get("answer").getAsString();
    }
}
