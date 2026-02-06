package gg.wil.imposter.api.messages.websocket.send;

import com.google.gson.JsonObject;
import gg.wil.imposter.api.messages.websocket.WebSocketSendMessage;
import gg.wil.imposter.api.messages.websocket.WebSocketSendMessageType;

import java.util.UUID;

public class SendPlayerLeaveMessage extends WebSocketSendMessage {

    private final UUID uuid;

    public SendPlayerLeaveMessage(UUID playerID) {
        super(WebSocketSendMessageType.PLAYER_LEAVE);
        this.uuid = playerID;
    }

    @Override
    public String toJson() {
        JsonObject message = new JsonObject();
        message.addProperty("type", getType().toString());

        JsonObject data = new JsonObject();
        data.addProperty("uuid", uuid.toString());

        message.add("data", data);
        return message.toString();
    }
}
