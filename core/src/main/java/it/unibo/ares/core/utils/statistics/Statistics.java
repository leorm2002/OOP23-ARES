package it.unibo.ares.core.utils.statistics;

import java.util.List;

import it.unibo.ares.core.utils.Pair;

/**
 * Functional interface that define a lists of statisics in the format
 * key-value.
 */
@FunctionalInterface
public interface Statistics {
    /**
     * Get the statistic.
     * 
     * @return a list of pair (descriprion, value)
     */
    List<Pair<String, String>> getStatistics();
}
