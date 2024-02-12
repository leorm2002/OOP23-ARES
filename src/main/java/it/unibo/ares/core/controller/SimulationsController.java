package it.unibo.ares.core.controller;

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
     */
    void startSimulation(String id);

    void onTick();

}