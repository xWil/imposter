package gg.wil.imposter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class Config {

    public static String WEBSOCKET_URL;

    @Value("${app.websocket-url}")
    public void setWebsocketUrl(String websocketUrl) {
        WEBSOCKET_URL = websocketUrl;
    }
}
