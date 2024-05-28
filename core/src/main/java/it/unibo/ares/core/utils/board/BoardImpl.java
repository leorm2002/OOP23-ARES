package it.unibo.ares.core.utils.board;

import it.unibo.ares.core.utils.Pair;
import it.unibo.ares.core.utils.pos.Pos;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.io.Serializable;

/**
 * Implementation of the Board interface.
 * This class represents a board that stores entities at different positions.
 * 
 * @param <V> the type of entities stored in the board
 */
public final class BoardImpl<V extends Serializable> implements Board<V> {
    private static final long serialVersionUID = 1L;
    private final Map<Pos, V> entities;

    /**
     * Create a new board.
     */
    public BoardImpl() {
        this.entities = new HashMap<>();
    }

    /*
     * {@inheritDoc}
     */
    @Override
    public Set<Pair<Pos, V>> getEntities() {
        return entities.entrySet().stream()
                .map(e -> new Pair<Pos, V>(e.getKey(), e.getValue()))
                .collect(Collectors.toSet());
    }

    /*
     * {@inheritDoc}
     */
    @Override
    public void addEntity(final Pos pos, final V entity) {
        Optional.ofNullable(entities.get(pos)).ifPresent(nullEntity -> {
            throw new IllegalArgumentException("Position " + pos + " is already occupied by " + nullEntity);
        });
        entities.put(pos, entity);
    }

    /*
     * {@inheritDoc}
     */
    @Override
    public void removeEntity(final Pos pos, final V entity) {
        Optional.ofNullable(entities.get(pos)).orElseThrow(
                () -> new IllegalArgumentException("Position " + pos + " is not occupied"));
        entities.remove(pos, entity);
    }

    /*
     * {@inheritDoc}
     */
    @Override
    public Optional<V> getEntity(final Pos pos) {
        return Optional.ofNullable(entities.get(pos));
    }
}
