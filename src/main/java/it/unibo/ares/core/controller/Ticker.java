package it.unibo.ares.core.controller;

/**
 * The Ticker interface provides a mechanism for objects to implement a
 * start-stop functionality.
 * Implementations of this interface are expected to define what actions should
 * be taken when the ticker is started or stopped.
 */
public interface Ticker {
    /**
     * Starts the ticker, initiating any scheduled or repetitive tasks that should
     * be run.
     */
    void start();

    /**
     * Stops the ticker, halting any ongoing tasks and preventing new ones from
     * starting.
     * This method should also handle the necessary cleanup.
     */
    void stop();
}
