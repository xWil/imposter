package gg.wil.imposter.game.gamemode.imposter;

import gg.wil.imposter.api.messages.websocket.send.SendAnsweringStartMessage;
import gg.wil.imposter.api.messages.websocket.send.SendQuestionMessage;
import gg.wil.imposter.game.Game;
import gg.wil.imposter.game.Lobby;
import gg.wil.imposter.game.Player;

import java.util.*;

public class ImposterRound {

    private final Lobby lobby;
    private final Game game;
    private final int roundNumber;
    private final int maxRounds;
    private final int time;

    private Player imposter;
    private String currentQuestion;
    private String imposterQuestion;
    private Map<UUID, String> answers = new HashMap<>();

    public ImposterRound(Lobby lobby, Game game, int roundNumber, int maxRounds, int time) {
        this.lobby = lobby;
        this.game = game;
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
    }
}
