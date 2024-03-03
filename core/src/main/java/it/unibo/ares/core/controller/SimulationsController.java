package it.unibo.ares.core.controller;

import it.unibo.ares.core.api.SimulationControlApi;
import it.unibo.ares.core.api.SimulationOutputData;

import java.util.concurrent.Flow.Subscriber;

/**
 * A controller for all the simulations.
 */
public abstract class SimulationsController implements SimulationControlApi {

    /**
     * Adds a new simulation to the current session.
     * 
     * @param id         The identifier of the simulation.
     * @param simulation The simulation to add.
     */
    abstract void addSimulation(String id, Simulation simulation);

    /**
     * tick each simulation.
     */
    abstract void makeModelsTick();

    abstract void subscribe(String id, Subscriber<SimulationOutputData> subscriber);

}
