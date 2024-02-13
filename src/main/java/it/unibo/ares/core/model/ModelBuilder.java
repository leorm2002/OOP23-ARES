package it.unibo.ares.core.model;

import java.util.function.Function;
import java.util.function.Predicate;

import it.unibo.ares.core.utils.Pair;
import it.unibo.ares.core.utils.parameters.Parameter;
import it.unibo.ares.core.utils.parameters.Parameters;
import it.unibo.ares.core.utils.state.State;

public interface ModelBuilder {
    /**
     * Builds an instance of the Model interface.
     *
     * @return An instance of the Model interface.
     * @throws IllegalStateException if the strategy is not set.
     */
    Pair<Model, State> build();

    /**
     * Resets the model to its initial state.
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
    <T> ModelBuilder addParameter(Parameter<T> parameter);

    ModelBuilder addExitFunction(Predicate<State> exitfFunction);

    ModelBuilder addInitFunction(Function<Parameters, State> initFunction);

}
