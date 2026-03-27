package gg.wil.imposter.websocket.messages.send.state;

import com.google.gson.JsonObject;
import gg.wil.imposter.game.Game;
import gg.wil.imposter.lobby.Lobby;
import gg.wil.imposter.session.Player;
import gg.wil.imposter.game.gamemode.GameMode;
import gg.wil.imposter.game.gamemode.imposter.ImposterGameMode;
import gg.wil.imposter.game.gamemode.imposter.ImposterRound;

import java.util.Map;
import java.util.UUID;

public class SendImposterStateMessage extends SendGameStateMessage {

    public SendImposterStateMessage(Player to, Lobby lobby, Game game) {
        super(to, lobby, game);
    }

    @Override
    protected void getData(JsonObject data) {
        if(this.game.getGameMode() == null) return;
        if(this.game.getGameMode().getMode() != GameMode.Mode.IMPOSTER) return;

        ImposterGameMode gameMode = (ImposterGameMode) this.game.getGameMode();
        ImposterRound round = gameMode.getCurrentRound();

        int roundNumber = gameMode.getRoundNumber();
        Map<UUID, Integer> scores = gameMode.getScores();

        String phase = "";
        String question = "";
        Map<UUID, String> answers = null;

        if(round != null) {
            phase = round.getPhase().toString();
            question = round.getQuestionForPlayer(this.to);
            answers = round.getAnswers();
        }

        data.addProperty("round", roundNumber);
        data.addProperty("maxRounds", 3);
        JsonObject scoresObject = new JsonObject();
        if(scores != null) scores.forEach((uuid, score) -> scoresObject.addProperty(uuid.toString(), score));
        data.add("scores", scoresObject);

        // round specific
        data.addProperty("phase", phase);
        data.addProperty("question", super.sanitizeString(question));

        JsonObject answersObject = new JsonObject();
        if(answers != null) answers.forEach((uuid, answer) -> answersObject.addProperty(uuid.toString(), super.sanitizeString(answer)));
        data.add("answers", answersObject);
    }
}
