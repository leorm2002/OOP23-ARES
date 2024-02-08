package it.unibo.ares.core.model;

import it.unibo.ares.core.utils.parameters.Parameters;
import it.unibo.ares.core.utils.state.State;

public interface Model {
    /**
     * Performs a tick of the model, updating the simulation enviroment state based on the current state.
     *
     * @param state the current state of the enviroment
     * @return the updated state of the enviroment
     */
    public State tick(State state);

    /**
     * Gets the parameters of the model.
     *
     * @return the parameters of the model
     */
    public Parameters getParameters();

    /**
     * Sets a parameter of the model.
     *
     * @param key the key of the parameter
     * @param value the value of the parameter
     * @param <T> the type of the parameter
     */
    public <T> void setParameter(String key, T value);
    
}
