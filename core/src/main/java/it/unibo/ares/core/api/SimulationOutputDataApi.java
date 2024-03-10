package it.unibo.ares.core.api;

import it.unibo.ares.core.utils.pos.Pos;

import java.util.Map;

/**
 * API for the output of the simulation.
 */
public interface SimulationOutputDataApi {

    /**
     * Gets the data of the simulation.
     *
     * @return the data of the simulation.
     */
    Map<Pos, String> getData();

    /**
     * Gets the id of the simulation.
     *
     * @return the id of the simulation.
     */
    String getSimulationId();

    /**
     * Gets the width of the simulation.
     *
     * @return the width of the simulation.
     */
    Integer getWidth();

    /**
     * Gets the height of the simulation.
     *
     * @return the height of the simulation.
     */
    Integer getHeight();

}
