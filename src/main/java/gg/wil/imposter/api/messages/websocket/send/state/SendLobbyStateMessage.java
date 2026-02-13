package gg.wil.imposter.api.messages.websocket.send.state;

import com.google.gson.JsonObject;
import gg.wil.imposter.game.Game;
import gg.wil.imposter.game.Lobby;
import gg.wil.imposter.game.Player;

public class SendLobbyStateMessage extends SendGameStateMessage {

    public SendLobbyStateMessage(Player to, Lobby lobby, Game game) {
        super(to, lobby, game);
    }

    @Override
    protected void getData(JsonObject data) {}
}
