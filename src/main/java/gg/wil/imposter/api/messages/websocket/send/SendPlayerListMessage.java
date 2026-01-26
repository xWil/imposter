package gg.wil.imposter.api.messages.websocket.send;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializer;
import gg.wil.imposter.api.messages.websocket.WebSocketMessageType;
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
        super(WebSocketMessageType.SEND_PLAYER_LIST);
        this.host = host;
        this.playerList = playerList;
    }

    @Override
    public String toJson() {
        Gson gson = new GsonBuilder().registerTypeAdapter(Player.class, (JsonSerializer<Player>) (src, type, context) -> {
                    JsonObject jsonObject = new JsonObject();
                    jsonObject.addProperty("uuid", src.getUUID().toString());
                    jsonObject.addProperty("username", src.getUsername());
                    return jsonObject;
                })
                .create();

        return gson.toJson(this);
    }
}
