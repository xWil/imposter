package gg.wil.imposter.websocket.messages.send;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import gg.wil.imposter.websocket.messages.WebSocketSendMessage;
import gg.wil.imposter.websocket.messages.WebSocketSendMessageType;
import gg.wil.imposter.session.Player;

public class SendIconChangeMessage extends WebSocketSendMessage {

    private final Player data;

    public SendIconChangeMessage(Player player) {
        super(WebSocketSendMessageType.ICON_CHANGE);
        this.data = player;
    }

    @Override
    public String toJson() {
        Gson gson = new GsonBuilder().registerTypeAdapter(Player.class, getPlayerTypeAdapter()).create();
        return gson.toJson(this);
    }
}
