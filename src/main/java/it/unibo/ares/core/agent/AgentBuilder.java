package it.unibo.ares.core.agent;

import java.util.function.BiFunction;

import it.unibo.ares.core.utils.parameters.Parameter;
import it.unibo.ares.core.utils.pos.Pos;
import it.unibo.ares.core.utils.state.State;

/**
 * The AgentBuilder interface represents a builder for creating instances of the
 * Agent interface.
 * The AgentBuilder interface represents a builder for creating instances of the
 * Agent interface.
 */
interface AgentBuilder {
    /**
     * Builds an instance of the Agent interface.
     *
     * @return An instance of the Agent interface.
     * @throws IllegalStateException if the strategy is not set.
     */
    Agent build();

    /**
     * Resets the agent to its initial state.
     */
    void reset();

    /**
     * Adds a parameter to the agent.
     *
     * @throws IllegalArgumentException if the parameter is already set.
     * @throws NullPointerException     if the parameter key or type is null.
     * @param parameter the parameter to add.
     * @param <T>       the type of the parameter value (ex. Integer, String, ...).
     * @return the agent builder with the added parameter.
     */
    <T> AgentBuilder addParameter(Parameter<T> parameter);

    /**
     * Adds a strategy to the agent.
     *
     * @throws NullPointerException if the strategy is null.
     * @param strategy the strategy to add.
     * @return the agent builder with the added strategy.
     */
    AgentBuilder addStrategy(BiFunction<State, Pos, State> strategy);

}
