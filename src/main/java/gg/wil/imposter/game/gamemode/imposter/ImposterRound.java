package gg.wil.imposter.game.gamemode.imposter;

import gg.wil.imposter.api.messages.websocket.send.*;
import gg.wil.imposter.game.Game;
import gg.wil.imposter.game.Lobby;
import gg.wil.imposter.game.Player;
import gg.wil.imposter.game.gamemode.imposter.questions.ImposterQuestions;
import gg.wil.imposter.game.gamemode.imposter.questions.QuestionPair;

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
    private final Map<UUID, String> answers = new HashMap<>();
    private final Map<UUID, UUID> votes = new HashMap<>();

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
        Collection<Player> players = this.lobby.getConnectedPlayers();
        Random random = new Random();
        ArrayList<Player> playerList = new ArrayList<>(players);
        this.imposter = playerList.get(random.nextInt(playerList.size()));
    }

    private void getQuestions() {
        QuestionPair pair = ImposterQuestions.getRandomQuestion();
        while(this.gameMode.hasQuestionBeenUsed(pair.id())) {
            pair = ImposterQuestions.getRandomQuestion();
        }

        this.gameMode.markQuestionAsUsed(pair.id());
        this.currentQuestion = pair.question();
        this.imposterQuestion = pair.imposterQuestion();

        List<Player> players = new ArrayList<>(lobby.getPlayers());
        Player randomPlayer1 = null;
        if(this.currentQuestion.contains("{random_player}")) {
            randomPlayer1 = players.get(new Random().nextInt(players.size()));
            this.currentQuestion = this.currentQuestion.replace("{random_player}", randomPlayer1.getUsername());
        }
        if(this.imposterQuestion.contains("{random_player}")) {
            Player randomPlayer2 = players.get(new Random().nextInt(players.size()));
            if(randomPlayer1 != null) {
                while (randomPlayer1.equals(randomPlayer2)) {
                    randomPlayer2 = players.get(new Random().nextInt(players.size()));
                }
            }
            this.imposterQuestion = this.imposterQuestion.replace("{random_player}", randomPlayer2.getUsername());
        }
    }

    private void sendInitialMessages() {
        SendQuestionMessage questionMessage = new SendQuestionMessage(this.currentQuestion, this.roundNumber, this.maxRounds, this.time);
        SendQuestionMessage imposterQuestionMessage = new SendQuestionMessage(this.imposterQuestion, this.roundNumber, this.maxRounds, this.time);
        this.lobby.getHost().sendMessage(new SendAnsweringStartMessage(this.roundNumber, this.maxRounds, this.time));
        this.lobby.broadcastExcludePlayer(questionMessage, imposter.getUUID());
        this.imposter.sendMessage(imposterQuestionMessage);
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
        switch(this.phase) {
            case ANSWERING -> endAnsweringPhase(outOfTime);
            case DISCUSSING -> endDiscussingPhase(outOfTime);
            case VOTING -> endVotingPhase(outOfTime);
        }
    }

    private void endAnsweringPhase(boolean outOfTime) {
        this.phase = Phase.DISCUSSING;
        if(outOfTime) {
            Set<Player> unansweredPlayers = new HashSet<>(this.lobby.getConnectedPlayers());
            for(UUID playerUUID : answers.keySet()) {
                unansweredPlayers.remove(lobby.getPlayer(playerUUID));
            }
            if(!unansweredPlayers.isEmpty()) {
                for(Player player : unansweredPlayers) {
                    if(!player.isConnected()) continue;
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
        if(outOfTime) {
            Set<Player> unansweredPlayers = new HashSet<>(lobby.getConnectedPlayers());
            for(UUID playerUUID : votes.keySet()) {
                unansweredPlayers.remove(lobby.getPlayer(playerUUID));
            }
            if(!unansweredPlayers.isEmpty()) {
                for(Player player : unansweredPlayers) {
                    if(!player.isConnected()) continue;
                    player.sendMessage(new SendTimesUpMessage());
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
