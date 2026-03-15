package gg.wil.imposter.api.messages.websocket.receive;

import com.google.gson.JsonObject;
import gg.wil.imposter.api.messages.websocket.WebSocketReceiveMessage;
import gg.wil.imposter.api.messages.websocket.WebSocketReceiveMessageType;
import gg.wil.imposter.exception.MessageException;
import gg.wil.imposter.game.IconData;
import gg.wil.imposter.game.Player;

public class ReceiveIconChangeMessage extends WebSocketReceiveMessage {

    private final IconData iconData;
    public IconData getIconData() {
        return iconData;
    }

    public ReceiveIconChangeMessage(Player from, JsonObject data) throws MessageException {
        super(WebSocketReceiveMessageType.ICON_CHANGE, from);

        String shape = super.getString(data, "shape");
        String shapeColor = super.getString(data, "shapeColor");
        String backgroundColor = super.getString(data, "backgroundColor");
        String strokeColor = super.getString(data, "strokeColor");
        int strokeSize = Math.clamp(super.getInt(data, "strokeSize"), 0, 20);
        iconData = new IconData(shape, shapeColor, backgroundColor, strokeColor, strokeSize);
    }
}
