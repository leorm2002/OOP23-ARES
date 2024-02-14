package it.unibo.ares.core.agent;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.unibo.ares.core.agent.EntityImpl;

/**
 * Unit test for {@link EntityImpl}.
 */
public final class EntityImplTest {

    private EntityImpl entity;

    /**
     * Istantiate a new EntityImpl before each test.
     */
    @BeforeEach
    public void setUp() {
        entity = new EntityImpl();
    }

    /**
     * Should throw an IllegalStateException if the name is not set and we try to
     * get it.
     */
    @Test
    void testGetName() {
        assertThrows(IllegalStateException.class, () -> {
            entity.getName();
        });
    }

    /**
     * Test setting a name.
     */
    @Test
    void testSetName() {
        String name = "Test Name";
        entity.setName(name);
        assertEquals(name, entity.getName());
    }

    /**
     * Should throw an IllegalArgumentException if we try to set a null name.
     */
    @Test
    void testSetNameWithNull() {
        assertThrows(IllegalArgumentException.class, () -> {
            entity.setName(null);
        });
    }
}
