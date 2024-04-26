package it.unibo.ares.core.model;

import it.unibo.ares.core.utils.parameters.Parameter;
import it.unibo.ares.core.utils.parameters.Parameters;
import it.unibo.ares.core.utils.state.State;
import it.unibo.ares.core.utils.statistics.StatisticsGenerator;

import java.util.function.BiPredicate;
import java.util.function.Function;

/**
 * Represents a builder for creating models.
 */
interface ModelBuilder {
    /**
     * Builds an instance of the Model interface.
     *
     * @return An instance of the Model interface.
     * @throws IllegalStateException if the strategy is not set.
     */
    Model build();

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
     * @return the model builder with the added parameter.
     */
    <T> ModelBuilder addParameter(Parameter<T> parameter);

    /**
     * Add the function that will be used to check wether the simulation is
     * over.
     * 
     * @param exitfFunction
     * @return the model builder with the added exit function
     */
    ModelBuilder addExitFunction(BiPredicate<State, State> exitfFunction);

    /**
     * Add the function that initialize the state of the model.
     * 
     * @param initFunction
     * @return the model builder with the added init function
     */
    ModelBuilder addInitFunction(Function<Parameters, State> initFunction);

    /**
     * Aggiunge il generatore per permettere al modello di generare statistiche.
     * 
     * @param generator il generatore
     * @return the model builder itself
     */
    ModelBuilder addStatisticsGenerator(StatisticsGenerator generator);
}
