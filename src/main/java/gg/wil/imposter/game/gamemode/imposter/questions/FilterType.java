package gg.wil.imposter.game.gamemode.imposter.questions;

import gg.wil.imposter.game.Game;
import gg.wil.imposter.game.Lobby;
import gg.wil.imposter.game.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public enum FilterType {
    NONE {
        @Override
        public FilteredQuestion filter(QuestionPair pair, Lobby lobby, Game game) {
            return new FilteredQuestion(pair.question(), pair.imposterQuestion());
        }
    },
    RANDOM_PLAYER {
        @Override
        public FilteredQuestion filter(QuestionPair pair, Lobby lobby, Game game) {
            List<Player> players = new ArrayList<>(lobby.getPlayers());
            String randomPlayer = players.get(new Random().nextInt(players.size())).getUsername();
            return new FilteredQuestion(pair.question().replace("%random_player%", randomPlayer), pair.imposterQuestion().replace("%random_player%", randomPlayer));
        }
    },
    RANDOM_PLAYER_DIFFERENT {
        @Override
        public FilteredQuestion filter(QuestionPair pair, Lobby lobby, Game game) {
            List<Player> players = new ArrayList<>(lobby.getPlayers());
            Random random = new Random();
            Player group = players.get(random.nextInt(players.size()));
            Player imposter;
            do { imposter = players.get(random.nextInt(players.size()));
            } while(imposter.equals(group));
            return new FilteredQuestion(pair.question().replace("%random_player%", group.getUsername()), pair.imposterQuestion().replace("%random_player%", imposter.getUsername()));
        }
    };

    public abstract FilteredQuestion filter(QuestionPair pair, Lobby lobby, Game game);
}
