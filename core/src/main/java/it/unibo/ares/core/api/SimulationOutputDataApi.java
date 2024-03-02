package it.unibo.ares.core.api;

import java.util.Map;

import it.unibo.ares.core.utils.pos.Pos;

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

    Integer getWidth();

    Integer getHeight();

}