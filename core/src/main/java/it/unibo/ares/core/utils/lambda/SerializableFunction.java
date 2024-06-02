package it.unibo.ares.core.utils.lambda;

import java.io.Serializable;
import java.util.function.BiPredicate;

public interface SerializableFunction<T, V> extends BiPredicate<T, V>, Serializable {

}
