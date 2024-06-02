package it.unibo.ares.core.utils.lambda;

import java.io.Serializable;
import java.util.function.BiPredicate;

public interface SerializableBiPredicate<T, V> extends BiPredicate<T, V>, Serializable {

}
