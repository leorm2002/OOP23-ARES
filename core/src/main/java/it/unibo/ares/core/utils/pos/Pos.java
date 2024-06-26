package it.unibo.ares.core.utils.pos;

import java.io.Serializable;

/**
 * Pos represents a position with x and y coordinates.
 */
public interface Pos extends Serializable {
    /**
     * Returns the x-coordinate of the position.
     *
     * @return the x-coordinate of the position
     */
    Integer getX();

    /**
     * Returns the y-coordinate of the position.
     *
     * @return the y-coordinate of the position
     */
    Integer getY();

    /**
     * Returns the distance between this position and the given position.
     * 
     * @param pos
     * @return a new Pos object representing the distance between this position and
     *         the given position
     */
    Pos diff(Pos pos);

}
