package gg.wil.imposter.game.gamemode.imposter;

import gg.wil.imposter.api.messages.websocket.WebSocketReceiveMessage;
import gg.wil.imposter.api.messages.websocket.receive.ReceiveAnswerSubmitMessage;
import gg.wil.imposter.api.messages.websocket.receive.ReceiveIntroFinishedMessage;
import gg.wil.imposter.api.messages.websocket.send.SendGameStartMessage;
import gg.wil.imposter.api.messages.websocket.send.SendPlayerFinishedAnsweringMessage;
import gg.wil.imposter.game.Game;
import gg.wil.imposter.game.Lobby;
import gg.wil.imposter.game.Player;
import gg.wil.imposter.game.gamemode.GameMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImposterGameMode extends GameMode {

    private final Logger logger;
    private final int maxRounds;

    private int roundNumber = 0;
    private ImposterRound currentRound;

    public ImposterGameMode(Lobby lobby, Game game, int maxRounds) {
        super(lobby, game, maxRounds);
        this.logger = LoggerFactory.getLogger("ImposterGameMode - " + lobby.getLobbyCode());
        this.maxRounds = maxRounds;
    }

    @Override
    public void startGame() {
        lobby.broadcast(new SendGameStartMessage());
    }

    private void nextRound() {
        if(this.roundNumber >= this.maxRounds) {
            // TODO: end game
            return;
        }
        this.roundNumber++;
        currentRound = new ImposterRound(this.lobby, this.game, this.roundNumber, this.maxRounds, 60);
    }

    @Override
    public void handleMessage(WebSocketReceiveMessage message) {
        switch (message) {
            case ReceiveAnswerSubmitMessage receiveAnswerSubmitMessage -> handleAnswerSubmit(receiveAnswerSubmitMessage);
            case ReceiveIntroFinishedMessage receiveIntroFinishedMessage -> handleIntroFinishedMessage(receiveIntroFinishedMessage);
            default -> {
                this.logger.warn("Unhandled message type: {}", message.getType());
            }
        }
    }

    private void handleAnswerSubmit(ReceiveAnswerSubmitMessage message) {
        if(currentRound == null) return;
        Player from = message.getFrom();
        this.currentRound.receiveAnswer(from.getUUID(), message.getAnswer());
        this.lobby.getHost().sendMessage(new SendPlayerFinishedAnsweringMessage(from.getUUID()));
    }

    private void handleIntroFinishedMessage(ReceiveIntroFinishedMessage message) {
        nextRound();
    }
}
