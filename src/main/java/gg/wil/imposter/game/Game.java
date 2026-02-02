package gg.wil.imposter.game;

import gg.wil.imposter.api.messages.websocket.WebSocketReceiveMessage;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;

public class Game {

    private final Lobby lobby;
    private final GameThread gameThread;

    private final CopyOnWriteArraySet<WebSocketReceiveMessage> unprocessedMessages = new CopyOnWriteArraySet<>();

    public Game(Lobby lobby) {
        this.lobby = lobby;
        this.gameThread = new GameThread(this);
        this.gameThread.start();
    }

    public void stopGame() {
        gameThread.stopGame();
    }

    public void receiveMessage(WebSocketReceiveMessage message) {
        unprocessedMessages.add(message);
    }

    public Set<WebSocketReceiveMessage> getUnprocessedMessages(boolean clear) {
        Set<WebSocketReceiveMessage> messages = new HashSet<>(unprocessedMessages);
        if(clear) unprocessedMessages.clear();
        return messages;
    }
}
