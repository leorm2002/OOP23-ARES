package it.unibo.ares.controller;

import java.util.List;
import java.util.Set;

import it.unibo.ares.model.Model;
import it.unibo.ares.utils.Pair;
import it.unibo.ares.utils.parameters.Parameters;

public interface SimulationInitializer {

    /**
     * Gets the models available for the simulation.
     * @return A list of pairs, where the first element is the name of the model and the second element is the identifier of the model.
     */
    List<Pair<String, String>> getModels();

    /**
     * Sets the model of the simulation.
     * @param modelId The identifier of the model to set.
     * @return The id of the configuration session, used to identify the configuration session.
     */
    String setModel(String modelId);

    /**
     * Gets the parameters of the model.
     * @return a set containing all the parameters of the model.
     */
    Parameters getModelParametersParameters(String modelId);

    /**
     * Sets a parameter of the model.
     * @param key The key of the parameter to set.
     * @param value The value of the parameter to set.
     */
    void setModelParameter(String key, Object value);

    /**
     * Gets a simplified view of the agents of the model, it permets only to parametrize the agents 
     * as a group and not singularly, it is useful for the frontend.
     * @return A set containing all the agents of the model, represented as a pair of strings, where the
     * first element is the name of the agent and the second element is the identifier of the group agent.
     */
    Set<Pair<String, String>> getAgentsSimplified();

    /**
     * Sets a parameter of the agent.
     * @param agentId The identifier of the group of agents to set the parameter to.
     * @param key The key of the parameter to set.
     * @param value The value of the parameter to set.
     */
    void setAgentParameterSimplified(String agentId, String key, Object value);

    /**
     * Starts the simulation.
     * @return The id of the simulation session, used to identify the simulation session.
     */
    Pair<String, Model> startSimulation(String initializationId);

}