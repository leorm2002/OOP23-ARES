package it.unibo.ares.core.controller;

import it.unibo.ares.core.model.Model;
import it.unibo.ares.core.utils.state.State;

import java.util.concurrent.CompletableFuture;

/**
 * A simulation is a class that contains the state of the simulation and the
 * model of the simulation.
 */
interface Simulation {

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
     * Pause the simulation.
     */
    void pause();

    /**
     * @return true if the simulation is running, false otherwise.
     */
    boolean isRunning();

    /**
     * Ticks the simulation.
     * 
     * @param simulationSessionId The user simulation session ids of the simulation
     *                            to tick.
     * @return A future containing the output of the simulation.
     */
    CompletableFuture<SimulationOutputData> tick(String simulationSessionId);

    /**
     * Ticks the simulation.
     * 
     * @param simulationSessionId The user simulation session ids of the simulation
     *                            to tick.
     * @return A future containing the output of the simulation.
     */
    SimulationOutputData tickSync(String simulationSessionId);

}
