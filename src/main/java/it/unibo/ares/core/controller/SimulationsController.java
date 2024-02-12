package it.unibo.ares.core.controller;

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
     * restart the simulation of the given id.
     * 
     * @param id The identifier of the simulation.
     */
    void startSimulation(String id);

    /**
     * tick each simulation.
     */
    void tick();

}
