package it.unibo.ares.utils.parameters;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

public class ParameterImplTest {
   @Test
    void testConstructorWithValue() {
        String key = "testKey";
        Integer value = 10;
        ParameterImpl<Integer> parameter = new ParameterImpl<>(key, value);

        assertEquals(key, parameter.getKey());
        assertEquals(value, parameter.getValue());
        assertEquals(Integer.class, parameter.getType());
        assertTrue(parameter.isSetted());
    }

    @Test
    void testConstructorWithType() {
        String key = "testKey";
        Class<Integer> type = Integer.class;
        ParameterImpl<Integer> parameter = new ParameterImpl<>(key, type);

        assertEquals(key, parameter.getKey());
        assertNull(parameter.getValue());
        assertEquals(type, parameter.getType());
        assertFalse(parameter.isSetted());
    }

    @Test
    void testSetValue() {
        String key = "testKey";
        Integer value = 10;
        ParameterImpl<Integer> parameter = new ParameterImpl<>(key, Integer.class);

        assertFalse(parameter.isSetted());
        assertNull(parameter.getValue());

        parameter.setValue(value);

        assertTrue(parameter.isSetted());
        assertEquals(value, parameter.getValue());
    }
}
