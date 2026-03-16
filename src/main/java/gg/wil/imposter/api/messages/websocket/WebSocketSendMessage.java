package gg.wil.imposter.api.messages.websocket;

import com.google.gson.JsonObject;
import com.google.gson.JsonSerializer;
import gg.wil.imposter.game.Player;
import org.springframework.web.util.HtmlUtils;

public abstract class WebSocketSendMessage {

    private final WebSocketSendMessageType type;
    public WebSocketSendMessageType getType() {
        return type;
    }

    public WebSocketSendMessage(WebSocketSendMessageType type) {
        this.type = type;
    }

    public abstract String toJson();

    protected JsonSerializer<Player> getPlayerTypeAdapter() {
        return (JsonSerializer<Player>) (src, type, context) -> {
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("uuid", src.getUUID().toString());
            jsonObject.addProperty("username", sanitizeString(src.getUsername()));
            jsonObject.add("icon", src.getIconData().toJsonElement());
            return jsonObject;
        };
    }

    protected final String sanitizeString(String string) {
        return HtmlUtils.htmlEscape(string).trim();
    }
}
