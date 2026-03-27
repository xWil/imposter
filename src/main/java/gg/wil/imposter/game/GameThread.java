package gg.wil.imposter.game;

import gg.wil.imposter.Config;
import gg.wil.imposter.session.Player;
import gg.wil.imposter.websocket.messages.WebSocketReceiveMessage;
import gg.wil.imposter.websocket.messages.receive.*;
import gg.wil.imposter.websocket.messages.send.*;
import gg.wil.imposter.websocket.messages.send.state.SendImposterStateMessage;
import gg.wil.imposter.websocket.messages.send.state.SendLobbyStateMessage;
import gg.wil.imposter.game.gamemode.GameMode;
import gg.wil.imposter.lobby.Lobby;
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
        this.logger = LoggerFactory.getLogger("GameThread - " + lobby.getLobbyCode());
    }

    @Override
    public void run() {
        this.logger.info("Game thread started");
        long tickCount = 0;

        final long TARGET_TICK_TIME = 1_000_000_000 / Config.GAME_THREAD_TPS;
        final long START_TIME = System.nanoTime();

        while(running) {
            long start = System.nanoTime();
            // calculate timeDebt
            final long correctStartTime = START_TIME + (tickCount * TARGET_TICK_TIME);
            final long timeDebt = Math.abs(correctStartTime - start);

            // handle game stuff
            this.tick();

            // wait for the next tick
            tickCount++;
            final long tickTime = System.nanoTime()-start;
            long sleepTime = TARGET_TICK_TIME-tickTime;
            sleepTime -= timeDebt;

            if(sleepTime > 0) {
                // long sleep to avoid too many time checks
                if(sleepTime > Config.GAME_THREAD_SLEEP_INACCURACY) {
                    final long sleepFor = sleepTime - Config.GAME_THREAD_SLEEP_INACCURACY;
                    sleepTime -= sleepFor;

                    try { Thread.sleep(sleepFor/1_000_000, (int) (sleepFor % 1_000_000));
                    } catch (InterruptedException ignored) {}
                }

                // short sleep to allow for better tick accuracy
                final long finishTime = System.nanoTime() + sleepTime;
                while(System.nanoTime() <= finishTime) {
                    try { Thread.sleep(1);
                    } catch (InterruptedException ignored) {}
                }
            }
        }
    }

    private void tick() {
        this.processMessages();
        this.game.getScheduler().gameThreadTick();
        this.game.getComponentManager().tickComponents();
    }

    private void processMessages() {
        Set<WebSocketReceiveMessage> messages = game.getUnprocessedMessages(true);
        for(WebSocketReceiveMessage message : messages) {
            switch (message) {
                case ReceiveGameStartMessage receiveGameStartMessage -> handleGameStartMessage(receiveGameStartMessage);
                case ReceiveIconChangeMessage receiveIconChangeMessage -> handleIconChangeMessage(receiveIconChangeMessage);
                case ReceivePingMessage receivePingMessage -> handlePingMessage(receivePingMessage);
                case ReceivePlayerJoinMessage receivePlayerJoinMessage -> handlePlayerJoinMessage(receivePlayerJoinMessage);
                case ReceivePlayerLeaveMessage receivePlayerLeaveMessage -> handlePlayerLeaveMessage(receivePlayerLeaveMessage);
                case ReceivePlayerRejoinMessage receivePlayerRejoinMessage -> handlePlayerRejoinMessage(receivePlayerRejoinMessage);
                default -> {
                    if(this.gameMode != null) {
                        this.gameMode.handleMessage(message);
                    }
                }
            }
            this.game.getComponentManager().broadcastMessage(message);
        }
    }

    private void handleGameStartMessage(ReceiveGameStartMessage message) {
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
        if(this.gameMode != null) {
            // already started
            from.sendMessage(new SendGameStartErrorMessage(SendGameStartErrorMessage.ErrorType.ALREADY_STARTED));
            return;
        }
        // start game
        this.lobby.setState(Lobby.LobbyState.PLAYING);
        this.gameMode = message.getMode().create(lobby, game, message.getSettings());
        this.game.setGameMode(gameMode);
        this.gameMode.startGame();
    }

    private void handleIconChangeMessage(ReceiveIconChangeMessage message) {
        Player from = message.getFrom();
        from.setIconData(message.getIconData());
        this.lobby.broadcastExcludePlayer(new SendIconChangeMessage(from), from.getUUID());
    }

    private void handlePingMessage(ReceivePingMessage message) {
        message.getFrom().sendMessage(new SendPongMessage());
    }

    private void handlePlayerJoinMessage(ReceivePlayerJoinMessage message) {
        Player from = message.getFrom();
        from.setIconData(message.getIconData());
        this.lobby.broadcastExcludePlayer(new SendPlayerJoinMessage(from), from.getUUID());
        from.sendMessage(new SendPlayerListMessage(lobby.getPlayers()));
    }

    private void handlePlayerLeaveMessage(ReceivePlayerLeaveMessage message) {
        Player from = message.getFrom();
        if(this.lobby.getHost() == from) {
            this.lobby.broadcastToPlayers(new SendHostLeaveMessage());
            this.lobby.closeLobby();
            return;
        }
        this.lobby.broadcastExcludePlayer(new SendPlayerLeaveMessage(from.getUUID()), from.getUUID());
    }

    private void handlePlayerRejoinMessage(ReceivePlayerRejoinMessage message) {
        Player from = message.getFrom();
        from.sendMessage(new SendPlayerListMessage(this.lobby.getPlayers()));

        if(this.lobby.getState() == Lobby.LobbyState.WAITING) {
            from.sendMessage(new SendLobbyStateMessage(from, this.lobby, this.game));
        } else if(this.lobby.getState() == Lobby.LobbyState.PLAYING) {
            switch(this.gameMode.getMode()) {
                case IMPOSTER -> from.sendMessage(new SendImposterStateMessage(from, this.lobby, this.game));
            }
        }
    }

    public void stopGame() {
        this.logger.info("Stopping game thread");
        running = false;
    }
}
