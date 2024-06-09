package it.unibo.ares.core.utils.lambda;

import java.io.Serializable;
import java.util.function.Function;

/**
 * A functional interface that extends the {@link java.util.function.Function} interface and
 * {@link java.io.Serializable} interface. It represents a function that takes an argument of type T
 * and returns a result of type V. This interface is designed to be serializable, allowing instances
 * of this interface to be transferred over the network or stored in a file.
 *
 * @param <T> the type of the input to the function
 * @param <V> the type of the result of the function
 */
public interface SerializableFunction<T, V> extends Function<T, V>, Serializable {

}
