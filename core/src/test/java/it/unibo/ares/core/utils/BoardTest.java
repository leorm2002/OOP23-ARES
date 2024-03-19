package it.unibo.ares.core.utils;

import it.unibo.ares.core.utils.board.Board;
import it.unibo.ares.core.utils.board.BoardImpl;
import it.unibo.ares.core.utils.pos.Pos;
import it.unibo.ares.core.utils.pos.PosImpl;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit test for {@link Board}.
 */
class BoardTest {
    /**
     * Test adding an entity to the board.
     */
    @Test
    void testAddEntity() {
        final Board<String> board = new BoardImpl<>();
        final Pos pos = new PosImpl(0, 0);
        final String entity = "Entity";
        board.addEntity(pos, entity);
        final Optional<String> retrievedEntity = board.getEntity(pos);
        assertTrue(retrievedEntity.isPresent());
        assertEquals(entity, retrievedEntity.get());
    }

    /**
     * Test removing an entity from the board.
     */
    @Test
    void testRemoveEntity() {
        final Board<String> board = new BoardImpl<>();
        final Pos pos = new PosImpl(0, 0);
        final String entity = "Entity";
        board.addEntity(pos, entity);
        board.removeEntity(pos, entity);
        final Optional<String> retrievedEntity = board.getEntity(pos);
        assertFalse(retrievedEntity.isPresent());
    }

    /**
     * Test getting all the entities from the board.
     */
    @Test
    void testGetEntities() {
        final Board<String> board = new BoardImpl<>();
        final Pos pos1 = new PosImpl(0, 0);
        final Pos pos2 = new PosImpl(1, 1);
        final String entity1 = "Entity1";
        final String entity2 = "Entity2";
        board.addEntity(pos1, entity1);
        board.addEntity(pos2, entity2);
        final Set<Pair<Pos, String>> entities = board.getEntities();
        assertEquals(2, entities.size());
        assertTrue(entities.contains(new Pair<>(pos1, entity1)));
        assertTrue(entities.contains(new Pair<>(pos2, entity2)));
    }

    /**
     * Test throwing an IllegalArgumentException when trying to add an entity to a
     * position already occupied.
     */
    @Test
    void testThrowingDuplicateAdd() {
        final Board<String> board = new BoardImpl<>();
        final Pos pos = new PosImpl(0, 0);
        final String entity = "Entity";
        board.addEntity(pos, entity);
        assertThrows(IllegalArgumentException.class, () -> {
            board.addEntity(pos, entity);
        });
    }

    /**
     * Test throwing an IllegalArgumentException when trying to remove an entity
     * from a position where it is not present.
     */
    @Test
    void testThrowingRemoveNotPresent() {
        final Board<String> board = new BoardImpl<>();
        final Pos pos = new PosImpl(0, 0);
        final String entity = "Entity";
        assertThrows(IllegalArgumentException.class, () -> {
            board.removeEntity(pos, entity);
        });
    }
}
