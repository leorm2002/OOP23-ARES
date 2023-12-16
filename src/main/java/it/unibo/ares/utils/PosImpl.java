package it.unibo.ares.utils;

import java.util.Objects;


/**
 *  {@inheritDoc}
 */
public class PosImpl implements Pos {
    /**
     * Constructs a new PosImpl object with the specified x and y coordinates.
     *
     * @param x the x coordinate of the position
     * @param y the y coordinate of the position
     */
    public PosImpl(Integer x, Integer y) {
        this.x = x;
        this.y = y;
    }

    private final Integer y;    
    private final Integer x;


    /**
    *  {@inheritDoc}
    */
    @Override
    public Integer getX() {
        return this.x;
    }

    /**
    *  {@inheritDoc}
    */
    @Override
    public Integer getY() {
        return this.y;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PosImpl pos = (PosImpl) o;
        return Objects.equals(x, pos.x) && Objects.equals(y, pos.y);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int hashCode() {
        return Objects.hash(x, y);
    }
}
