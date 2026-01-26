package gg.wil.imposter.api.messages.websocket;

import com.google.gson.JsonObject;
import com.google.gson.JsonSerializer;
import gg.wil.imposter.game.Player;

public abstract class WebSocketSendMessage extends WebSocketMessage {
    public WebSocketSendMessage(WebSocketMessageType type) {
        super(type);
    }

    public abstract String toJson();

    protected JsonSerializer<Player> getPlayerTypeAdapter() {
        return (JsonSerializer<Player>) (src, type, context) -> {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("uuid", src.getUUID().toString());
            jsonObject.addProperty("username", src.getUsername());
            return jsonObject;
        };
    }
}
