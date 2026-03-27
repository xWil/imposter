package gg.wil.imposter.websocket.messages.receive;

import com.google.gson.JsonObject;
import gg.wil.imposter.websocket.messages.WebSocketReceiveMessage;
import gg.wil.imposter.websocket.messages.WebSocketReceiveMessageType;
import gg.wil.imposter.exception.MessageException;
import gg.wil.imposter.session.Player;

public class ReceiveAnswerSubmitMessage extends WebSocketReceiveMessage {

    private final String answer;

    public String getAnswer() {
        return answer;
    }

    public ReceiveAnswerSubmitMessage(Player from, JsonObject data) throws MessageException {
        super(WebSocketReceiveMessageType.ANSWER_SUBMIT, from);
        this.answer = super.getString(data, "answer", 140);
    }
}
