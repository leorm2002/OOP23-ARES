package it.unibo.ares.core.utils.lambda;

import java.io.Serializable;
import java.util.function.BiFunction;

/**
 * Represents a serializable function that accepts two arguments and produces a result.
 * This is a functional interface whose functional method is {@link #apply(Object, Object)}.
 *
 * @param <T> the type of the first argument to the function
 * @param <V> the type of the second argument to the function
 * @param <G> the type of the result of the function
 */
public interface SerializableBiFunction<T, V, G> extends BiFunction<T, V, G>, Serializable {

}
