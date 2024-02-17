package it.unibo.ares.core.controller;

import java.util.concurrent.Flow.Subscriber;

import it.unibo.ares.core.controller.models.SimulationOutputData;

/**
 * A controller for all the simulations.
 */
public interface SimulationsController {

    /**
     * Adds a new simulation to the current session.
     * 
     * @param id         The identifier of the simulation.
     * @param simulation The simulation to add.
     */
    void addSimulation(String id, Simulation simulation);

    /**
     * remove the simulation from the current session.
     *
     * @param id The identifier of the simulation.
     */
    void removeSimulation(String id);

    /**
     * start the simulation of the given id.
     *
     * @param id The identifier of the simulation.
     */
    void startSimulation(String id);

    /**
     * pause the simulation of the given id.
     *
     * @param id The identifier of the simulation.
     */
    void pauseSimulation(String id);

    /**
     * restart the simulation of the given id.
     * 
     * @param id The identifier of the simulation.
     */
    void restartSimulation(String id);

    /**
     * tick each simulation.
     */
    void makeModelsTick();

    void subscribe(String id, Subscriber<SimulationOutputData> subscriber);

}
