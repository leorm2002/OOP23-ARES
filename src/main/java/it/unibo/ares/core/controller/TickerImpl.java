package it.unibo.ares.core.controller;

import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class TickerImpl implements Ticker {
    private final ScheduledExecutorService scheduler;
    private final long initialDelay;
    private final long period;
    private final Runnable tickTask;

    public TickerImpl(final Runnable tickTask, final long initialDelay, final long period) {
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
        this.tickTask = tickTask;
        this.initialDelay = initialDelay;
        this.period = period;
    }

    @Override
    public void start() {
        scheduler.scheduleAtFixedRate(tickTask, initialDelay, period, TimeUnit.SECONDS);
    }

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