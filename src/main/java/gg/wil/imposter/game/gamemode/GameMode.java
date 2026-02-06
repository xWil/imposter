package gg.wil.imposter.game.gamemode;

import gg.wil.imposter.api.messages.websocket.WebSocketReceiveMessage;
import gg.wil.imposter.game.Game;
import gg.wil.imposter.game.Lobby;

public abstract class GameMode {

    protected final Lobby lobby;
    protected final Game game;

    public GameMode(Lobby lobby, Game game) {
        this.lobby = lobby;
        this.game = game;
    }

    public abstract void startGame();

    public abstract void handleMessage(WebSocketReceiveMessage message);

    public enum Mode {
        IMPOSTER(ImposterGameMode::new);

        private GameModeFactory factory;
        public GameMode create(Lobby lobby, Game game) { return factory.create(lobby, game); }

        Mode(GameModeFactory factory) {
            this.factory = factory;
        }
    }

    private interface GameModeFactory {
        GameMode create(Lobby lobby, Game game);
    }
}
