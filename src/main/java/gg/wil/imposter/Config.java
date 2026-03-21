package gg.wil.imposter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Config {

    public static int API_REFILL_TIME;
    public static int API_TOKEN_COUNT;

    public static long GAME_THREAD_SLEEP_INACCURACY;
    public static int GAME_THREAD_TPS;

    public static char[] LOBBY_CODE_ALLOWED_CHARS;
    public static int LOBBY_CODE_LENGTH = 6;
    public static int LOBBY_CODE_MAX_ATTEMPTS = 100;
    public static long LOBBY_TIMEOUT;

    public static int WEBSOCKET_MAX_CONNECTIONS;
    public static int WEBSOCKET_MESSAGES_PER_SECOND;
    public static int WEBSOCKET_MAX_SIZE;
    public static String WEBSOCKET_URL;

    // API
    @Value("${app.api.refill-time}")
    public void setApiRefillTime(int apiRefillTime) {
        API_REFILL_TIME = apiRefillTime;
    }

    @Value("${app.api.token-count}")
    public void setApiTokenCount(int apiTokenCount) {
        API_TOKEN_COUNT = apiTokenCount;
    }


    // GAME
    @Value("${app.game.thread.sleep-inaccuracy}")
    public void setGameThreadSleepInaccuracy(long gameThreadSleepInaccuracy) {
        GAME_THREAD_SLEEP_INACCURACY = gameThreadSleepInaccuracy;
    }

    @Value("${app.game.thread.tps}")
    public void setGameThreadTps(int gameThreadTps) {
        GAME_THREAD_TPS = gameThreadTps;
    }

    // LOBBY
    @Value("${app.lobby.code.allowed-chars}")
    public void setLobbyCodeAllowedChars(String lobbyCodeAllowedChars) {
        LOBBY_CODE_ALLOWED_CHARS = lobbyCodeAllowedChars.toCharArray();
    }

    @Value("${app.lobby.code.length}")
    public void setLobbyCodeLength(int lobbyCodeLength) {
        LOBBY_CODE_LENGTH = lobbyCodeLength;
    }

    @Value("${app.lobby.code.max-attempts}")
    public void setLobbyCodeMaxAttempts(int lobbyCodeMaxAttempts) {
        LOBBY_CODE_MAX_ATTEMPTS = lobbyCodeMaxAttempts;
    }

    @Value("${app.lobby.timeout}")
    public void setLobbyTimeout(long lobbyTimeout) {
        LOBBY_TIMEOUT = lobbyTimeout;
    }


    // WEBSOCKET
    @Value("${app.websocket.max-connections}")
    public void setWebsocketMaxConnections(int websocketMaxSize) {
        WEBSOCKET_MAX_CONNECTIONS = websocketMaxSize;
    }

    @Value("${app.websocket.messages-per-second}")
    public void setWebsocketMessagesPerSecond(int websocketMessagesPerSecond) {
        WEBSOCKET_MESSAGES_PER_SECOND = websocketMessagesPerSecond;
    }

    @Value("${app.websocket.max-size}")
    public void setWebsocketMaxSize(int websocketMaxSize) {
        WEBSOCKET_MAX_SIZE = websocketMaxSize;
    }

    @Value("${app.websocket.url}")
    public void setWebsocketUrl(String websocketUrl) {
        WEBSOCKET_URL = websocketUrl;
    }
}
