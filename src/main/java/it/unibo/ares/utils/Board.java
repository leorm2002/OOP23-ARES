package it.unibo.ares.utils;

import java.util.Optional;
import java.util.Set;

/**
 * Represents a board that stores elements of type V associated with keys of type K.
 *
 * @param <V> the type of the elements
 */
public interface Board<V> {
    Set<Pair<Pos, V>> getEntities();
    void addEntity(Pos pos, V entity);
    void removeEntity(Pos pos, V entity);
    Optional<V> getEntity(Pos pos);

}
