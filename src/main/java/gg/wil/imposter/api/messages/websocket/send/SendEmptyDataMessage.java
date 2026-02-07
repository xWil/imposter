package gg.wil.imposter.api.messages.websocket.send;

import com.google.gson.JsonObject;
import gg.wil.imposter.api.messages.websocket.WebSocketSendMessage;
import gg.wil.imposter.api.messages.websocket.WebSocketSendMessageType;

public abstract class SendEmptyDataMessage extends WebSocketSendMessage {

    public SendEmptyDataMessage(WebSocketSendMessageType type) {
        super(type);
    }

    @Override
    public String toJson() {
        JsonObject message = new JsonObject();
        message.addProperty("type", getType().toString());
        message.add("data", new JsonObject());

        return message.toString();
    }
}
