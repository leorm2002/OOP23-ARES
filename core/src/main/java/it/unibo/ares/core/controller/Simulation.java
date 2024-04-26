package it.unibo.ares.core.controller;

import java.util.Optional;
import java.util.concurrent.CompletableFuture;

import it.unibo.ares.core.model.Model;
import it.unibo.ares.core.utils.state.State;

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
    CompletableFuture<Optional<SimulationOutputData>> tick(String simulationSessionId);

    /**
     * Ticks the simulation.
     * 
     * @param simulationSessionId The user simulation session ids of the simulation
     *                            to tick.
     * @return A future containing the output of the simulation.
     */
    Optional<SimulationOutputData> tickSync(String simulationSessionId);

    /**
     * get the tick rate.
     * 
     * @return il tick rate in ms
     */
    Integer getTickRate();

    /**
     * set the tick rate.
     * 
     * @param tickRate il tick rate in ms
     */
    void setTickRate(Integer tickRate);
}
