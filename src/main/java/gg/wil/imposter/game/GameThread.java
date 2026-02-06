package gg.wil.imposter.game;

import gg.wil.imposter.api.messages.websocket.WebSocketReceiveMessage;
import gg.wil.imposter.api.messages.websocket.receive.ReceiveGameStartMessage;
import gg.wil.imposter.api.messages.websocket.receive.ReceiveIconChangeMessage;
import gg.wil.imposter.api.messages.websocket.receive.ReceivePlayerJoinMessage;
import gg.wil.imposter.api.messages.websocket.receive.ReceivePlayerLeaveMessage;
import gg.wil.imposter.api.messages.websocket.send.*;
import gg.wil.imposter.game.gamemode.GameMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

public class GameThread extends Thread {

    private final Logger logger;
    private final Game game;
    private final Lobby lobby;

    private GameMode gameMode;

    private boolean running = true;

    public GameThread(Game game, Lobby lobby) {
        this.game = game;
        this.lobby = lobby;
        logger = LoggerFactory.getLogger("GameThread-" + lobby.getLobbyCode());
    }

    @Override
    public void run() {
        logger.info("Game thread started");
        long tickCount = 0;

        final long TARGET_TPS = 5;
        final long TARGET_TICK_TIME = 1000000000/TARGET_TPS;
        final long START_TIME = System.nanoTime();

        while(running) {
            long start = System.nanoTime();
            // calculate timeDebt
            long correctStartTime = START_TIME + (tickCount * TARGET_TICK_TIME);
            long timeDebt = Math.abs(correctStartTime - start);

            // handle game stuff
            this.tick();

            // wait for the next tick
            tickCount++;
            long tickTime = System.nanoTime()-start;
            long sleepTime = TARGET_TICK_TIME-tickTime;
            sleepTime -= timeDebt;

            if(sleepTime > 0) {
                long finishTime = System.nanoTime() + sleepTime;
                while(System.nanoTime() <= finishTime) {
                    try { Thread.sleep(1);
                    } catch (InterruptedException ignored) {}
                }
            }
        }
    }

    private void tick() {
        processMessages();
    }

    private void processMessages() {
        Set<WebSocketReceiveMessage> messages = game.getUnprocessedMessages(true);
        for(WebSocketReceiveMessage message : messages) {
            switch (message) {
                case ReceiveGameStartMessage receiveGameStartMessage -> handleGameStartMessage(receiveGameStartMessage);
                case ReceiveIconChangeMessage receiveIconChangeMessage -> handleIconChangeMessage(receiveIconChangeMessage);
                case ReceivePlayerJoinMessage receivePlayerJoinMessage -> handlePlayerJoinMessage(receivePlayerJoinMessage);
                case ReceivePlayerLeaveMessage receivePlayerLeaveMessage -> handlePlayerLeaveMessage(receivePlayerLeaveMessage);
                default -> {
                    if(gameMode != null) {
                        gameMode.handleMessage(message);
                    }
                }
            }
        }
    }

    private void handleGameStartMessage(ReceiveGameStartMessage message) {
        System.out.println("Received game start message");
        Player from = message.getFrom();
        if(from != lobby.getHost()) {
            // not host
            from.sendMessage(new SendGameStartErrorMessage(SendGameStartErrorMessage.ErrorType.NOT_HOST));
            return;
        }
        if(lobby.getPlayers().size() < 3) {
            // not enough players
            from.sendMessage(new SendGameStartErrorMessage(SendGameStartErrorMessage.ErrorType.NOT_ENOUGH_PLAYERS));
            return;
        }
        // start game
        if(this.gameMode != null) return;
        this.gameMode = message.getMode().create(lobby, game);
        this.game.setGameMode(gameMode);
        gameMode.startGame();
    }

    private void handleIconChangeMessage(ReceiveIconChangeMessage message) {
        message.getFrom().setIconData(message.getIconData());
        // TODO: broadcast icon change
    }

    private void handlePlayerJoinMessage(ReceivePlayerJoinMessage message) {
        Player from = message.getFrom();
        from.setIconData(message.getIconData());
        lobby.broadcastExcludePlayer(new SendPlayerJoinMessage(from), from.getUUID());
        from.sendMessage(new SendPlayerListMessage(lobby.getPlayers()));
    }

    private void handlePlayerLeaveMessage(ReceivePlayerLeaveMessage message) {
        Player from = message.getFrom();
        if(lobby.getHost() == from) {
            lobby.broadcastToPlayers(new SendHostLeaveMessage());
            lobby.closeLobby();
            return;
        }
        lobby.broadcastExcludePlayer(new SendPlayerLeaveMessage(from.getUUID()), from.getUUID());
    }

    public void stopGame() {
        logger.info("Stopping game thread");
        running = false;
    }
}
