package it.unibo.ares.core.controller;

import java.util.Set;
import java.util.concurrent.Flow.Subscriber;
import java.util.List;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import it.unibo.ares.core.api.InitializationApi;
import it.unibo.ares.core.api.SimulationControlApi;
import it.unibo.ares.core.api.SimulationOutputData;
import it.unibo.ares.core.model.Model;
import it.unibo.ares.core.utils.Pair;
import it.unibo.ares.core.utils.parameters.Parameters;

/**
 * This class is used as an entry point for the simulation system, it is used to
 * access the initialization and the controller of the simulations.
 */
@SuppressFBWarnings(value = {
        "EI_EXPOSE_REP"
}, justification = "C'è i due campi sono final e non modificabili, espongono i metodi richiesti per gestire le simulazioni.")

public final class CalculatorSupplier implements InitializationApi, SimulationControlApi {
    private static volatile CalculatorSupplier instance;

    private final SimulationsController controller;
    private final SimulationInitializer initializer;
    private final Ticker ticker;

    /**
     * Starts the simulation with the given initialization id.
     *
     * @param initializationId The id of the initialization to start.
     * @param subscriber       instance of the subscriber which the data of the
     *                         simulation will be passed to.
     * @return The id of the simulation.
     */
    public String startSimulation(final String initializationId, final Subscriber<SimulationOutputData> subscriber) {
        Pair<String, Model> resp = initializer.startSimulation(initializationId);
        controller.addSimulation(resp.getFirst(),
                new SimulationImpl(resp.getSecond().initilize(), resp.getSecond()));
        controller.startSimulation(initializationId);
        controller.subscribe(initializationId, subscriber);
        return initializationId;
    }

    /**
     * Returns the singleton instance of the calculator supplier.
     *
     * @return the singleton instance of the calculator supplier.
     */
    public static CalculatorSupplier getInstance() {
        CalculatorSupplier curr = instance;

        if (curr != null) {
            return curr;
        }
        synchronized (CalculatorSupplier.class) {
            if (instance == null) {
                instance = new CalculatorSupplier(
                        new SimulationsControllerImpl(), new SimulationInitializerImpl());
            }
            return instance;
        }
    }

    private CalculatorSupplier(final SimulationsController c, final SimulationInitializer i) {
        this.controller = c;
        this.initializer = i;
        this.ticker = new TickerImpl(controller::makeModelsTick, 0, 1);
        ticker.start();
    }

    @Override
    public List<String> getRunningSimulations() {
        return controller.getRunningSimulations();
    }

    @Override
    public void removeSimulation(String id) {
        controller.removeSimulation(id);
    }

    @Override
    public void pauseSimulation(String id) {
        controller.pauseSimulation(id);
    }

    @Override
    public void startSimulation(String id) {
        controller.startSimulation(id);
    }

    @Override
    public Set<String> getModels() {
        return initializer.getModels();
    }

    @Override
    public String setModel(String modelId) {
        return initializer.setModel(modelId);
    }

    @Override
    public Parameters getModelParametersParameters(String initializationId) {
        return initializer.getModelParametersParameters(initializationId);
    }

    @Override
    public void setModelParameter(String initializationId, String key, Object value) {
        initializer.setModelParameter(initializationId, key, value);
    }

    @Override
    public Set<String> getAgentsSimplified(String initializationId) {
        return initializer.getAgentsSimplified(initializationId);
    }

    @Override
    public Parameters getAgentParametersSimplified(String initializationId, String agentId) {
        return initializer.getAgentParametersSimplified(initializationId, agentId);
    }

    @Override
    public void setAgentParameterSimplified(String initializationId, String agentId, String key, Object value) {
        initializer.setAgentParameterSimplified(initializationId, agentId, key, value);
    }
}
