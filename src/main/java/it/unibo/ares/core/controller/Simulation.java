package it.unibo.ares.core.controller;

import java.util.concurrent.CompletableFuture;

import it.unibo.ares.core.controller.models.SimulationOutputData;
import it.unibo.ares.core.model.Model;
import it.unibo.ares.core.utils.state.State;

/**
 * A simulation is a class that contains the state of the simulation and the
 * model of the simulation.
 */
public interface Simulation {

    /**
     * @return the state
     */
    State getState();

    /**
     * @return the model
     */
    Model getModel();

    /**
     * Starts the simulation.
     */
    void start();

    /**
     * @return true if the simulation is running, false otherwise.
     */
    boolean isRunning();

    /**
     * Ticks the simulation.
     * 
     * @param simulationId The id of the simulation to tick.
     * @return A future containing the output of the simulation.
     */
    CompletableFuture<SimulationOutputData> tick(String simulationId);
}
