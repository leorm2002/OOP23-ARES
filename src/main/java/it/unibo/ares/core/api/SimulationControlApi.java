package it.unibo.ares.core.api;

public interface SimulationControlApi {

    /**
     * remove the simulation from the current session.
     *
     * @param id The identifier of the simulation.
     */
    public void removeSimulation(String id);

    /**
     * pause the simulation of the given id.
     *
     * @param id The identifier of the simulation.
     */
    public void pauseSimulation(String id);

    /**
     * start the simulation of the given id.
     *
     * @param id The identifier of the simulation.
     */
    public void startSimulation(String id);

}