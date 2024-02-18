package it.unibo.ares.core.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.function.Supplier;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import it.unibo.ares.core.agent.Agent;
import it.unibo.ares.core.model.BoidsModelFactory;
import it.unibo.ares.core.model.Model;
import it.unibo.ares.core.model.ModelFactory;
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
        ModelFactory sf = new SchellingModelFactories();
        ModelFactory bf = new BoidsModelFactory();
        modelsSupplier.put(sf.getModelId(), sf::getModel);
        modelsSupplier.put(bf.getModelId(), bf::getModel);
        this.intilizingModels = new HashMap<>();
        this.initializedModels = new HashMap<>();
    }

    private void setAgentParameter(final String initializationId, final String agentType, final String key,
            final Object value, Predicate<Agent> predicate) {
        this.initializedModels.get(
                initializationId).getFirst().getAgents().stream()
                .map(Pair::getSecond)
                .filter(predicate::test)
                .forEach(ag -> ag.setParameter(key, value));
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
        // TODO AGGIUNGERE METODO PER RIMUOVERE MODELLO IN INIZIALIZZAXIONE PASSANDO ID
        String randomID = UUID.randomUUID().toString();
        this.intilizingModels.put(randomID, this.modelsSupplier.get(modelId).get());
        return randomID;
    }

    /**
     * Gets the parameters of the model.
     * 
     * @return a set containing all the parameters of the model.
     */
    @Override
    public Parameters getModelParametersParameters(final String initializationId) {
        return this.intilizingModels.get(initializationId).getParameters().copy();
    }

    /**
     * Sets a parameter of the model.
     * 
     * @param key   The key of the parameter to set.
     * @param value The value of the parameter to set.
     */
    @Override
    public void setModelParameter(final String initializationId, final String key, final Object value) {
        this.intilizingModels.get(initializationId).getParameters().setParameter(key, value);
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
    public Set<String> getAgentsSimplified(String initializationId) {
        // TODO RICALCOLO SE CAMBIA MODEL
        this.initializedModels.computeIfAbsent(
                initializationId,
                id -> new Pair<>(intilizingModels.get(
                        initializationId).initilize(),
                        intilizingModels.get(initializationId)));
        return this.initializedModels.get(
                initializationId)
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
    public void setAgentParameterSimplified(final String initializationId, final String agentType,
            final String key,
            final Object value) {
        setAgentParameter(initializationId, agentType, key, value, ag -> ag.getType().equals(agentType));
    }

    @Override
    public Parameters getAgentParametersSimplified(final String initializationId, final String agentId) {
        return this.initializedModels.get(initializationId).getFirst().getAgents().stream()
                .map(Pair::getSecond)
                .filter(ag -> ag.getType().equals(agentId))
                .map(Agent::getParameters)
                .findAny()
                .orElseThrow();
    }

    @Override
    Pair<String, Model> startSimulation(final String initializationId) {
        if (!this.initializedModels.containsKey(initializationId)) {
            throw new IllegalArgumentException("The model has not been initialized");
        }
        if (initializedModels.get(initializationId).getFirst().getAgents()
                .stream()
                .map(Pair::getSecond)
                .map(Agent::getParameters)
                .map(Parameters::getParametersToset)
                .anyMatch(s -> !s.isEmpty())) {
            throw new IllegalArgumentException("Some agent parameters are not set");
        }
        Model model = intilizingModels.get(initializationId);

        return new Pair<String, Model>(initializationId, model);
    }
}
