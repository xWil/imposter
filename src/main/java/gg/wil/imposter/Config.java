package gg.wil.imposter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Config {

    public static int API_REFILL_TIME;
    public static int API_TOKEN_COUNT;

    public static int WEBSOCKET_MAX_CONNECTIONS;
    public static int WEBSOCKET_MESSAGES_PER_SECOND;
    public static int WEBSOCKET_MAX_SIZE;
    public static String WEBSOCKET_URL;

    @Value("${app.api.refill-time}")
    public void setApiRefillTime(int apiRefillTime) {
        API_REFILL_TIME = apiRefillTime;
    }

    @Value("${app.api.token-count}")
    public void setApiTokenCount(int apiTokenCount) {
        API_TOKEN_COUNT = apiTokenCount;
    }

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
