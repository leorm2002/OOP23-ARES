package it.unibo.ares.core.utils.lambda;

import java.io.Serializable;
import java.util.function.BiFunction;

public interface SerializableBiFunction<T, V, G> extends BiFunction<T, V, G>, Serializable {

}
