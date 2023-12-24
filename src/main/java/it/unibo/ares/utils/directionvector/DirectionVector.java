package it.unibo.ares.utils.directionvector;

/**
 * Represents a vector in a two-dimensional space.
 */
public interface DirectionVector {

    /**
     * Gets the x-component of the direction vector.
     *
     * @return the x-component of the direction vector.
     */
    Double getX();

    /**
     * Gets the y-component of the direction vector.
     *
     * @return the y-component of the direction vector.
     */
    Double getY();

    /**
     * Gets the x-component of the direction vector.
     *
     * @return the x-component of the direction vector normalized to have a magnitude of 1.
     */
    Double getNormalizedX();

    /**
     * Gets the y-component of the direction vector.
     *
     * @return the y-component of the direction vector normalized to have a magnitude of 1.
     */
    Double getNormalizedY();

    /**
     * Gets the magnitude of the direction vector.
     *
     * @return the magnitude of the direction vector.
     */
    Double getMagnitude();

    /**
     * Computes the dot product between this vector and the given vector.
     * @param other vector to compute the dot product with
     * @return the dot product between this vector and the given vector.
     */
    Double pointProduct(DirectionVector other);

    /**
     * Return the normalized version of this vector.
     * @return a vector with the same direction of this vector but with magnitude 1.
     */
    DirectionVector getNormalized();

    /**
     * Adds the given vector to this vector.
     * @param other vector to add to this vector
     * @return a new vector representing the mean of this vector and the given vector.
     */
    DirectionVector mean(DirectionVector other);

}