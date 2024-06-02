package it.unibo.ares.core.agent;

import java.io.Serializable;

import it.unibo.ares.core.utils.parameters.Parameters;
import it.unibo.ares.core.utils.pos.Pos;
import it.unibo.ares.core.utils.state.State;

/**
 * Represents an agent in an agent-based model.
 */
public interface Agent extends Serializable {

    /**
     * Performs a tick of the agent, updating the simulation enviroment state based
     * on the current state and the agnet position.
     *
     * @param state the current state of the enviroment
     * @param pos   the position of the agent
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
     * @param key   the key of the parameter
     * @param value the value of the parameter
     * @param <T>   the type of the parameter
     */
    <T extends Serializable> void setParameter(String key, T value);

    /**
     * Retrieves the id of the agent.
     *
     * @return the id of the agent
     */
    String getId();

    /**
     * Sets the type of the agent.
     *
     * @return the type of the agent
     */
    String getType();

    /**
     * Sets the type of the agent.
     *
     * @param type the type of the agent
     */
    void setType(String type);
}
