package gg.wil.imposter.game.gamemode.imposter;

import gg.wil.imposter.api.messages.websocket.WebSocketReceiveMessage;
import gg.wil.imposter.api.messages.websocket.receive.*;
import gg.wil.imposter.api.messages.websocket.send.SendGameEndMessage;
import gg.wil.imposter.api.messages.websocket.send.SendGameStartMessage;
import gg.wil.imposter.api.messages.websocket.send.SendPlayerFinishedAnsweringMessage;
import gg.wil.imposter.api.messages.websocket.send.SendScoresMessage;
import gg.wil.imposter.game.Game;
import gg.wil.imposter.game.Lobby;
import gg.wil.imposter.game.Player;
import gg.wil.imposter.game.gamemode.GameMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class ImposterGameMode extends GameMode {

    private final Logger logger;
    private final int maxRounds;

    private final Set<Integer> usedQuestions = new HashSet<>();
    private int roundNumber = 0;
    private ImposterRound currentRound;

    private final Map<UUID, Integer> scores = new HashMap<>();

    public ImposterGameMode(Lobby lobby, Game game, int maxRounds) {
        super(lobby, game, maxRounds);
        this.logger = LoggerFactory.getLogger("ImposterGameMode - " + lobby.getLobbyCode());
        this.maxRounds = maxRounds;
    }

    @Override
    public void startGame() {
        logger.info("Starting game...");
        this.lobby.broadcast(new SendGameStartMessage());
        this.lobby.getPlayers().forEach(player -> scores.put(player.getUUID(), 0));
    }

    private void nextRound() {
        if(this.roundNumber >= this.maxRounds) return;
        this.roundNumber++;
        logger.info("Starting round {}", this.roundNumber);
        currentRound = new ImposterRound(this.lobby, this.game, this, this.roundNumber, this.maxRounds, 60);
    }

    public void markQuestionAsUsed(int questionID) {
        usedQuestions.add(questionID);
    }

    public boolean hasQuestionBeenUsed(int questionID) {
        return usedQuestions.contains(questionID);
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
            case ReceiveGameEndMessage receiveGameEndMessage -> handleGameEnd(receiveGameEndMessage);
            case ReceiveIntroFinishedMessage receiveIntroFinishedMessage -> handleIntroFinishedMessage(receiveIntroFinishedMessage);
            case ReceivePhaseEndMessage receivePhaseEndMessage -> handlePhaseEndMessage(receivePhaseEndMessage);
            case ReceiveRoundEndMessage receiveRoundEndMessage -> handleRoundEndMessage (receiveRoundEndMessage);
            case ReceiveScoresGetMessage receiveScoresGetMessage -> handleScoresGetMessage(receiveScoresGetMessage);
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

    private void handleGameEnd(ReceiveGameEndMessage message) {
        if(!message.getFrom().getUUID().equals(this.lobby.getHost().getUUID())) return;
        this.lobby.getPlayers().forEach(player -> player.sendMessage(new SendGameEndMessage()));
        this.game.stopGame();
    }

    private void handleIntroFinishedMessage(ReceiveIntroFinishedMessage message) {
        if(!message.getFrom().getUUID().equals(this.lobby.getHost().getUUID())) return;
        this.nextRound();
    }

    private void handlePhaseEndMessage(ReceivePhaseEndMessage message) {
        if(currentRound == null) return;
        Player from = message.getFrom();
        if(!from.getUUID().equals(lobby.getHost().getUUID())) return;
        currentRound.endPhase(false);
    }

    private void handleRoundEndMessage(ReceiveRoundEndMessage message) {
        if(!message.getFrom().getUUID().equals(this.lobby.getHost().getUUID())) return;
        this.nextRound();
    }

    private void handleScoresGetMessage(ReceiveScoresGetMessage message) {
        Player from = message.getFrom();
        from.sendMessage(new SendScoresMessage(scores, !(this.roundNumber==this.maxRounds)));
    }

    private void handleTimesUpMessage(ReceiveTimesUpMessage message) {
        if(this.currentRound == null) return;
        Player from = message.getFrom();
        if(!from.getUUID().equals(lobby.getHost().getUUID())) return;
        this.currentRound.endPhase(true);
    }

    private void handleVoteSubmit(ReceiveVoteSubmitMessage message) {
        if(this.currentRound == null) return;
        this.currentRound.receiveVote(message.getFrom().getUUID(), message.getPlayerID());
    }
}
