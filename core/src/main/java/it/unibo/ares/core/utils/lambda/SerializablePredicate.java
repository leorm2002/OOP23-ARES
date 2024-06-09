package it.unibo.ares.core.utils.lambda;

import java.io.Serializable;
import java.util.function.Predicate;

/**
 * A functional interface that represents a serializable predicate.
 * 
 * This interface extends the {@link Predicate} interface and the
 * {@link Serializable} interface.
 * It can be used to define a predicate that can be serialized and deserialized.
 * 
 * @param <T> the type of the input to the predicate
 */
public interface SerializablePredicate<T> extends Predicate<T>, Serializable {

}
