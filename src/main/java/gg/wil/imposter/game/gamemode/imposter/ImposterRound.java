package gg.wil.imposter.game.gamemode.imposter;

import gg.wil.imposter.api.messages.websocket.send.*;
import gg.wil.imposter.game.Game;
import gg.wil.imposter.game.Lobby;
import gg.wil.imposter.game.Player;

import java.util.*;

public class ImposterRound {

    private final Lobby lobby;
    private final Game game;
    private final ImposterGameMode gameMode;
    private final int roundNumber;
    private final int maxRounds;
    private final int time;

    private Phase phase = Phase.ANSWERING;
    private Player imposter;
    private String currentQuestion;
    private String imposterQuestion;
    private Map<UUID, String> answers = new HashMap<>();
    private Map<UUID, UUID> votes = new HashMap<>();

    public ImposterRound(Lobby lobby, Game game, ImposterGameMode gameMode, int roundNumber, int maxRounds, int time) {
        this.lobby = lobby;
        this.game = game;
        this.gameMode = gameMode;
        this.roundNumber = roundNumber;
        this.maxRounds = maxRounds;
        this.time = time;

        this.getImposter();
        this.getQuestions();
        this.sendInitialMessages();
    }

    private void getImposter() {
        Collection<Player> players = lobby.getPlayers();
        Random random = new Random();
        ArrayList<Player> playerList = new ArrayList<>(players);
        this.imposter = playerList.get(random.nextInt(playerList.size()));
    }

    private void getQuestions() {
        this.currentQuestion = "Is murder a good thing?";
        this.imposterQuestion = "Do you like spaghetti?";
    }

    private void sendInitialMessages() {
        SendQuestionMessage questionMessage = new SendQuestionMessage(this.currentQuestion, this.roundNumber, this.maxRounds, this.time);
        SendQuestionMessage imposterQuestionMessage = new SendQuestionMessage(this.imposterQuestion, this.roundNumber, this.maxRounds, this.time);
        this.lobby.getHost().sendMessage(new SendAnsweringStartMessage(this.roundNumber, this.maxRounds, this.time));
        this.lobby.broadcastExcludePlayer(questionMessage, imposter.getUUID());
        this.imposter.sendMessage(imposterQuestionMessage);
    }

    public void receiveAnswer(UUID playerUUID, String answer) {
        answers.put(playerUUID, answer);
        if(this.phase == Phase.DISCUSSING && answers.size() >= lobby.getPlayers().size()) {
            changeToDiscussingPhase();
        }
    }

    public void receiveVote(UUID playerUUID, UUID votedFor) {
        if(this.phase != Phase.VOTING) return;
        votes.put(playerUUID, votedFor);
        if(votes.size() >= lobby.getPlayers().size()) {
            endPhase(false);
        }
    }

    public void endPhase(boolean outOfTime) {
        switch(this.phase) {
            case ANSWERING -> endAnsweringPhase(outOfTime);
            case DISCUSSING -> endDiscussingPhase(outOfTime);
            case VOTING -> endVotingPhase(outOfTime);
        }
    }

    private void endAnsweringPhase(boolean outOfTime) {
        this.phase = Phase.DISCUSSING;
        if(outOfTime) {
            Set<Player> unansweredPlayers = new HashSet<>(lobby.getPlayers());
            for(UUID playerUUID : answers.keySet()) {
                unansweredPlayers.remove(lobby.getPlayer(playerUUID));
            }
            if(!unansweredPlayers.isEmpty()) {
                for(Player player : unansweredPlayers) {
                    player.sendMessage(new SendTimesUpMessage());
                }
                return;
            }
        }
        changeToDiscussingPhase();
    }

    private void changeToDiscussingPhase() {
        if(this.phase != Phase.DISCUSSING) return;
        this.lobby.getHost().sendMessage(new SendAnswersMessage(currentQuestion, 30, answers));
    }

    private void endDiscussingPhase(boolean outOfTime) {
        this.phase = Phase.VOTING;
        SendVotingStartMessage message = new SendVotingStartMessage(currentQuestion, 60, answers);
        this.lobby.broadcast(message);
    }

    private void endVotingPhase(boolean outOfTime) {
        this.phase = Phase.FINISHED;
        SendVotesMessage message = new SendVotesMessage(votes);
        if(outOfTime) {
            Set<Player> unansweredPlayers = new HashSet<>(lobby.getPlayers());
            for(UUID playerUUID : votes.keySet()) {
                unansweredPlayers.remove(lobby.getPlayer(playerUUID));
            }
            if(!unansweredPlayers.isEmpty()) {
                for(Player player : unansweredPlayers) {
                    player.sendMessage(new SendTimesUpMessage());
                }
            }
        }
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
