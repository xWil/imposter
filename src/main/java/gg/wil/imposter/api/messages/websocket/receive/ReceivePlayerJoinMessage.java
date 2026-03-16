package gg.wil.imposter.api.messages.websocket.receive;

import com.google.gson.JsonObject;
import gg.wil.imposter.api.messages.websocket.WebSocketReceiveMessage;
import gg.wil.imposter.api.messages.websocket.WebSocketReceiveMessageType;
import gg.wil.imposter.exception.MessageException;
import gg.wil.imposter.game.IconData;
import gg.wil.imposter.game.Player;

public class ReceivePlayerJoinMessage extends WebSocketReceiveMessage {

    private final IconData iconData;
    public IconData getIconData() {
        return iconData;
    }

    public ReceivePlayerJoinMessage(Player from, JsonObject data) throws MessageException {
        super(WebSocketReceiveMessageType.PLAYER_JOIN, from);

        String shape = super.getString(data, "shape");
        String shapeColor = super.getString(data, "shapeColor");
        String backgroundColor = super.getString(data, "backgroundColor");
        String strokeColor = super.getString(data, "strokeColor");
        int strokeSize = super.getInt(data, "strokeSize");
        iconData = IconData.create(shape, shapeColor, backgroundColor, strokeColor, strokeSize);
    }
}
