package it.unibo.ares.core.api;

import it.unibo.ares.core.utils.pos.Pos;

import java.util.Collections;
import java.util.Map;

/**
 * A simple class used to identify a data with a string.
 *
 */
public final class SimulationOutputData {
    private final Map<Pos, String> data;
    private final String simulationId;

    /**
     * Creates a new SimulationOutputData.
     *
     * @param data         the data of the simulation
     * @param simulationId the id of the simulation
     */
    public SimulationOutputData(final Map<Pos, String> data, final String simulationId) {
        this.data = Collections.unmodifiableMap(data);
        this.simulationId = simulationId;
    }

    /**
     * Gets the data of the simulation.
     *
     * @return the data of the simulation.
     */
    public Map<Pos, String> getData() {
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
