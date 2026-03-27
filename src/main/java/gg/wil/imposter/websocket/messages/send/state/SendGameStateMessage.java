package gg.wil.imposter.websocket.messages.send.state;

import com.google.gson.JsonObject;
import gg.wil.imposter.websocket.messages.WebSocketSendMessage;
import gg.wil.imposter.websocket.messages.WebSocketSendMessageType;
import gg.wil.imposter.game.Game;
import gg.wil.imposter.lobby.Lobby;
import gg.wil.imposter.session.Player;

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
