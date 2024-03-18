package it.unibo.ares.core.utils.statistics;

import java.util.List;

import it.unibo.ares.core.utils.Pair;

@FunctionalInterface
public interface Statistics {
    List<Pair<String, String>> getStatistics();
}
