package it.unibo.ares.core.controller;

import it.unibo.ares.core.api.InitializationApi;
import it.unibo.ares.core.utils.Pair;

/**
 * Used to creare and parametrize an istance of a simulation to be runned on the
 * system.
 */
public abstract class SimulationInitializer implements InitializationApi {
    /**
     * Starts the simulation.
     * 
     * @param initializationId The identifier of the model to start the simulation
     *
     * @return The id of the simulation session, used to identify the simulation
     *         session.
     * @throws IllegalArgumentException if the model has not been initialized or if
     *                                  some agent parameters are not set.
     */
    abstract Pair<String, Simulation> startSimulation(String initializationId);
}
