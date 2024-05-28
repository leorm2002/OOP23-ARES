package it.unibo.ares.core.utils;

import java.io.Serializable;

/**
 * Represents an immutable pair of generic values.
 *
 * @param <T> the type of the first value
 * @param <U> the type of the second value
 */
public class Pair<T extends Serializable, U extends Serializable> implements Serializable {
    private static final long serialVersionUID = 1L;
    private final T first;
    private final U second;

    /**
     * Constructs a new Pair object with the specified values.
     *
     * @param first  the first value of the pair
     * @param second the second value of the pair
     */
    public Pair(final T first, final U second) {
        this.first = first;
        this.second = second;
    }

    /**
     * Gets the first value of the pair.
     *
     * @return the first value
     */
    public T getFirst() {
        return first;
    }

    /**
     * Gets the second value of the pair.
     *
     * @return the second value
     */
    public U getSecond() {
        return second;
    }

    /**
     * Returns a string representation of the pair.
     *
     * @return a string representation of the pair
     */
    @Override
    public String toString() {
        return "(" + first + ", " + second + ")";
    }

    /**
     * Checks if this pair is equal to another object.
     *
     * @param o the object to compare
     * @return true if the objects are equal, false otherwise
     */
    @Override
    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        }
        if (!(o instanceof Pair)) {
            return false;
        }
        final Pair<?, ?> p = (Pair<?, ?>) o;
        return p.first.equals(first) && p.second.equals(second);
    }

    /**
     * Returns the hash code value for the pair.
     *
     * @return the hash code value for the pair
     */
    @Override
    public int hashCode() {
        return first.hashCode() + second.hashCode();
    }
}
