package gg.wil.imposter.util;

import gg.wil.imposter.Config;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationContext;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.ReactiveStringRedisTemplate;
import org.springframework.scheduling.config.ScheduledTask;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.concurrent.TimeoutException;

@Component
@ConditionalOnExpression("'${app.server.mode}'.toUpperCase() == 'PROXY' || '${app.server.mode}'.toUpperCase() == 'GAME_SERVER'")
public class RedisHealthMonitor {

    private final Logger logger;
    private final ReactiveStringRedisTemplate redis;
    private final ApplicationContext context;
    private ScheduledTask checkTask;
    private boolean isShuttingDown = false;

    public RedisHealthMonitor(ReactiveStringRedisTemplate redis, ApplicationContext context) {
        this.logger = LoggerFactory.getLogger(RedisHealthMonitor.class);
        this.redis = redis;
        this.context = context;
    }

    @EventListener(ApplicationReadyEvent.class)
    public void startChecking() {
        this.checkTask = Scheduler.INSTANCE.runTaskTimer(this::checkRedisConnection, Config.REDIS_CHECK_INTERVAL);
    }

    public void checkRedisConnection() {
        if(isShuttingDown) {
            this.checkTask.cancel();
            return;
        }

        this.redis.opsForValue().get("health_check_ping")
                .timeout(Duration.ofSeconds(Config.REDIS_CHECK_TIMEOUT))
                .subscribe(result -> {}, this::handleDisconnect);
    }

    private void handleDisconnect(Throwable error) {
        if(isShuttingDown) {
            this.checkTask.cancel();
            return;
        }
        this.isShuttingDown = true;

        if(error instanceof TimeoutException) {
            this.logger.error("FATAL: Redis connection timed out. Shutting down.");
        } else {
            this.logger.error("FATAL: Lost connection to Redis. Shutting down.", error);
        }

        // shutdown gracefully
        int exitCode = SpringApplication.exit(this.context, () -> 1);
        System.exit(exitCode);
    }

}
