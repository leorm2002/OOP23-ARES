package it.unibo.ares.core.model;

import java.io.Serializable;

import it.unibo.ares.core.utils.parameters.Parameters;
import it.unibo.ares.core.utils.state.State;
import it.unibo.ares.core.utils.statistics.Statistics;

/**
 * Represents a model of a simulation.
 */
public interface Model extends Serializable {
    /**
     * Key that anyone should use to represent the size of the board.
     */
    String SIZEKEY = "size";

    /**
     * Performs a tick of the model, updating the simulation enviroment state based
     * on the current state.
     *
     * @param state the current state of the enviroment
     * @return the updated state of the enviroment
     */
    State tick(State state);

    /**
     * Gets the parameters of the model.
     *
     * @return the parameters of the model
     */
    Parameters getParameters();

    /**
     * Sets a parameter of the model.
     *
     * @param key   the key of the parameter
     * @param value the value of the parameter
     * @param <T>   the type of the parameter
     */
    <T> void setParameter(String key, T value);

    /**
     * Given the old state and the new state returns weather the simulation reached
     * the end.
     * 
     * @param oldState
     * @param newState
     * @return true if the simulation is over, false otherwise
     */
    boolean isOver(State oldState, State newState);

    /**
     * Initializes the model. Fail if not all parameters are setted.
     *
     * @return the initial state of the model
     */
    State initilize();

    /**
     * Ritorna le eventuali statistiche.
     * 
     * @param s lo stato su cui calcolarle
     * @return the statistics
     */
    Statistics getStatistics(State s);

}
