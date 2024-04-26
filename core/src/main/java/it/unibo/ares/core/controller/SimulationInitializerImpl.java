package it.unibo.ares.core.controller;

import it.unibo.ares.core.agent.Agent;
import it.unibo.ares.core.utils.Pair;
import it.unibo.ares.core.utils.parameters.Parameters;
import it.unibo.ares.core.utils.state.State;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import it.unibo.ares.core.model.SimpleModelFactory;
import it.unibo.ares.core.model.BoidsModelFactory;
import it.unibo.ares.core.model.FireSpreadModelFactory;
import it.unibo.ares.core.model.VirusModelFactory;
import it.unibo.ares.core.model.Model;
import it.unibo.ares.core.model.ModelFactory;
import it.unibo.ares.core.model.PredatorPreyModelFactory;
import it.unibo.ares.core.model.SchellingModelFactory;

/**
 * A class that initializes the simulation.
 */
public final class SimulationInitializerImpl extends SimulationInitializer {
    private static final int DEFAULTTICKRATE = 500;
    private final ConcurrentMap<String, Model> intilizingModels;
    private final ConcurrentMap<String, Pair<State, Model>> initializedModels;
    private final Map<String, Supplier<Model>> modelsSupplier;

    /**
     * Creates a new instance of the simulation initializer.
     */
    public SimulationInitializerImpl() {
        this.modelsSupplier = new HashMap<>();
        final ModelFactory sf = new SchellingModelFactory();
        final ModelFactory bf = new BoidsModelFactory();
        final ModelFactory ff = new FireSpreadModelFactory();
        final ModelFactory pp = new PredatorPreyModelFactory();
        final ModelFactory vf = new VirusModelFactory();
        final ModelFactory smf = new SimpleModelFactory();
        modelsSupplier.put(sf.getModelId(), sf::getModel);
        modelsSupplier.put(bf.getModelId(), bf::getModel);
        modelsSupplier.put(ff.getModelId(), ff::getModel);
        modelsSupplier.put(pp.getModelId(), pp::getModel);
        modelsSupplier.put(vf.getModelId(), vf::getModel);
        modelsSupplier.put(smf.getModelId(), smf::getModel);

        this.intilizingModels = new ConcurrentHashMap<>();
        this.initializedModels = new ConcurrentHashMap<>();
    }

    private void setAgentParameter(final String initializationId, final String key,
            final Object value, final Predicate<Agent> predicate) {
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
    public String addNewModel(final String modelId) {
        final String randomID = UUID.randomUUID().toString();
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
    public Set<String> getAgentsSimplified(final String initializationId) {
        this.initializedModels.computeIfAbsent(
                initializationId,
                id -> new Pair<>(intilizingModels.get(
                        initializationId).initilize(),
                        intilizingModels.get(initializationId)));
        this.intilizingModels.remove(initializationId);
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
     * Sets a parameter of all the agent with the given type.
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
        setAgentParameter(initializationId, key, value, ag -> ag.getType().equals(agentType));
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
    Pair<String, Simulation> startSimulation(final String initializationId) {
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
        final Pair<State, Model> model = this.initializedModels.remove(initializationId);
        return new Pair<>(initializationId,
                new SimulationImpl(model.getFirst(),
                        model.getSecond(), DEFAULTTICKRATE));
    }
}
