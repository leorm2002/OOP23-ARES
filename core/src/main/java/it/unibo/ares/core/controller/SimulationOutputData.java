package it.unibo.ares.core.controller;

import it.unibo.ares.core.api.SimulationOutputDataApi;
import it.unibo.ares.core.utils.pos.Pos;

import java.util.Collections;
import java.util.Map;

/**
 * A simple class used to identify a data with a string.
 *
 */
public final class SimulationOutputData implements SimulationOutputDataApi {
    private final Map<Pos, String> data;
    private final String simulationId;
    private final Integer width;
    private final Integer height;

    /**
     * Creates a new SimulationOutputData.
     *
     * @param data         the data of the simulation
     * @param simulationId the id of the simulation
     * @param width        the width of the simulation output
     * @param height       the height of the simulation output
     */
    public SimulationOutputData(final Map<Pos, String> data, final String simulationId, final Integer width,
            final Integer height) {
        this.data = Collections.unmodifiableMap(data);
        this.simulationId = simulationId;
        this.width = width;
        this.height = height;
    }

    /**
     * Gets the data of the simulation.
     *
     * @return the data of the simulation.
     */
    @Override
    public Map<Pos, String> getData() {
        return data;
    }

    /**
     * Gets the id of the simulation.
     *
     * @return the id of the simulation.
     */
    @Override
    public String getSimulationId() {
        return simulationId;
    }

    /**
     * Gets the width of the simulation output.
     *
     * @return the width of the simulation output.
     */
    @Override
    public Integer getWidth() {
        return width;
    }

    /**
     * Gets the height of the simulation output.
     *
     * @return the height of the simulation output.
     */
    @Override
    public Integer getHeight() {
        return height;
    }
}
