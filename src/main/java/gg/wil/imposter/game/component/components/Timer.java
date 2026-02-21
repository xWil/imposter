package gg.wil.imposter.game.component.components;

import gg.wil.imposter.game.component.Component;
import gg.wil.imposter.game.component.tick.Tick;

public class Timer implements Component {

    private final long startTime;
    private final long duration;
    private final long endTime;
    private final Runnable onFinish;
    private boolean finished = false;

    public Timer() {
        this(-1, null);
    }

    public Timer(long duration, Runnable onFinish) {
        this.startTime = System.nanoTime();
        this.duration = duration;
        this.endTime = duration > 0 ? startTime + (duration * 1_000_000) : -1;
        this.onFinish = onFinish;
    }

    public long getStartTime() {
        return startTime;
    }

    public long getDuration() {
        return duration;
    }

    public long getEndTime() {
        return endTime;
    }

    public long getRemainingTime() {
        final long remainingTime = endTime - System.nanoTime();
        if(duration > 0) return remainingTime > 0 ? remainingTime : 0;
        return -1;
    }

    public long getRemainingTimeMillis() {
        final long remainingTime = getRemainingTime();
        if(remainingTime > 0) return remainingTime / 1_000_000;
        return remainingTime;
    }

    public long getElapsedTime() {
        return System.nanoTime() - startTime;
    }

    public long getElapsedTimeMillis() {
        return getElapsedTime() / 1_000_000;
    }

    @Tick
    public void tick() {
        if(endTime > 0 && !finished && System.nanoTime() >= endTime) {
            this.finished = true;
            this.onFinish.run();
        }
    }
}
