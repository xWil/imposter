package gg.wil.imposter.websocket.messages.send;

import com.google.gson.JsonObject;
import gg.wil.imposter.websocket.messages.WebSocketSendMessage;
import gg.wil.imposter.websocket.messages.WebSocketSendMessageType;

import java.util.UUID;

public class SendPlayerFinishedAnsweringMessage extends WebSocketSendMessage {

    private final UUID uuid;

    public UUID getPlayerID() {
        return uuid;
    }

    public SendPlayerFinishedAnsweringMessage(UUID uuid) {
        super(WebSocketSendMessageType.PLAYER_FINISHED_ANSWERING);
        this.uuid = uuid;
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
