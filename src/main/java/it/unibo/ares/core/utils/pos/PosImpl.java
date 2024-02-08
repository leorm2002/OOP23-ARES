package it.unibo.ares.core.utils.pos;

import java.util.Objects;

/**
 * A class that represents a position in a two-dimensional space.
 */
public class PosImpl implements Pos {
    /**
     * Constructs a new PosImpl object with the specified x and y coordinates.
     *
     * @param x the x coordinate of the position
     * @param y the y coordinate of the position
     */
    public PosImpl(final Integer x, final Integer y) {
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
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
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

    /**
     * {@inheritDoc}
     */
    @Override
    public Pos diff(final Pos pos) {
        return new PosImpl(this.getX() - pos.getX(), this.getY() - pos.getY());
    }
}
