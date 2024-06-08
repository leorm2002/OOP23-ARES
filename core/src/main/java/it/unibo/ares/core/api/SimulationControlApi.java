package it.unibo.ares.core.api;

import java.util.List;

/**
 * This interface provides methods to control the simulation.
 */
public interface SimulationControlApi {

    /**
     * remove the simulation from the current session.
     *
     * @param id The identifier of the simulation.
     */
    void removeSimulation(String id);

    /**
     * pause the simulation of the given id.
     *
     * @param id The identifier of the simulation.
     */
    void pauseSimulation(String id);

    /**
     * start the simulation of the given id.
     *
     * @param id The identifier of the simulation.
     */
    void startSimulation(String id);

    /**
     * Returns a list of running models.
     *
     * @return a list of running models
     */
    List<String> getRunningSimulations();

    /**
     * get the tick rate.
     * 
     * @param id the id of the simulation
     * @return the tick rate ms
     */
    Integer getTickRate(String id);

    /**
     * set the tick rate.
     * 
     * @param id       the id of sumulation
     * @param tickRate the new tickrate in ms
     */
    void setTickRate(String id, Integer tickRate);

    /**
     * Save the simulation to a file.
     * 
     * @param id the id of sumulation
     * @return the path of the file
     */
    String save(String id);

    /**
     * set the tick rate.
     * 
     * @param filePath the path of the file
     */
    void load(String filePath);
}
