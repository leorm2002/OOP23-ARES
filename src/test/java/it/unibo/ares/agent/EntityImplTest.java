package it.unibo.ares.agent;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class EntityImplTest {
    
    private EntityImpl entity;

    @BeforeEach
    public void setUp() {
        entity = new EntityImpl();
    }

    @Test
    void testGetName() {
        assertThrows(IllegalStateException.class, () -> {
            entity.getName();
        });
    }

    @Test
    void testSetName() {
        String name = "Test Name";
        entity.setName(name);
        assertEquals(name, entity.getName());
    }

    @Test
    void testSetNameWithNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            entity.setName(null);
        });
    }
}