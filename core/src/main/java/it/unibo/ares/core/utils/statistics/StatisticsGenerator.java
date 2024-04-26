package it.unibo.ares.core.utils.statistics;

import it.unibo.ares.core.utils.state.State;

/**
 * A functional interface to generate statistics given a state.
 */
@FunctionalInterface
public interface StatisticsGenerator {
    /**
     * Generate the stats for the given state.
     * 
     * @param s the state
     * @return the computed statistics
     */
    Statistics generate(State s);
}
