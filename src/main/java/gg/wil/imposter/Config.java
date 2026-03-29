package gg.wil.imposter;

import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

@Component
public class Config {

    public static int API_REFILL_TIME;
    public static int API_TOKEN_COUNT;

    public static int GAME_IMPOSTER_ANSWERING_PHASE_DURATION_DEFAULT;
    public static int GAME_IMPOSTER_ANSWERING_PHASE_DURATION_MAX;
    public static int GAME_IMPOSTER_ANSWERING_PHASE_DURATION_MIN;
    public static int GAME_IMPOSTER_DISCUSSION_PHASE_DURATION_DEFAULT;
    public static int GAME_IMPOSTER_DISCUSSION_PHASE_DURATION_MAX;
    public static int GAME_IMPOSTER_DISCUSSION_PHASE_DURATION_MIN;
    public static int GAME_IMPOSTER_MAX_ROUNDS_DEFAULT;
    public static int GAME_IMPOSTER_MAX_ROUNDS_MAX;
    public static int GAME_IMPOSTER_MAX_ROUNDS_MIN;
    public static int GAME_IMPOSTER_VOTING_PHASE_DURATION_DEFAULT;
    public static int GAME_IMPOSTER_VOTING_PHASE_DURATION_MAX;
    public static int GAME_IMPOSTER_VOTING_PHASE_DURATION_MIN;

    public static long GAME_THREAD_SLEEP_INACCURACY;
    public static int GAME_THREAD_TPS;

    public static char[] LOBBY_CODE_ALLOWED_CHARS;
    public static int LOBBY_CODE_LENGTH;
    public static int LOBBY_CODE_MAX_ATTEMPTS;
    public static long LOBBY_TIMEOUT;

    public static String SERVER_MODE;
    public static String SERVER_PUBLIC_URL;

    public static int WEBSOCKET_MAX_CONNECTIONS;
    public static int WEBSOCKET_MAX_SIZE;
    public static int WEBSOCKET_MESSAGES_PER_SECOND;
    public static String WEBSOCKET_URL;

    public Config(Environment env) {
        API_REFILL_TIME = env.getProperty("app.api.refill-time", Integer.class, 0);
        API_TOKEN_COUNT = env.getProperty("app.api.token-count", Integer.class, 0);

        GAME_IMPOSTER_ANSWERING_PHASE_DURATION_DEFAULT = env.getProperty("app.game.imposter.answering-phase-duration.default", Integer.class, 0);
        GAME_IMPOSTER_ANSWERING_PHASE_DURATION_MAX = env.getProperty("app.game.imposter.answering-phase-duration.max", Integer.class, 0);
        GAME_IMPOSTER_ANSWERING_PHASE_DURATION_MIN = env.getProperty("app.game.imposter.answering-phase-duration.min", Integer.class, 0);
        GAME_IMPOSTER_DISCUSSION_PHASE_DURATION_DEFAULT = env.getProperty("app.game.imposter.discussion-phase-duration.default", Integer.class, 0);
        GAME_IMPOSTER_DISCUSSION_PHASE_DURATION_MAX = env.getProperty("app.game.imposter.discussion-phase-duration.max", Integer.class, 0);
        GAME_IMPOSTER_DISCUSSION_PHASE_DURATION_MIN = env.getProperty("app.game.imposter.discussion-phase-duration.min", Integer.class, 0);
        GAME_IMPOSTER_MAX_ROUNDS_DEFAULT = env.getProperty("app.game.imposter.max-rounds.default", Integer.class, 0);
        GAME_IMPOSTER_MAX_ROUNDS_MAX = env.getProperty("app.game.imposter.max-rounds.max", Integer.class, 0);
        GAME_IMPOSTER_MAX_ROUNDS_MIN = env.getProperty("app.game.imposter.max-rounds.min", Integer.class, 0);
        GAME_IMPOSTER_VOTING_PHASE_DURATION_DEFAULT = env.getProperty("app.game.imposter.voting-phase-duration.default", Integer.class, 0);
        GAME_IMPOSTER_VOTING_PHASE_DURATION_MAX = env.getProperty("app.game.imposter.voting-phase-duration.max", Integer.class, 0);
        GAME_IMPOSTER_VOTING_PHASE_DURATION_MIN = env.getProperty("app.game.imposter.voting-phase-duration.min", Integer.class, 0);

        GAME_THREAD_SLEEP_INACCURACY = env.getProperty("app.game.thread.sleep-inaccuracy", Long.class, 0L);
        GAME_THREAD_TPS = env.getProperty("app.game.thread.tps", Integer.class, 0);

        LOBBY_CODE_ALLOWED_CHARS = env.getProperty("app.lobby.code.allowed-chars", "").toCharArray();
        LOBBY_CODE_LENGTH = env.getProperty("app.lobby.code.length", Integer.class, 6);
        LOBBY_CODE_MAX_ATTEMPTS = env.getProperty("app.lobby.code.max-attempts", Integer.class, 100);
        LOBBY_TIMEOUT = env.getProperty("app.lobby.timeout", Long.class, 0L);

        SERVER_MODE = env.getProperty("app.server.mode", "");
        SERVER_PUBLIC_URL = env.getProperty("app.server.public-url", "");

        WEBSOCKET_MAX_CONNECTIONS = env.getProperty("app.websocket.max-connections", Integer.class, 0);
        WEBSOCKET_MAX_SIZE = env.getProperty("app.websocket.max-size", Integer.class, 0);
        WEBSOCKET_MESSAGES_PER_SECOND = env.getProperty("app.websocket.messages-per-second", Integer.class, 0);WEBSOCKET_URL = env.getProperty("app.websocket.url", "");
    }
}
