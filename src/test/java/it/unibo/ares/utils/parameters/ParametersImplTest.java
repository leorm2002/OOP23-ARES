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

/**
 * Unit test for {@link ParametersImpl}.
 */
public class ParametersImplTest {
    private ParametersImpl parameters;

    /**
     * Istantiate a new ParametersImpl before each test.
     */
    @BeforeEach
    public void setUp() {
        parameters = new ParametersImpl();
    }

    /**
     * Test adding a parameter with a type.
     */
    @Test
    void testAddParameterWithType() {
        String key = "testKey";
        Class<Integer> type = Integer.class;

        parameters.addParameter(key, type);

        Optional<Parameter<Integer>> parameter = parameters.getParameter(key, type);
        assertTrue(parameter.isPresent());
        assertFalse(parameter.get().isSetted());
    }
    /**
     * Test adding a parameter with a value.
     */
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
    /**
     * Test getting a parameter.
     */
    @Test
    void testGetParameter() {
        String key = "testKey";
        Integer value = 10;

        parameters.addParameter(key, value);

        Optional<Parameter<Integer>> parameter = parameters.getParameter(key, Integer.class);
        assertTrue(parameter.isPresent());
        assertEquals(value, parameter.get().getValue());
    }

    /**
     * Test setting a parameter.
     */
    @Test
    void testSetParameter() {
        String key = "testKey";
        // CHECKSTYLE: MagicNumber OFF just two casual value to test
        Integer value = 10;
        Integer newValue = 20;
        // CHECKSTYLE: MagicNumber ON
        parameters.addParameter(key, value);
        parameters.setParameter(key, newValue);
        Optional<Parameter<Integer>> parameter = parameters.getParameter(key, Integer.class);
        assertTrue(parameter.isPresent());
        assertTrue(parameter.get().isSetted());
        assertEquals(newValue, parameter.get().getValue());
    }
    /**
     * Test getting all the parameters.
     */
    @Test
    void testGetParameters() {
        String key1 = "key1";
        String key2 = "key2";
        // CHECKSTYLE: MagicNumber OFF just two casual value to test
        Integer value1 = 10;
        Integer value2 = 20;
        // CHECKSTYLE: MagicNumber ON

        parameters.addParameter(key1, value1);
        parameters.addParameter(key2, value2);

        Set<Parameter<?>> parameterSet = parameters.getParameters();
        assertEquals(2, parameterSet.size());

        for (Parameter<?> parameter : parameterSet) {
            assertTrue(parameter.isSetted());
        }
    }

    /**
     * Test getting all the parameters that are not setted.
     */
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

    /**
     * Test adding an existing parameter.
     */
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

    /**
     * Test cloning the parameters.
     */
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
    /**
     * Test adding a parameter with a null key and verify that a NullPointerException is thrown.
     */
    @Test
    void testAddParameterWithNullKey() {
        assertThrows(NullPointerException.class, () -> {
            parameters.addParameter(null, Integer.class);
        });
    }
    /**
     * Test adding a parameter with a duplicate key and verify that a IllegalArgumentException is thrown.
     */
    @Test
    void testAddParameterWithDuplicateKey() {
        String key = "testKey";
        Integer value1 = 10;
        parameters.addParameter(key, value1);

        assertThrows(IllegalArgumentException.class, () -> {
            // CHECKSTYLE: MagicNumber OFF just a casual different value to test
            parameters.addParameter(key, 20);
            // CHECKSTYLE: MagicNumber ON
        });
    }
    /**
     * Test adding a parameter with a duplicate key of a different type and verify that a IllegalArgumentException is thrown.
     */
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
    /**
     * Test setting a non existing parameter and verify that a NullPointerException is thrown.
     */
    @Test
    void testSetMissingParametr() {
        String key = "testKey";
        Integer value = 10;

        assertThrows(IllegalArgumentException.class, () -> {
            parameters.setParameter(key, value);
        });
    }
    /**
     * Test setting a parameter originally created from an existing value with a type different that
     * the right one and verify that a IllegalArgumentException is thrown.
     */
    @Test
    void testSetParametrOfWrongTypeWithValue() {
        String key = "testKey";
        Integer value = 10;

        parameters.addParameter(key, value);

        assertThrows(IllegalArgumentException.class, () -> {
            parameters.setParameter(key, "test");
        });
    }
    /**
     * Test setting a parameter originally created declaring the expected type with a type different
     *  that the right one and verify that a IllegalArgumentException is thrown.
     */
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
