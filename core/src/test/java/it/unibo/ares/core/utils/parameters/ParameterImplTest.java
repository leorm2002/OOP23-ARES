package it.unibo.ares.core.utils.parameters;

import it.unibo.ares.core.utils.parameters.ParameterImpl;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for {@link ParameterImpl}.
 */
public class ParameterImplTest {
    /**
     * Test the constructor with a value.
     */
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

    /**
     * Test the constructor with a type.
     */
    @Test
    void testConstructorWithType() {
        String key = "testKey";
        Class<Integer> type = Integer.class;
        ParameterImpl<Integer> parameter = new ParameterImpl<>(key, type);

        assertEquals(key, parameter.getKey());
        assertThrows(IllegalStateException.class,
                () -> parameter.getValue());
        assertEquals(type, parameter.getType());
        assertFalse(parameter.isSetted());
    }

    /**
     * Verify that an IllegalStateException is thrown when trying to get the value
     * of a parameter that has not been setted.
     * Verify we're getting the right value when the parameter has been setted.
     */
    @Test
    void testSetValue() {
        String key = "testKey";
        Integer value = 10;
        ParameterImpl<Integer> parameter = new ParameterImpl<>(key, Integer.class);

        assertFalse(parameter.isSetted());
        assertThrows(IllegalStateException.class,
                () -> parameter.getValue());

        var settedParameter = parameter.setValue(value);

        assertTrue(settedParameter.isSetted());
        assertEquals(value, settedParameter.getValue());
    }
}
