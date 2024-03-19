package it.unibo.ares.core.agent;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit test for {@link EntityImpl}.
 */
@SuppressWarnings("UWF_FIELD_NOT_INITIALIZED_IN_CONSTRUCTOR")
final class EntityImplTest {

    /**
     * Should throw an IllegalStateException if the name is not set and we try to
     * get it.
     */
    @Test
    void testGetName() {
        EntityImpl entity = new EntityImpl();
        assertThrows(IllegalStateException.class, () -> {
            entity.getName();
        });
    }

    /**
     * Test setting a name.
     */
    @Test
    void testSetName() {
        EntityImpl entity = new EntityImpl();
        String name = "Test Name";
        entity.setName(name);
        assertEquals(name, entity.getName());
    }

    /**
     * Should throw an IllegalArgumentException if we try to set a null name.
     */
    @Test
    void testSetNameWithNull() {
        EntityImpl entity = new EntityImpl();
        assertThrows(IllegalArgumentException.class, () -> {
            entity.setName(null);
        });
    }
}
