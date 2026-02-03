package gg.wil.imposter.api.messages.websocket.receive;

import com.google.gson.JsonObject;
import gg.wil.imposter.api.messages.websocket.WebSocketReceiveMessage;
import gg.wil.imposter.api.messages.websocket.WebSocketReceiveMessageType;
import gg.wil.imposter.game.IconData;
import gg.wil.imposter.game.Player;

public class ReceivePlayerJoinMessage extends WebSocketReceiveMessage {

    private final IconData iconData;
    public IconData getIconData() {
        return iconData;
    }

    public ReceivePlayerJoinMessage(Player from, JsonObject json) {
        super(WebSocketReceiveMessageType.PLAYER_JOIN, from);

        String shape = json.get("shape").getAsString();
        String shapeColor = json.get("shapeColor").getAsString();
        String backgroundColor = json.get("backgroundColor").getAsString();
        String strokeColor = json.get("strokeColor").getAsString();
        int strokeSize = Math.clamp(json.get("strokeSize").getAsInt(), 0, 20);
        iconData = new IconData(shape, shapeColor, backgroundColor, strokeColor, strokeSize);
    }
}
