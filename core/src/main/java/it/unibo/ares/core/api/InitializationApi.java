package it.unibo.ares.core.api;

import it.unibo.ares.core.utils.parameters.Parameters;

import java.util.Set;
import java.io.Serializable;

/**
 * API for the initialization of the simulation.
 */
public interface InitializationApi {

    /**
     * Gets the models available for the simulation.
     * 
     * @return A set of strings each identifying a different model
     *
     */
    Set<String> getModels();

    /**
     * Sets the model of the simulation.
     * 
     * @param modelId The identifier of the model to set.
     * @return The id of the configuration session, used to identify the
     *         configuration session.
     */
    String addNewModel(String modelId);

    /**
     * Gets the parameters of the model.
     * 
     * @param initializationId The identifier of the model to get the parameters
     *                         from.
     * @return a set containing all the parameters of the model.
     */
    Parameters getModelParametersParameters(String initializationId);

    /**
     * Sets a parameter of the model.
     * 
     * @param initializationId The identifier of the model to set the parameter to.
     * @param key              The key of the parameter to set.
     * @param value            The value of the parameter to set.
     */
    <T extends Object & Serializable> void setModelParameter(String initializationId, String key, T value);

    /**
     * Gets a simplified view of the agents of the model, it permets only to
     * parametrize the agents
     * as a group and not singularly, it is useful for the frontend.
     * 
     * @param initializationId The identifier of the model to get the agents from.
     * @return A set containing all the agents of the model, represented as a pair
     *         of strings, where the
     *         first element is the name of the agent and the second element is the
     *         identifier of the group agent.
     */
    Set<String> getAgentsSimplified(String initializationId);

    /**
     * Gets the parameters of the agent.
     * 
     * @param agentId          The identifier of the group of agents to get
     *                         the parameters
     *                         from.
     * @param initializationId The identifier of the model to get the
     *                         parameters from.
     * @return A Parameters containing all the parameters of the agent.
     */
    Parameters getAgentParametersSimplified(String initializationId,
            String agentId);

    /**
     * Sets a parameter of the agent.
     * 
     * @param initializationId The identifier of the group of agents to set the
     *                         parameter to.
     * @param key              The key of the parameter to set.
     * @param value            The value of the parameter to set.
     * @param agentId          The identifier of the group of agents to set the
     *                         parameter to.
     */
    <T extends Object & Serializable> void setAgentParameterSimplified(String initializationId, String agentId,
            String key, T value);

}
