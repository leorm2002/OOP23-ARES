package it.unibo.ares.core.controller;

public interface SimulationManager {

    /**
     * Saves the given simulation.
     *
     * @param simulation The simulation to be saved.
     * @return A string representing the saved simulation.
     */
    String save(Simulation simulation);

    /**
     * Loads a simulation from the specified file path.
     *
     * @param filePath The file path of the simulation to be loaded.
     * @return The loaded simulation.
     */
    Simulation load(String filePath);

}
