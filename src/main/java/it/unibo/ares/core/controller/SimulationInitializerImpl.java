package it.unibo.ares.core.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import it.unibo.ares.core.agent.Agent;
import it.unibo.ares.core.model.Model;
import it.unibo.ares.core.model.SchellingModelFactories;
import it.unibo.ares.core.utils.Pair;
import it.unibo.ares.core.utils.parameters.Parameters;
import it.unibo.ares.core.utils.state.State;

/**
 * A class that initializes the simulation.
 */
public final class SimulationInitializerImpl extends SimulationInitializer {

    private Map<String, Model> intilizingModels;
    private Map<String, Pair<State, Model>> initializedModels;
    private Map<String, Supplier<Model>> modelsSupplier;

    public SimulationInitializerImpl() {
        this.modelsSupplier = new HashMap<>();
        modelsSupplier.put(SchellingModelFactories.getModelId(), SchellingModelFactories::getModel);
        this.intilizingModels = new HashMap<>();
    }

    /**
     * Gets the models available for the simulation.
     * 
     * @return A list of pairs, where the first element is the name of the model and
     *         the second element is the identifier of the model.
     */
    @Override
    public Set<String> getModels() {
        return modelsSupplier.keySet();
    }

    /**
     * Sets the model of the simulation.
     * 
     * @param modelId The identifier of the model to set.
     * @return The id of the configuration session, used to identify the
     *         configuration session.
     */
    @Override
    public String setModel(final String modelId) {
        String randomID = "";
        this.intilizingModels.put(randomID, this.modelsSupplier.get(modelId).get());
        return randomID;
    }

    /**
     * Gets the parameters of the model.
     * 
     * @return a set containing all the parameters of the model.
     */
    @Override
    public Parameters getModelParametersParameters(final String modelId) {
        return this.intilizingModels.get(modelId).getParameters().clone();
    }

    /**
     * Sets a parameter of the model.
     * 
     * @param key   The key of the parameter to set.
     * @param value The value of the parameter to set.
     */
    @Override
    public void setModelParameter(final String modelId, final String key, final Object value) {
        this.intilizingModels.get(modelId).getParameters().setParameter(key, value);
    }

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
    @Override
    public Set<String> getAgentsSimplified(String modelId) {
        // TODO RICALCOLO SE CAMBIA MODEL
        this.initializedModels.computeIfAbsent(modelId,
                id -> new Pair<>(intilizingModels.get(modelId).initilize(), intilizingModels.get(modelId)));
        return this.initializedModels.get(modelId)
                .getFirst()
                .getAgents()
                .stream()
                .map(Pair::getSecond)
                .map(Agent::getType)
                .collect(Collectors.toSet());
    }

    /**
     * Sets a parameter of all the agent with the given type
     * 
     * @param agentType The identifier of the group of agents to set the parameter
     *                  to.
     * @param key       The key of the parameter to set.
     * @param value     The value of the parameter to set.
     */
    @Override
    public void setAgentParameterSimplified(final String modelId, final String agentType, final String key,
            final Object value) {
        this.initializedModels.get(modelId).getFirst().getAgents().stream()
                .map(Pair::getSecond)
                .filter(ag -> ag.getId().equals(agentType))
                .forEach(ag -> ag.getParameters().setParameter(key, value));
    }

    @Override
    public Parameters getAgentParametersSimplified(final String agentId) {
        return null;
    }

    /**
     * Starts the simulation.
     *
     * @return The id of the simulation session, used to identify the simulation
     *         session.
     */
    @Override
    public Pair<String, Model> startSimulation(final String initializationId) {
        Model model = intilizingModels.get(initializationId);

        return new Pair<String, Model>(initializationId, model);
    }
}
