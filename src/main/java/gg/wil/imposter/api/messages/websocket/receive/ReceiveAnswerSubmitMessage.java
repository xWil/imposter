package gg.wil.imposter.api.messages.websocket.receive;

import com.google.gson.JsonObject;
import gg.wil.imposter.api.messages.websocket.WebSocketReceiveMessage;
import gg.wil.imposter.api.messages.websocket.WebSocketReceiveMessageType;
import gg.wil.imposter.exception.MessageException;
import gg.wil.imposter.game.Player;

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
