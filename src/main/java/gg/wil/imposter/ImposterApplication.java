package gg.wil.imposter;

import gg.wil.imposter.util.Scheduler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ImposterApplication {

    public static String WEBSOCKET_URL;

    public ImposterApplication(Scheduler scheduler) {
        Scheduler.INSTANCE = scheduler;
    }

    public static void main(String[] args) {
        WEBSOCKET_URL = "ws://localhost:8080/ws/lobby/";
        SpringApplication.run(ImposterApplication.class, args);
    }

}
