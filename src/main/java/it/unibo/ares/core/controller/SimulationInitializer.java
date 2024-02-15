package it.unibo.ares.core.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import it.unibo.ares.core.model.Model;
import it.unibo.ares.core.utils.Pair;
import it.unibo.ares.core.utils.parameters.Parameters;

/**
 * Used to creare and parametrize an istance of a simulation to be runned on the
 * system.
 */
public abstract class SimulationInitializer {

    /**
     * Gets the models available for the simulation.
     * 
     * @return A set of strings each identifying a different model
     *
     */
    public abstract Set<String> getModels();

    /**
     * Sets the model of the simulation.
     * 
     * @param modelId The identifier of the model to set.
     * @return The id of the configuration session, used to identify the
     *         configuration session.
     */
    public abstract String setModel(String modelId);

    /**
     * Gets the parameters of the model.
     * 
     * @return a set containing all the parameters of the model.
     */
    public abstract Parameters getModelParametersParameters(String modelId);

    /**
     * Sets a parameter of the model.
     * 
     * @param key   The key of the parameter to set.
     * @param value The value of the parameter to set.
     */
    public abstract void setModelParameter(String modelId, String key, Object value);

    /**
     * Gets a simplified view of the agents of the model, it permets only to
     * parametrize the agents
     * as a group and not singularly, it is useful for the frontend.
     * 
     * @return A set containing all the agents of the model, represented as a pair
     *         of strings, where the
     *         first element is the name of the agent and the second element is the
     *         identifier of the group agent.
     */
    public abstract Set<String> getAgentsSimplified(String modelId);

    /**
     * Gets the parameters of the agent.
     * 
     * @param agentId The identifier of the group of agents to get the parameters
     *                from.
     * @return A set containing all the parameters of the agent.
     */
    public abstract Parameters getAgentParametersSimplified(String agentId);

    /**
     * Sets a parameter of the agent.
     * 
     * @param agentId The identifier of the group of agents to set the parameter to.
     * @param key     The key of the parameter to set.
     * @param value   The value of the parameter to set.
     */
    public abstract void setAgentParameterSimplified(String modelId, String agentId, String key, Object value);

    /**
     * Starts the simulation.
     *
     * @param initializationId The identifier of the initialization session to start
     * @return The id of the simulation session, used to identify the simulation
     *         session.
     */
    abstract Pair<String, Model> startSimulation(String initializationId);
}
