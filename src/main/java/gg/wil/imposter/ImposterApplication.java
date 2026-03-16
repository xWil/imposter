package gg.wil.imposter;

import gg.wil.imposter.util.Scheduler;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class ImposterApplication {

    public ImposterApplication(Scheduler scheduler) {
        Scheduler.INSTANCE = scheduler;
    }

    public static void main(String[] args) {
        SpringApplication.run(ImposterApplication.class, args);
    }

}
