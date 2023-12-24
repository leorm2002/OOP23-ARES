package it.unibo.ares.utils.parameters;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ParametersImplTest {
    private ParametersImpl parameters;

    @BeforeEach
    public void setUp() {
        parameters = new ParametersImpl();
    }

    @Test
    void testAddParameterWithType() {
        String key = "testKey";
        Class<Integer> type = Integer.class;

        parameters.addParameter(key, type);

        Optional<Parameter<Integer>> parameter = parameters.getParameter(key, type);
        assertTrue(parameter.isPresent());
        assertFalse(parameter.get().isSetted());
    }

    @Test
    void testAddParameterWithValue() {
        String key = "testKey";
        Integer value = 10;

        parameters.addParameter(key, value);

        Optional<Parameter<Integer>>  parameter = parameters.getParameter(key, Integer.class);
        assertTrue(parameter.isPresent());
        assertTrue(parameter.get().isSetted());
        assertEquals(value, parameter.get().getValue());
    }

    @Test
    void testGetParameter() {
        String key = "testKey";
        Integer value = 10;

        parameters.addParameter(key, value);

        Optional<Parameter<Integer>> parameter = parameters.getParameter(key, Integer.class);
        assertTrue(parameter.isPresent());
        assertEquals(value, parameter.get().getValue());
    }

    @Test
    void testSetParameter() {
        String key = "testKey";
        Integer value = 10;

        parameters.addParameter(key, value);

        parameters.setParameter(key, 20);

        Optional<Parameter<Integer>> parameter = parameters.getParameter(key, Integer.class);
        assertTrue(parameter.isPresent());
        assertTrue(parameter.get().isSetted());
        assertEquals(20, parameter.get().getValue());
    }

    @Test
    void testGetParameters() {
        String key1 = "key1";
        String key2 = "key2";
        Integer value1 = 10;
        Integer value2 = 20;

        parameters.addParameter(key1, value1);
        parameters.addParameter(key2, value2);

        Set<Parameter<?>> parameterSet = parameters.getParameters();
        assertEquals(2, parameterSet.size());

        for (Parameter<?> parameter : parameterSet) {
            assertTrue(parameter.isSetted());
        }
    }

    @Test
    void testGetParametersToset() {
        String key1 = "key1";
        String key2 = "key2";
        Integer value1 = 10;

        parameters.addParameter(key1, value1);
        parameters.addParameter(key2, Integer.class);

        Set<Parameter<?>> parameterSet = parameters.getParametersToset();
        assertEquals(1, parameterSet.size());

        for (Parameter<?> parameter : parameterSet) {
            assertFalse(parameter.isSetted());
        }
    }

    @Test
    void testAddParameterWithParameter() {
        String key = "testKey";
        Integer value = 10;
        Parameter<Integer> parameter = new ParameterImpl<>(key, value);

        parameters.addParameter(parameter);

        Optional<Parameter<Integer>> retrievedParameter = parameters.getParameter(key, Integer.class);
        assertTrue(retrievedParameter.isPresent());
        assertTrue(retrievedParameter.get().isSetted());
        assertEquals(value, retrievedParameter.get().getValue());
    }

    @Test
    void testClone() {
        String key = "testKey";
        Integer value = 10;

        parameters.addParameter(key, value);

        Parameters clone = parameters.clone();

        Optional<Parameter<Integer>> parameter = clone.getParameter(key, Integer.class);
        assertTrue(parameter.isPresent());
        assertTrue(parameter.get().isSetted());
        assertEquals(value, parameter.get().getValue());
        assertNotSame(parameters, clone);
    }

@Test
void testAddParameterWithNullKey() {
    assertThrows(NullPointerException.class, () -> {
        parameters.addParameter(null, Integer.class);
    });
}

@Test
void testAddParameterWithDuplicateKey() {
    String key = "testKey";
    Integer value1 = 10;
    Integer value2 = 20;

    parameters.addParameter(key, value1);

    assertThrows(IllegalArgumentException.class, () -> {
        parameters.addParameter(key, value2);
    });
}

@Test
void testAddParameterWithDuplicateKeyOfDifferentType() {
    String key = "testKey";
    Integer value1 = 10;
    String value2 = "prova";

    parameters.addParameter(key, value1);

    assertThrows(IllegalArgumentException.class, () -> {
        parameters.addParameter(key, value2);
    });
}

@Test
void testSetMissingParametr() {
    String key = "testKey";
    Integer value = 10;

    assertThrows(IllegalArgumentException.class, () -> {
        parameters.setParameter(key, value);
    });
}

@Test
void testSetParametrOfWrongTypeWithValue() {
    String key = "testKey";
    Integer value = 10;

    parameters.addParameter(key, value);

    assertThrows(IllegalArgumentException.class, () -> {
        parameters.setParameter(key, "test");
    });
}
@Test
void testSetParametrOfWrongTypeWithType() {
    String key = "testKey";
    Class<Integer> type = Integer.class;

    parameters.addParameter(key, type);

    assertThrows(IllegalArgumentException.class, () -> {
        parameters.setParameter(key, "test");
    });
}
}
