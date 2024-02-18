package it.unibo.ares.core.controller;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * This class implements the Ticker interface and provides a mechanism for
 * executing a task at a fixed rate.
 * It uses a ScheduledExecutorService to schedule the task.
 */
public class TickerImpl implements Ticker {
    private final ScheduledExecutorService scheduler;
    private final long initialDelay;
    private final long period;
    private final Runnable tickTask;

    /**
     * Constructs a new TickerImpl.
     *
     * @param tickTask     the task to be executed at a fixed rate
     * @param initialDelay the delay (in seconds) before the task is first executed
     * @param period       the period (in seconds) between successive executions of
     *                     the task
     */
    public TickerImpl(final Runnable tickTask, final long initialDelay, final long period) {
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
        this.tickTask = tickTask;
        this.initialDelay = initialDelay;
        this.period = period;
    }

    /**
     * Starts the execution of the task at a fixed rate.
     */
    @Override
    public void start() {
        scheduler.scheduleAtFixedRate(tickTask, initialDelay, period, TimeUnit.SECONDS);
    }

    /**
     * Stops the execution of the task. If tasks are in progress, it waits for 60
     * seconds for them to finish.
     * If tasks do not finish in 60 seconds, it attempts to stop them forcefully.
     */
    @Override
    public void stop() {
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(60, TimeUnit.SECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException ie) {
            Thread.currentThread().interrupt();
            scheduler.shutdownNow();
        }
    }
}