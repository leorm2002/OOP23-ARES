package it.unibo.ares.core.utils.lambda;

import java.io.Serializable;
import java.util.function.BiPredicate;

/**
 * A functional interface that represents a serializable binary predicate
 * (two-argument predicate) operation.
 * It extends the {@link java.util.function.BiPredicate} interface and the
 * {@link java.io.Serializable} interface.
 *
 * @param <T> the type of the first argument to the predicate
 * @param <V> the type of the second argument to the predicate
 */
public interface SerializableBiPredicate<T, V> extends BiPredicate<T, V>, Serializable {

}
