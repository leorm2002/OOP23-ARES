package it.unibo.ares.utils.parameters;

import static org.junit.jupiter.api.Assertions.*;

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
        assertThrows( IllegalStateException.class , 
            () -> parameter.getValue()
        );
        assertEquals(type, parameter.getType());
        assertFalse(parameter.isSetted());
    }

    @Test
    void testSetValue() {
        String key = "testKey";
        Integer value = 10;
        ParameterImpl<Integer> parameter = new ParameterImpl<>(key, Integer.class);

        assertFalse(parameter.isSetted());
        assertThrows( IllegalStateException.class ,
            () -> parameter.getValue()
        );

        var settedParameter = parameter.setValue(value);

        assertTrue(settedParameter.isSetted());
        assertEquals(value, settedParameter.getValue());
    }
}
