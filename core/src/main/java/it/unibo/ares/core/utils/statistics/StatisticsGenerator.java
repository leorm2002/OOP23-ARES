package it.unibo.ares.core.utils.statistics;

import it.unibo.ares.core.utils.state.State;

public interface StatisticsGenerator {
    Statistics generate(State s);
}
