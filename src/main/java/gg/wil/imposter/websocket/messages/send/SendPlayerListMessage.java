package gg.wil.imposter.websocket.messages.send;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import gg.wil.imposter.websocket.messages.WebSocketSendMessageType;
import gg.wil.imposter.websocket.messages.WebSocketSendMessage;
import gg.wil.imposter.session.Player;

import java.util.Collection;

public final class SendPlayerListMessage extends WebSocketSendMessage {

    private final Collection<Player> data;
    public Collection<Player> getPlayerList() {
        return data;
    }

    public SendPlayerListMessage(Collection<Player> playerList) {
        super(WebSocketSendMessageType.PLAYER_LIST);
        this.data = playerList;
    }

    @Override
    public String toJson() {
        Gson gson = new GsonBuilder().registerTypeAdapter(Player.class, getPlayerTypeAdapter()).create();
        return gson.toJson(this);
    }
}
