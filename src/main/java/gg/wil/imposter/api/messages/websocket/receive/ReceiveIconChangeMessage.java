package gg.wil.imposter.api.messages.websocket.receive;

import com.google.gson.JsonObject;
import gg.wil.imposter.api.messages.websocket.WebSocketReceiveMessage;
import gg.wil.imposter.api.messages.websocket.WebSocketReceiveMessageType;
import gg.wil.imposter.game.Player;

public class ReceiveIconChangeMessage extends WebSocketReceiveMessage {

    private final String shape;
    public String getShape() {
        return shape;
    }

    private final String backgroundColor;
    public String getBackgroundColor() {
        return backgroundColor;
    }

    private final String shapeColor;
    public String getShapeColor() {
        return shapeColor;
    }

    private final String strokeColor;
    public String getStrokeColor() {
        return strokeColor;
    }

    private final int strokeSize;
    public int getStrokeSize() {
        return strokeSize;
    }

    public ReceiveIconChangeMessage(Player from, JsonObject json) {
        super(WebSocketReceiveMessageType.ICON_CHANGE, from);

        this.shape = json.get("shape").getAsString();
        this.backgroundColor = json.get("backgroundColor").getAsString();
        this.shapeColor = json.get("shapeColor").getAsString();
        this.strokeColor = json.get("strokeColor").getAsString();
        this.strokeSize = Math.clamp(json.get("strokeSize").getAsInt(), 0, 20);
    }
}
