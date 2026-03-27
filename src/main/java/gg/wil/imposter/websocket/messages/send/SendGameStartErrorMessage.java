package gg.wil.imposter.websocket.messages.send;

import com.google.gson.JsonObject;
import gg.wil.imposter.websocket.messages.WebSocketSendMessage;
import gg.wil.imposter.websocket.messages.WebSocketSendMessageType;

public class SendGameStartErrorMessage extends WebSocketSendMessage {

    private final ErrorType errorType;
    public ErrorType getErrorType() {
        return errorType;
    }

    public SendGameStartErrorMessage(ErrorType errorType) {
        super(WebSocketSendMessageType.GAME_START_ERROR);
        this.errorType = errorType;
    }

    @Override
    public String toJson() {
        JsonObject message = new JsonObject();
        message.addProperty("type", getType().toString());

        JsonObject data = new JsonObject();
        data.addProperty("reason", errorType.toString());

        message.add("data", data);
        return message.toString();
    }

    public enum ErrorType {
        ALREADY_STARTED,
        NOT_ENOUGH_PLAYERS,
        NOT_HOST;
    }
}
