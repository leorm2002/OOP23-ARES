package it.unibo.ares.core.controller.models;

import it.unibo.ares.core.utils.pos.Pos;
import java.util.HashMap;

/**
 * A simple class used to identify a data with a string.
 * 
 */
public final class SimulationOutputData {
    private HashMap<Pos, String> data;
    private String simulationId;

    public SimulationOutputData(HashMap<Pos, String> data, String simulationId) {
        this.data = data;
        this.simulationId = simulationId;
    }

    /**
     * Gets the data of the simulation.
     *
     * @return the data of the simulation.
     */
    public HashMap<Pos, String> getData() {
        return data;
    }

    /**
     * Gets the id of the simulation.
     *
     * @return the id of the simulation.
     */
    public String getSimulationId() {
        return simulationId;
    }
}
