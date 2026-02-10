package gg.wil.imposter.game.gamemode.imposter;

import gg.wil.imposter.api.messages.websocket.WebSocketReceiveMessage;
import gg.wil.imposter.api.messages.websocket.receive.*;
import gg.wil.imposter.api.messages.websocket.send.SendGameStartMessage;
import gg.wil.imposter.api.messages.websocket.send.SendPlayerFinishedAnsweringMessage;
import gg.wil.imposter.game.Game;
import gg.wil.imposter.game.Lobby;
import gg.wil.imposter.game.Player;
import gg.wil.imposter.game.gamemode.GameMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class ImposterGameMode extends GameMode {

    private final Logger logger;
    private final int maxRounds;

    private int roundNumber = 0;
    private ImposterRound currentRound;

    private Map<UUID, Integer> scores = new HashMap<>();

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
        currentRound = new ImposterRound(this.lobby, this.game, this, this.roundNumber, this.maxRounds, 60);
    }

    public void incrementScore(UUID playerUUID, int amount) {
        scores.put(playerUUID, scores.getOrDefault(playerUUID, 0) + amount);
    }

    public void decrementScore(UUID playerUUID, int amount) {
        scores.put(playerUUID, scores.getOrDefault(playerUUID, 0) - amount);
    }

    @Override
    public void handleMessage(WebSocketReceiveMessage message) {
        switch (message) {
            case ReceiveAnswerSubmitMessage receiveAnswerSubmitMessage -> handleAnswerSubmit(receiveAnswerSubmitMessage);
            case ReceiveIntroFinishedMessage receiveIntroFinishedMessage -> handleIntroFinishedMessage(receiveIntroFinishedMessage);
            case ReceivePhaseEndMessage receivePhaseEndMessage -> handlePhaseEndMessage(receivePhaseEndMessage);
            case ReceiveTimesUpMessage receiveTimesUpMessage -> handleTimesUpMessage(receiveTimesUpMessage);
            case ReceiveVoteSubmitMessage receiveVoteSubmitMessage -> handleVoteSubmit(receiveVoteSubmitMessage);
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

    private void handlePhaseEndMessage(ReceivePhaseEndMessage message) {
        if(currentRound == null) return;
        Player from = message.getFrom();
        if(!from.getUUID().equals(lobby.getHost().getUUID())) return;
        currentRound.endPhase(false);
    }

    private void handleTimesUpMessage(ReceiveTimesUpMessage message) {
        if(currentRound == null) return;
        Player from = message.getFrom();
        if(!from.getUUID().equals(lobby.getHost().getUUID())) return;
        currentRound.endPhase(true);
    }

    private void handleVoteSubmit(ReceiveVoteSubmitMessage message) {
        this.currentRound.receiveVote(message.getFrom().getUUID(), message.getPlayerID());
    }
}
