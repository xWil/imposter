package gg.wil.imposter.game.gamemode;

import gg.wil.imposter.api.messages.websocket.WebSocketReceiveMessage;
import gg.wil.imposter.game.Game;
import gg.wil.imposter.game.Lobby;
import gg.wil.imposter.game.gamemode.imposter.ImposterGameMode;

public abstract class GameMode {

    private final Mode mode;
    protected final Lobby lobby;
    protected final Game game;
    protected final int maxRounds;

    public GameMode(Mode mode, Lobby lobby, Game game, int maxRounds) {
        this.mode = mode;
        this.lobby = lobby;
        this.game = game;
        this.maxRounds = maxRounds;
    }

    public Mode getMode() {
        return mode;
    }

    public abstract void startGame();

    public abstract void handleMessage(WebSocketReceiveMessage message);

    public enum Mode {
        IMPOSTER(ImposterGameMode::new);

        private GameModeFactory factory;
        public GameMode create(Lobby lobby, Game game, int maxRounds) { return factory.create(lobby, game, maxRounds); }

        Mode(GameModeFactory factory) {
            this.factory = factory;
        }
    }

    private interface GameModeFactory {
        GameMode create(Lobby lobby, Game game, int maxRounds);
    }
}
