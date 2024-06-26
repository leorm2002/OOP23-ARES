package it.unibo.ares.core.utils.board;

import it.unibo.ares.core.utils.Pair;
import it.unibo.ares.core.utils.pos.Pos;

import java.io.Serializable;
import java.util.Optional;
import java.util.Set;

/**
 * A board is a 2D grid, we associate a generica entity of type V to a position
 * Pos.
 *
 * @param <V> the type of entities stored in the board
 */
public interface Board<V extends Serializable> extends Serializable {
    /**
     * Retrieves a set of pairs containing the positions and entities on the board.
     *
     * @return a set of pairs representing the entities on the board
     */
    Set<Pair<Pos, V>> getEntities();

    /**
     * Adds an entity to the specified position on the board.
     *
     * @param pos    the position where the entity should be added
     * @param entity the entity to be added
     * @throws IllegalArgumentException if the position is already occupied
     */
    void addEntity(Pos pos, V entity);

    /**
     * Removes an entity from the specified position on the board.
     *
     * @param pos    the position from where the entity should be removed
     * @param entity the entity to be removed
     * @throws IllegalArgumentException if the position is not occupied
     */
    void removeEntity(Pos pos, V entity);

    /**
     * Retrieves the entity at the specified position on the board, if any.
     *
     * @param pos the position to retrieve the entity from
     * @return an optional containing the entity at the specified position, or empty
     *         if no entity is present
     */
    Optional<V> getEntity(Pos pos);
}
