package it.unibo.ares.core.utils.pos;

import java.util.Objects;

/**
 * A class that represents a position in a two-dimensional space.
 */
public class PosImpl implements Pos {

    private static final long serialVersionUID = 1L;
    private final Integer y;
    private final Integer x;

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

    /**
     * Constructs a new PosImpl object with the specified x and y coordinates
     * takes double as input, they will be rounded to the nearest integer.
     *
     * @param x the x coordinate of the position
     * @param y the y coordinate of the position
     */
    public PosImpl(final Double x, final Double y) {
        this.x = Math.round(x.floatValue());
        this.y = Math.round(y.floatValue());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer getX() {
        return this.x;
    }

    /**
     * {@inheritDoc}
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
        final PosImpl pos = (PosImpl) o;
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
