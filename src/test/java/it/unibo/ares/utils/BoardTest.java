package it.unibo.ares.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertThrows;
import java.util.Optional;
import java.util.Set;
import org.junit.jupiter.api.Test;
import it.unibo.ares.utils.board.Board;
import it.unibo.ares.utils.board.BoardImpl;
import it.unibo.ares.utils.pos.Pos;
import it.unibo.ares.utils.pos.PosImpl;

/**
 * Unit test for {@link Board}.
 */
public class BoardTest {
    /**
     * Test adding an entity to the board.
     */
    @Test
    public void testAddEntity() {
        Board<String> board = new BoardImpl<>();
        Pos pos = new PosImpl(0, 0);
        String entity = "Entity";
        board.addEntity(pos, entity);
        Optional<String> retrievedEntity = board.getEntity(pos);
        assertTrue(retrievedEntity.isPresent());
        assertEquals(entity, retrievedEntity.get());
    }
    /**
     * Test removing an entity from the board.
     */
    @Test
    public void testRemoveEntity() {
        Board<String> board = new BoardImpl<>();
        Pos pos = new PosImpl(0, 0);
        String entity = "Entity";
        board.addEntity(pos, entity);
        board.removeEntity(pos, entity);
        Optional<String> retrievedEntity = board.getEntity(pos);
        assertFalse(retrievedEntity.isPresent());
    }
    /**
     * Test getting all the entities from the board.
     */
    @Test
    public void testGetEntities() {
        Board<String> board = new BoardImpl<>();
        Pos pos1 = new PosImpl(0, 0);
        Pos pos2 = new PosImpl(1, 1);
        String entity1 = "Entity1";
        String entity2 = "Entity2";
        board.addEntity(pos1, entity1);
        board.addEntity(pos2, entity2);
        Set<Pair<Pos, String>> entities = board.getEntities();
        assertEquals(2, entities.size());
        assertTrue(entities.contains(new Pair<>(pos1, entity1)));
        assertTrue(entities.contains(new Pair<>(pos2, entity2)));
    }
    /**
     * Test throwing an IllegalArgumentException when trying to add an entity to a position already occupied.
     */
    @Test
    public void testThrowingDuplicateAdd() {
        Board<String> board = new BoardImpl<>();
        Pos pos = new PosImpl(0, 0);
        String entity = "Entity";
        assertThrows(IllegalArgumentException.class, () -> {
            board.addEntity(pos, entity);
            board.addEntity(pos, entity);
        });
    }
    /**
     * Test throwing an IllegalArgumentException when trying to remove an entity from a position where it is not present.
     */
    @Test
    public void testThrowingRemoveNotPresent() {
        Board<String> board = new BoardImpl<>();
        Pos pos = new PosImpl(0, 0);
        String entity = "Entity";
        assertThrows(IllegalArgumentException.class, () -> {
            board.removeEntity(pos, entity);
        });
    }
}