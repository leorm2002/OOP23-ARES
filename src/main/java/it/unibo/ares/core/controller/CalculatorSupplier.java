package it.unibo.ares.core.controller;

import java.util.concurrent.Flow.Subscriber;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import it.unibo.ares.core.controller.models.SimulationOutputData;
import it.unibo.ares.core.model.Model;
import it.unibo.ares.core.utils.Pair;

/**
 * This class is used as an entry point for the simulation system, it is used to
 * access the initialization and the controller of the simulations.
 */
@SuppressFBWarnings(value = {
        "EI_EXPOSE_REP"
}, justification = "C'è i due campi sono final e non modificabili, espongono i metodi richiesti per gestire le simulazioni.")

public final class CalculatorSupplier {
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
    public SimulationsController getController() {
        return controller;
    }

    /**
     * @return the initializer
     */
    public SimulationInitializer getInitializer() {
        return initializer;
    }

    private CalculatorSupplier(final SimulationsController c, final SimulationInitializer i) {
        this.controller = c;
        this.initializer = i;
    }
}
