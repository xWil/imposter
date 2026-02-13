package gg.wil.imposter.api.messages.websocket.send.state;

import com.google.gson.JsonObject;
import gg.wil.imposter.api.messages.websocket.WebSocketSendMessage;
import gg.wil.imposter.api.messages.websocket.WebSocketSendMessageType;
import gg.wil.imposter.game.Game;
import gg.wil.imposter.game.Lobby;
import gg.wil.imposter.game.Player;

public abstract class SendGameStateMessage extends WebSocketSendMessage {

    protected final Player to;
    protected final Lobby lobby;
    protected final Game game;

    public SendGameStateMessage(Player to, Lobby lobby, Game game) {
        super(WebSocketSendMessageType.GAME_STATE);
        this.to = to;
        this.lobby = lobby;
        this.game = game;
    }

    protected abstract void getData(JsonObject data);

    @Override
    public String toJson() {
        JsonObject message = new JsonObject();
        message.addProperty("type", getType().toString());

        JsonObject data = new JsonObject();
        data.addProperty("lobbyState", this.lobby.getState().toString());
        if(this.lobby.getState() == Lobby.LobbyState.PLAYING) data.addProperty("gameMode", this.game.getGameMode().getMode().toString());
        getData(data);

        message.add("data", data);
        return message.toString();
    }
}
