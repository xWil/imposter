package gg.wil.imposter.api.messages.websocket.send;

import com.google.gson.JsonObject;
import gg.wil.imposter.api.messages.websocket.WebSocketSendMessage;
import gg.wil.imposter.api.messages.websocket.WebSocketSendMessageType;

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
        NOT_ENOUGH_PLAYERS,
        NOT_HOST;
    }
}
