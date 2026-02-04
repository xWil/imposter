package gg.wil.imposter.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.SchedulingConfigurer;
import org.springframework.scheduling.config.OneTimeTask;
import org.springframework.scheduling.config.ScheduledTask;
import org.springframework.scheduling.config.ScheduledTaskRegistrar;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
public class Scheduler implements SchedulingConfigurer {

    public static Scheduler INSTANCE;

    private final Logger logger = LoggerFactory.getLogger(getClass());

    private ScheduledTaskRegistrar taskRegistrar;

    @Override
    public void configureTasks(ScheduledTaskRegistrar taskRegistrar) {
        this.taskRegistrar = taskRegistrar;
    }

    public ScheduledTask runTaskLater(Runnable runnable, long delay) {
        if(taskRegistrar == null) {
            logger.warn("TaskRegistrar is null, cannot schedule task.");
            return null;
        }

        Duration duration = Duration.ofMillis(delay);
        OneTimeTask task = new OneTimeTask(runnable, duration);
        return taskRegistrar.scheduleOneTimeTask(task);
    }
}
