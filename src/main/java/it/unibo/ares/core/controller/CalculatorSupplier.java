package it.unibo.ares.core.controller;

import java.util.Set;
import java.util.concurrent.Flow.Subscriber;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import it.unibo.ares.core.api.SimulationInitializerApi;
import it.unibo.ares.core.api.SimulationsControllerApi;
import it.unibo.ares.core.controller.models.SimulationOutputData;
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

public final class CalculatorSupplier implements SimulationInitializerApi, SimulationsControllerApi {
    private static volatile CalculatorSupplier instance;

    private final SimulationsController controller;
    private final SimulationInitializer initializer;

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

    /**
     * @return the controller
     */
    public SimulationsControllerApi getController() {
        return controller;
    }

    /**
     * @return the initializer
     */
    public SimulationInitializerApi getInitializer() {
        return initializer;
    }

    private CalculatorSupplier(final SimulationsController c, final SimulationInitializer i) {
        this.controller = c;
        this.initializer = i;
    }

    @Override
    public void removeSimulation(String id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'removeSimulation'");
    }

    @Override
    public void pauseSimulation(String id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'pauseSimulation'");
    }

    @Override
    public void startSimulation(String id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'startSimulation'");
    }

    @Override
    public Set<String> getModels() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getModels'");
    }

    @Override
    public String setModel(String modelId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setModel'");
    }

    @Override
    public Parameters getModelParametersParameters(String initializationId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getModelParametersParameters'");
    }

    @Override
    public void setModelParameter(String initializationId, String key, Object value) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setModelParameter'");
    }

    @Override
    public Set<String> getAgentsSimplified(String initializationId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getAgentsSimplified'");
    }

    @Override
    public Parameters getAgentParametersSimplified(String initializationId, String agentId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getAgentParametersSimplified'");
    }

    @Override
    public void setAgentParameterSimplified(String initializationId, String agentId, String key, Object value) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setAgentParameterSimplified'");
    }
}
