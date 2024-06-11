package it.unibo.ares.core.controller;

import it.unibo.ares.core.agent.Agent;
import it.unibo.ares.core.utils.Pair;
import it.unibo.ares.core.utils.parameters.Parameters;
import it.unibo.ares.core.utils.state.State;

import java.io.Serializable;
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

        private <T extends Serializable> void setAgentParameter(final String initializationId, final String key,
                        final T value, final Predicate<Agent> predicate) {
                this.initializedModels.get(
                                initializationId).getFirst().getAgents().stream()
                                .map(Pair::getSecond)
                                .filter(predicate::test)
                                .forEach(ag -> ag.setParameter(key, value));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Set<String> getModels() {
                return modelsSupplier.keySet();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String addNewModel(final String modelId) {
                final String randomID = UUID.randomUUID().toString();
                this.intilizingModels.put(randomID, this.modelsSupplier.get(modelId).get());
                return randomID;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Parameters getModelParametersParameters(final String initializationId) {
                return this.intilizingModels.get(initializationId).getParameters().copy();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public <T extends Object & Serializable> void setModelParameter(final String initializationId, final String key,
                        final T value) {
                this.intilizingModels.get(initializationId).getParameters().setParameter(key, value);
        }

        /**
         * {@inheritDoc}
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
         * {@inheritDoc}
         */
        @Override
        public <T extends Object & Serializable> void setAgentParameterSimplified(final String initializationId,
                        final String agentType,
                        final String key,
                        final T value) {
                setAgentParameter(initializationId, key, value, ag -> ag.getType().equals(agentType));
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Parameters getAgentParametersSimplified(final String initializationId, final String agentId) {
                return this.initializedModels.get(initializationId).getFirst().getAgents().stream()
                                .map(Pair::getSecond)
                                .filter(ag -> ag.getType().equals(agentId))
                                .map(Agent::getParameters)
                                .findAny()
                                .orElseThrow();
        }

        /**
         * {@inheritDoc}
         */
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
