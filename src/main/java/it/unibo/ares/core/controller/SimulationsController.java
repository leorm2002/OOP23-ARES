package it.unibo.ares.core.controller;

import java.util.concurrent.Flow.Subscriber;

import it.unibo.ares.core.controller.models.SimulationOutputData;

/**
 * A controller for all the simulations.
 */
public abstract class SimulationsController {

    /**
     * Adds a new simulation to the current session.
     * 
     * @param id         The identifier of the simulation.
     * @param simulation The simulation to add.
     */
    protected abstract void addSimulation(String id, Simulation simulation);

    /**
     * remove the simulation from the current session.
     *
     * @param id The identifier of the simulation.
     */
    public abstract void removeSimulation(String id);

    /**
     * pause the simulation of the given id.
     *
     * @param id The identifier of the simulation.
     */
    public abstract void pauseSimulation(String id);

    /**
     * start the simulation of the given id.
     *
     * @param id The identifier of the simulation.
     */
    public abstract void startSimulation(String id);

    /**
     * tick each simulation.
     */
    protected abstract void makeModelsTick();

    abstract void subscribe(String id, Subscriber<SimulationOutputData> subscriber);

}
