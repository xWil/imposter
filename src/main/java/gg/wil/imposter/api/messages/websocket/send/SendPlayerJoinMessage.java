package gg.wil.imposter.api.messages.websocket.send;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import gg.wil.imposter.api.messages.websocket.WebSocketSendMessageType;
import gg.wil.imposter.api.messages.websocket.WebSocketSendMessage;
import gg.wil.imposter.game.Player;

public class SendPlayerJoinMessage extends WebSocketSendMessage {

    private final Player data;
    public Player getPlayer() {
        return data;
    }

    public SendPlayerJoinMessage(Player player) {
        super(WebSocketSendMessageType.PLAYER_JOIN);
        this.data = player;
    }

    @Override
    public String toJson() {
        Gson gson = new GsonBuilder().registerTypeAdapter(Player.class, getPlayerTypeAdapter()).create();
        return gson.toJson(this);
    }
}
