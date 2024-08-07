package it.unibo.ares.core.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

class TickerTest {
    private TickerImpl ticker;
    private static final long INITIALDELAY = 0L;
    private static final long PERIOD = 1L;

    @AfterEach
    void tearDown() throws InterruptedException {
        if (ticker != null) {
            ticker.stop();
        }
    }

    @Test
    void startShouldExecuteTaskAtFixedRate() throws InterruptedException {
        final AtomicInteger counter = new AtomicInteger(0);
        final Runnable task = counter::incrementAndGet;
        ticker = new TickerImpl(task, INITIALDELAY, PERIOD);

        ticker.start();
        // Wait for the task to be executed at least twice
        Thread.sleep(TimeUnit.SECONDS.toMillis(2 * PERIOD + 1));
        ticker.stop();
        assertTrue(counter.get() >= 2, "Task was executed less than twice");
    }

    @Test
    void stopShouldStopTaskExecution() throws InterruptedException {
        final AtomicInteger counter = new AtomicInteger(0);
        final Runnable task = counter::incrementAndGet;
        ticker = new TickerImpl(task, INITIALDELAY, PERIOD);

        ticker.start();
        // Wait for the task to be executed at least once
        Thread.sleep(TimeUnit.SECONDS.toMillis(PERIOD + 1));
        ticker.stop();
        final int countAfterStop = counter.get();
        Thread.sleep(TimeUnit.SECONDS.toMillis(2 * PERIOD));

        // Check that the task did not run after stop
        assertEquals(countAfterStop, counter.get(), "Task executed after stop");
    }
}
