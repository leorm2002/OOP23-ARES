package it.unibo.ares.core.api;

import it.unibo.ares.core.utils.pos.Pos;
import it.unibo.ares.core.utils.statistics.Statistics;

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

    /**
     * Checks if the simulation is finished.
     *
     * @return true if the simulation is finished, false otherwise.
     */
    boolean isFinished();

    /**
     * Ritorna le statistiche di modello
     * 
     * @return le statistiche del modello
     */
    Statistics getStatistics();

}
