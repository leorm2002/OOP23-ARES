package it.unibo.ares.core.utils.parameters;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

/**
 * Test class for {@link ParameterImpl}.
 */
class ParameterImplTest {
    private static final String KEY = "testKey";

    /**
     * Test the constructor with a value.
     */
    @Test
    void testConstructorWithValue() {
        final Integer value = 10;
        final ParameterImpl<Integer> parameter = new ParameterImpl<>(KEY, value, true);

        assertEquals(KEY, parameter.getKey());
        assertEquals(value, parameter.getValue());
        assertEquals(Integer.class, parameter.getType());
        assertTrue(parameter.isSetted());
    }

    /**
     * Test the constructor with a type.
     */
    @Test
    void testConstructorWithType() {
        final Class<Integer> type = Integer.class;
        final ParameterImpl<Integer> parameter = new ParameterImpl<>(KEY, type, true);

        assertEquals(KEY, parameter.getKey());
        assertThrows(IllegalStateException.class,
                parameter::getValue);
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
        final Integer value = 10;
        final ParameterImpl<Integer> parameter = new ParameterImpl<>(KEY, Integer.class, true);

        assertFalse(parameter.isSetted());
        assertThrows(IllegalStateException.class,
                parameter::getValue);

        final ParameterImpl<Integer> settedParameter = parameter.updateValue(value);

        assertTrue(settedParameter.isSetted());
        assertEquals(value, settedParameter.getValue());
    }
}
