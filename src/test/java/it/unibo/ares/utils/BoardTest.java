package it.unibo.ares.utils;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;

import it.unibo.ares.utils.board.Board;
import it.unibo.ares.utils.board.BoardImpl;
import it.unibo.ares.utils.pos.Pos;
import it.unibo.ares.utils.pos.PosImpl;

public class BoardTest {
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

    @Test
    public void testThrowingDuplicateAdd(){
        Board<String> board = new BoardImpl<>();
        Pos pos = new PosImpl(0, 0);
        String entity = "Entity";
        assertThrows(IllegalArgumentException.class, () -> {
            board.addEntity(pos, entity);
            board.addEntity(pos, entity);
        });
    }

    @Test
    public void testThrowingRemoveNotPresent(){
        Board<String> board = new BoardImpl<>();
        Pos pos = new PosImpl(0, 0);
        String entity = "Entity";
        assertThrows(IllegalArgumentException.class, () -> {
            board.removeEntity(pos, entity);
        });
    }
}