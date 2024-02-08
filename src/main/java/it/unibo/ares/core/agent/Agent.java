package it.unibo.ares.core.agent;

import it.unibo.ares.core.utils.parameters.Parameters;
import it.unibo.ares.core.utils.pos.Pos;
import it.unibo.ares.core.utils.state.State;

/**
 * Represents an agent in an agent-based model.
 */
public interface Agent {

    /**
     * Performs a tick of the agent, updating the simulation enviroment state based on the current state and the agnet position.
     *
     * @param state the current state of the enviroment
     * @param pos the position of the agent
     * @return the updated state of the enviroment
     */
    State tick(State state, Pos pos);

    /**
     * Gets the parameters of the agent.
     *
     * @return the parameters of the agent
     */
    Parameters getParameters();

    /**
     * Sets a parameter of the agent.
     *
     * @param key the key of the parameter
     * @param value the value of the parameter
     * @param <T> the type of the parameter
     */
    <T> void setParameter(String key, T value);

}
