package gg.wil.imposter.game.scheduler;

public class GameTask implements Runnable {

    public static final int NO_REPEATING = -1;

    private final int id;
    private final long creationTime = System.nanoTime();
    private final Runnable task;
    private volatile long period;
    private long nextRun = -1;
    private long nextRunNano = -1;
    private volatile GameTask next = null;
    private volatile boolean cancelled = false;

    public GameTask() {
        this(-1, null, NO_REPEATING);
    }

    public GameTask(final int id, final Runnable task, final long period) {
        this.id = id;
        this.task = task;
        this.period = period;
    }

    public final int getId() {
        return id;
    }

    public final long getCreationTime() {
        return creationTime;
    }

    public final long getPeriod() {
        return period;
    }

    public final void setPeriod(final long period) {
        this.period = period;
    }

    public final long getNextRun() {
        return nextRun;
    }

    public void setNextRun(final long nextRun) {
        this.nextRun = nextRun;
    }

    public final long getNextRunNano() {
        return nextRunNano;
    }

    public final void setNextRunNano(final long nextRunNano) {
        this.nextRunNano = nextRunNano;
    }

    public final GameTask getNext() {
        return next;
    }

    public final void setNext(final GameTask next) {
        this.next = next;
    }

    public final boolean isCancelled() {
        return cancelled;
    }

    public final void cancel() {
        cancelled = true;
    }

    @Override
    public void run() {
        if(task != null) task.run();
    }
}
