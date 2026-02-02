package gg.wil.imposter;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ImposterApplication {

    public static String WEBSOCKET_URL;

    public static void main(String[] args) {
        WEBSOCKET_URL = "ws://localhost:8080/ws/lobby/";
        SpringApplication.run(ImposterApplication.class, args);
    }

}
