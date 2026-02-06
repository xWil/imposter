package gg.wil.imposter.game.gamemode;

import gg.wil.imposter.api.messages.websocket.WebSocketReceiveMessage;
import gg.wil.imposter.game.Game;
import gg.wil.imposter.game.Lobby;

public class ImposterGameMode extends GameMode {

    public ImposterGameMode(Lobby lobby, Game game) {
        super(lobby, game);
    }

    @Override
    public void startGame() {
        System.out.println("Imposter game started");
    }

    @Override
    public void handleMessage(WebSocketReceiveMessage message) {
        System.out.println("Received message: " + message);
    }
}
