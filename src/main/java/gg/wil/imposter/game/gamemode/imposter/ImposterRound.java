package gg.wil.imposter.game.gamemode.imposter;

import gg.wil.imposter.api.messages.websocket.send.*;
import gg.wil.imposter.game.Game;
import gg.wil.imposter.game.Lobby;
import gg.wil.imposter.game.Player;
import gg.wil.imposter.game.component.components.Timer;
import gg.wil.imposter.game.gamemode.imposter.questions.FilteredQuestion;
import gg.wil.imposter.game.gamemode.imposter.questions.ImposterQuestions;
import gg.wil.imposter.game.gamemode.imposter.questions.QuestionPair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.security.SecureRandom;
import java.util.*;

public class ImposterRound {

    private final Logger logger;
    private final Lobby lobby;
    private final Game game;
    private final ImposterGameMode gameMode;
    private final int roundNumber;
    private final int maxRounds;
    private final SecureRandom random;

    private Phase phase = Phase.ANSWERING;
    private Timer phaseTimer;
    private Player imposter;
    private String currentQuestion;
    private String imposterQuestion;
    private final Map<UUID, String> answers = new HashMap<>();
    private final Map<UUID, UUID> votes = new HashMap<>();

    public ImposterRound(Lobby lobby, Game game, ImposterGameMode gameMode, SecureRandom random, int roundNumber, int maxRounds) {
        this.logger = LoggerFactory.getLogger("ImposterRound - " + lobby.getLobbyCode());
        this.lobby = lobby;
        this.game = game;
        this.gameMode = gameMode;
        this.random = random;
        this.roundNumber = roundNumber;
        this.maxRounds = maxRounds;

        this.getImposter();
        this.getQuestions();
        this.sendInitialMessages();
    }

    private void getImposter() {
        Collection<Player> players = this.lobby.getConnectedPlayers();
        ArrayList<Player> playerList = new ArrayList<>(players);
        this.imposter = playerList.get(random.nextInt(playerList.size()));
    }

    private void getQuestions() {
        QuestionPair pair;
        do {
            pair = ImposterQuestions.getRandomQuestion(this.random);
        } while(this.gameMode.hasQuestionBeenUsed(pair.id()));
        this.gameMode.markQuestionAsUsed(pair.id());

        FilteredQuestion questions = pair.filterType().filter(pair, lobby, game);
        this.currentQuestion = questions.question();
        this.imposterQuestion = questions.imposterQuestion();
    }

    private void sendInitialMessages() {
        final int answeringPhaseDuration = this.gameMode.getAnsweringPhaseDuration();
        SendQuestionMessage questionMessage = new SendQuestionMessage(this.currentQuestion, this.roundNumber, this.maxRounds, answeringPhaseDuration);
        SendQuestionMessage imposterQuestionMessage = new SendQuestionMessage(this.imposterQuestion, this.roundNumber, this.maxRounds, answeringPhaseDuration);
        this.lobby.getHost().sendMessage(new SendAnsweringStartMessage(this.roundNumber, this.maxRounds, answeringPhaseDuration));
        this.lobby.broadcastExcludePlayer(questionMessage, imposter.getUUID());
        this.imposter.sendMessage(imposterQuestionMessage);

        this.phaseTimer = new Timer((answeringPhaseDuration * 1000L), () -> endPhase(true));
        this.game.getComponentManager().registerComponent(this.phaseTimer);
    }

    public Map<UUID, String> getAnswers() {
        return answers;
    }

    public String getQuestionForPlayer(Player player) {
        return player.equals(imposter) ? imposterQuestion : currentQuestion;
    }

    public Phase getPhase() {
        return phase;
    }

    public void receiveAnswer(UUID playerUUID, String answer) {
        answers.put(playerUUID, answer);

        boolean moveOn = true;
        for(Player player : this.lobby.getConnectedPlayers()) {
            if(answers.containsKey(player.getUUID())) continue;
            moveOn = false;
            break;
        }

        if(moveOn) endAnsweringPhase(false);
    }

    public void receiveVote(UUID playerUUID, UUID votedFor) {
        if(this.phase != Phase.VOTING) return;
        votes.put(playerUUID, votedFor);

        boolean moveOn = true;
        for(Player player : this.lobby.getConnectedPlayers()) {
            if(votes.containsKey(player.getUUID())) continue;
            moveOn = false;
            break;
        }

        if(moveOn) endVotingPhase(false);
    }

    public void endPhase(boolean outOfTime) {
        logger.info("Ending phase: {}", phase);
        switch(this.phase) {
            case ANSWERING -> endAnsweringPhase(outOfTime);
            case DISCUSSING -> endDiscussingPhase(outOfTime);
            case VOTING -> endVotingPhase(outOfTime);
        }
    }

    private void endAnsweringPhase(boolean outOfTime) {
        this.phase = Phase.DISCUSSING;
        if(outOfTime) {
            SendTimesUpMessage message = new SendTimesUpMessage();
            this.lobby.getHost().sendMessage(message);

            Set<Player> unansweredPlayers = new HashSet<>(this.lobby.getConnectedPlayers());
            for(UUID playerUUID : answers.keySet()) {
                unansweredPlayers.remove(lobby.getPlayer(playerUUID));
            }
            if(!unansweredPlayers.isEmpty()) {
                for(Player player : unansweredPlayers) {
                    if(!player.isConnected()) continue;
                    player.sendMessage(message);
                }
                return;
            }
        }
        changeToDiscussingPhase();
    }

    private void changeToDiscussingPhase() {
        if(this.phase != Phase.DISCUSSING) return;

        final int discussionPhaseDuration = this.gameMode.getDiscussionPhaseDuration();
        this.lobby.getHost().sendMessage(new SendAnswersMessage(currentQuestion, imposterQuestion, discussionPhaseDuration, answers));

        this.game.getComponentManager().deregisterComponent(this.phaseTimer);
        this.phaseTimer = new Timer((discussionPhaseDuration * 1000L), () -> endPhase(true));
        this.game.getComponentManager().registerComponent(this.phaseTimer);
    }

    private void endDiscussingPhase(boolean outOfTime) {
        this.phase = Phase.VOTING;
        final int votingPhaseDuration = this.gameMode.getVotingPhaseDuration();
        SendVotingStartMessage message = new SendVotingStartMessage(currentQuestion, imposterQuestion, votingPhaseDuration, answers);
        this.lobby.broadcast(message);

        this.game.getComponentManager().deregisterComponent(this.phaseTimer);
        this.phaseTimer = new Timer((votingPhaseDuration * 1000L), () -> endPhase(true));
        this.game.getComponentManager().registerComponent(this.phaseTimer);
    }

    private void endVotingPhase(boolean outOfTime) {
        this.phase = Phase.FINISHED;
        if(outOfTime) {
            SendTimesUpMessage message = new SendTimesUpMessage();
            this.lobby.getHost().sendMessage(message);

            Set<Player> unansweredPlayers = new HashSet<>(lobby.getConnectedPlayers());
            for(UUID playerUUID : votes.keySet()) {
                unansweredPlayers.remove(lobby.getPlayer(playerUUID));
            }
            if(!unansweredPlayers.isEmpty()) {
                for(Player player : unansweredPlayers) {
                    if(!player.isConnected()) continue;
                    player.sendMessage(message);
                }
            }
        }
        SendVotesMessage message = new SendVotesMessage(votes, imposter.getUUID());
        this.lobby.getHost().sendMessage(message);

        // calculate scores
        UUID imposterUUID = this.imposter.getUUID();
        for(Map.Entry<UUID, UUID> entry : votes.entrySet()) {
            UUID from = entry.getKey();
            if(from.equals(imposterUUID)) continue;
            UUID to = entry.getValue();
            if(to.equals(imposterUUID)) gameMode.incrementScore(from, 200);
            else gameMode.incrementScore(imposterUUID, 300);
        }
    }

    public enum Phase {
        ANSWERING,
        DISCUSSING,
        VOTING,
        FINISHED
    }
}
