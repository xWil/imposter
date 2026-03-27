package gg.wil.imposter.game.gamemode.imposter;

import gg.wil.imposter.Config;
import gg.wil.imposter.websocket.messages.WebSocketReceiveMessage;
import gg.wil.imposter.websocket.messages.receive.*;
import gg.wil.imposter.websocket.messages.send.SendGameEndMessage;
import gg.wil.imposter.websocket.messages.send.SendGameStartMessage;
import gg.wil.imposter.websocket.messages.send.SendPlayerFinishedAnsweringMessage;
import gg.wil.imposter.websocket.messages.send.SendScoresMessage;
import gg.wil.imposter.game.Game;
import gg.wil.imposter.lobby.Lobby;
import gg.wil.imposter.session.Player;
import gg.wil.imposter.game.Settings;
import gg.wil.imposter.game.gamemode.GameMode;
import gg.wil.imposter.util.ImposterUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.SecureRandom;
import java.util.*;

public class ImposterGameMode extends GameMode {

    private final Logger logger;
    private final SecureRandom random;

    private final int maxRounds;
    private final int answeringPhaseDuration;
    private final int discussionPhaseDuration;
    private final int votingPhaseDuration;

    private final Set<Integer> usedQuestions = new HashSet<>();
    private int roundNumber = 0;
    private ImposterRound currentRound;

    private final Map<UUID, Integer> scores = new HashMap<>();

    public ImposterGameMode(Lobby lobby, Game game, Settings settings) {
        super(Mode.IMPOSTER, lobby, game, settings);
        this.logger = LoggerFactory.getLogger("ImposterGameMode - " + lobby.getLobbyCode());
        this.random = ImposterUtil.generateSecureRandom();

        int maxRounds = settings.getIntOrDefault("maxRounds", Config.GAME_IMPOSTER_MAX_ROUNDS_DEFAULT);
        int answeringPhaseDuration = settings.getIntOrDefault("answeringPhaseDuration", Config.GAME_IMPOSTER_ANSWERING_PHASE_DURATION_DEFAULT);
        int discussionPhaseDuration = settings.getIntOrDefault("discussionPhaseDuration", Config.GAME_IMPOSTER_DISCUSSION_PHASE_DURATION_DEFAULT);
        int votingPhaseDuration = settings.getIntOrDefault("votingPhaseDuration", Config.GAME_IMPOSTER_VOTING_PHASE_DURATION_DEFAULT);

        this.maxRounds = Math.clamp(maxRounds, Config.GAME_IMPOSTER_MAX_ROUNDS_MIN, Config.GAME_IMPOSTER_MAX_ROUNDS_MAX);
        this.answeringPhaseDuration = Math.clamp(answeringPhaseDuration, Config.GAME_IMPOSTER_ANSWERING_PHASE_DURATION_MIN, Config.GAME_IMPOSTER_ANSWERING_PHASE_DURATION_MAX);
        this.discussionPhaseDuration = Math.clamp(discussionPhaseDuration, Config.GAME_IMPOSTER_DISCUSSION_PHASE_DURATION_MIN, Config.GAME_IMPOSTER_DISCUSSION_PHASE_DURATION_MAX);
        this.votingPhaseDuration = Math.clamp(votingPhaseDuration, Config.GAME_IMPOSTER_VOTING_PHASE_DURATION_MIN, Config.GAME_IMPOSTER_VOTING_PHASE_DURATION_MAX);

        this.logger.info("Settings: maxRounds={}, answeringPhaseDuration={}, discussionPhaseDuration={}, votingPhaseDuration={}", this.maxRounds, this.answeringPhaseDuration, this.discussionPhaseDuration, this.votingPhaseDuration);
    }

    public int getMaxRounds() {
        return maxRounds;
    }

    public int getAnsweringPhaseDuration() {
        return answeringPhaseDuration;
    }

    public int getDiscussionPhaseDuration() {
        return discussionPhaseDuration;
    }

    public int getVotingPhaseDuration() {
        return votingPhaseDuration;
    }

    public int getRoundNumber() {
        return roundNumber;
    }

    public ImposterRound getCurrentRound() {
        return currentRound;
    }

    public Map<UUID, Integer> getScores() {
        return scores;
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
        currentRound = new ImposterRound(this.lobby, this.game, this, this.random, this.roundNumber, this.maxRounds);
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

    private void sendTimesUp() {
        if(this.currentRound == null) return;
        this.currentRound.endPhase(true);
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

    private void handleVoteSubmit(ReceiveVoteSubmitMessage message) {
        if(this.currentRound == null) return;
        this.currentRound.receiveVote(message.getFrom().getUUID(), message.getPlayerID());
    }
}
