package gg.wil.imposter.websocket.messages.send.state;

import com.google.gson.JsonObject;
import gg.wil.imposter.game.Game;
import gg.wil.imposter.lobby.Lobby;
import gg.wil.imposter.session.Player;

public class SendLobbyStateMessage extends SendGameStateMessage {

    public SendLobbyStateMessage(Player to, Lobby lobby, Game game) {
        super(to, lobby, game);
    }

    @Override
    protected void getData(JsonObject data) {}
}
