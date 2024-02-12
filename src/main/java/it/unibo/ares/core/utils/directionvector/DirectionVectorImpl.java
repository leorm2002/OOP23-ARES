package it.unibo.ares.core.utils.directionvector;

import javax.annotation.concurrent.Immutable;

/**
 * Implementation of the DirectionVector interface.
 * Represents a vector with x and y components.
 */
@Immutable
public class DirectionVectorImpl implements DirectionVector {
    private final Double x;
    private final Double y;

    private final Double magnitude;

    /**
     * Constructs a DirectionVectorImpl object with the given x and y components.
     * The vector is normalized to have a magnitude of 1.
     *
     * @param x the x component of the vector
     * @param y the y component of the vector
     */
    public DirectionVectorImpl(final Double x, final Double y) {
        this.x = x;
        this.y = y;
        this.magnitude = Math.sqrt(x * x + y * y);
    }

    /**
     * Constructs a DirectionVectorImpl object with the given x and y components.
     * The vector is normalized to have a magnitude of 1.
     *
     * @param x the x component of the vector as integer
     * @param y the y component of the vector as integer
     */
    public DirectionVectorImpl(final Integer x, final Integer y) {
        this(x.doubleValue(), y.doubleValue());
    }

    /**
     * @inheritDoc
     */
    @Override
    public Double getX() {
        return x;
    }

    /**
     * @inheritDoc
     */
    @Override
    public Double getY() {
        return y;
    }

    /**
     * @inheritDoc
     */
    @Override
    public Double getNormalizedX() {
        return x / magnitude;
    }

    /**
     * @inheritDoc
     */
    @Override
    public Double getNormalizedY() {
        return y / magnitude;
    }

    /**
     * @inheritDoc
     */
    @Override
    public Double getMagnitude() {
        return magnitude;
    }

    /**
     * @inheritDoc
     */
    @Override
    public Double pointProduct(final DirectionVector other) {
        return x * other.getX() + y * other.getY();
    }

    /**
     * @inheritDoc
     */
    @Override
    public DirectionVector getNormalized() {
        return new DirectionVectorImpl(x / magnitude, y / magnitude);
    }

    /**
     * @inheritDoc
     */
    @Override
    public DirectionVector mean(final DirectionVector other) {
        return new DirectionVectorImpl((x + other.getX()), (y + other.getY()));
    }
}
