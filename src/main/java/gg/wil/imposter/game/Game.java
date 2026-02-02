package gg.wil.imposter.game;

public class Game {

    private final GameThread gameThread;

    public Game() {
        this.gameThread = new GameThread(this);
        this.gameThread.start();
    }

    public void stopGame() {
        gameThread.stopGame();
    }
}
