package gg.wil.imposter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Config {

    public static String WEBSOCKET_URL;
    public static int WEBSOCKET_MAX_SIZE;

    @Value("${app.websocket-url}")
    public void setWebsocketUrl(String websocketUrl) {
        WEBSOCKET_URL = websocketUrl;
    }

    @Value("${app.websocket-max-size}")
    public void setWebsocketMaxSize(int websocketMaxSize) {
        WEBSOCKET_MAX_SIZE = websocketMaxSize;
    }
}
