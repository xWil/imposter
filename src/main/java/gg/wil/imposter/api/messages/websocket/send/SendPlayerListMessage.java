package gg.wil.imposter.api.messages.websocket.send;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import gg.wil.imposter.api.messages.websocket.WebSocketSendMessageType;
import gg.wil.imposter.api.messages.websocket.WebSocketSendMessage;
import gg.wil.imposter.game.Player;

import java.util.Collection;

public final class SendPlayerListMessage extends WebSocketSendMessage {

    private final Player host;
    private final Collection<Player> playerList;

    public Player getHost() {
        return host;
    }

    public Collection<Player> getPlayerList() {
        return playerList;
    }

    public SendPlayerListMessage(Player host, Collection<Player> playerList) {
        super(WebSocketSendMessageType.PLAYER_LIST);
        this.host = host;
        this.playerList = playerList;
    }

    @Override
    public String toJson() {
        Gson gson = new GsonBuilder().registerTypeAdapter(Player.class, getPlayerTypeAdapter()).create();
        return gson.toJson(this);
    }
}
