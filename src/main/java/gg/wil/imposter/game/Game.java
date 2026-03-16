package gg.wil.imposter.game;

import gg.wil.imposter.api.messages.websocket.WebSocketReceiveMessage;
import gg.wil.imposter.game.component.ComponentManager;
import gg.wil.imposter.game.gamemode.GameMode;
import gg.wil.imposter.game.scheduler.GameScheduler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;

public class Game {

    private final Logger logger;
    private final Lobby lobby;
    private final GameThread gameThread;
    private final GameScheduler scheduler;
    private final ComponentManager componentManager;
    private GameMode gameMode;

    private final ConcurrentLinkedQueue<WebSocketReceiveMessage> unprocessedMessages = new ConcurrentLinkedQueue<>();

    public Game(Lobby lobby) {
        this.logger = LoggerFactory.getLogger("Game - " + lobby.getLobbyCode());
        this.lobby = lobby;
        this.gameThread = new GameThread(this, lobby);
        this.scheduler = new GameScheduler(lobby.getLobbyCode());
        this.componentManager = new ComponentManager(this, lobby.getLobbyCode());
        this.gameThread.start();
    }

    public GameScheduler getScheduler() {
        return scheduler;
    }

    public ComponentManager getComponentManager() {
        return componentManager;
    }

    public void setGameMode(GameMode gameMode) {
        this.gameMode = gameMode;
    }

    public GameMode getGameMode() {
        return gameMode;
    }

    public void stopGame() {
        this.logger.info("Stopping game");
        this.scheduler.cancelAllTasks();
        this.gameThread.stopGame();
        this.lobby.closeLobby();
    }

    public void receiveMessage(WebSocketReceiveMessage message) {
        this.unprocessedMessages.add(message);
    }

    public Set<WebSocketReceiveMessage> getUnprocessedMessages(boolean clear) {
        Set<WebSocketReceiveMessage> messages = new HashSet<>();

        if (clear) {
            WebSocketReceiveMessage msg;
            while ((msg = this.unprocessedMessages.poll()) != null) {
                messages.add(msg);
            }
        } else {
            messages.addAll(this.unprocessedMessages);
        }

        return messages;
    }
}
