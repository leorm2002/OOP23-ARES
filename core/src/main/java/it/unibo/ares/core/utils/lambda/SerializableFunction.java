package it.unibo.ares.core.utils.lambda;

import java.io.Serializable;
import java.util.function.Function;

public interface SerializableFunction<T, V> extends Function<T, V>, Serializable {

}
