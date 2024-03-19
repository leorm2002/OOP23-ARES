package it.unibo.ares.core.utils.parameters;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Optional;
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit test for {@link ParametersImpl}.
 */
class ParametersImplTest {
    private ParametersImpl parameters;
    private static final String KEY = "testKey";

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
        final Class<Integer> type = Integer.class;

        parameters.addParameter(KEY, type, true);

        final Optional<Parameter<Integer>> parameter = parameters.getParameter(KEY, type);
        assertTrue(parameter.isPresent());
        assertFalse(parameter.get().isSetted());
    }

    /**
     * Test adding a parameter with a value.
     */
    @Test
    void testAddParameterWithValue() {
        final Integer value = 10;

        parameters.addParameter(KEY, value, true);

        final Optional<Parameter<Integer>> parameter = parameters.getParameter(KEY, Integer.class);
        assertTrue(parameter.isPresent());
        assertTrue(parameter.get().isSetted());
        assertEquals(value, parameter.get().getValue());
    }

    /**
     * Test getting a parameter.
     */
    @Test
    void testGetParameter() {
        final Integer value = 10;

        parameters.addParameter(KEY, value, true);

        final Optional<Parameter<Integer>> parameter = parameters.getParameter(KEY, Integer.class);
        assertTrue(parameter.isPresent());
        assertEquals(value, parameter.get().getValue());
    }

    /**
     * Test setting a parameter.
     */
    @Test
    void testSetParameter() {
        // CHECKSTYLE: MagicNumber OFF just two casual value to test
        final Integer value = 10;
        final Integer newValue = 20;
        // CHECKSTYLE: MagicNumber ON
        parameters.addParameter(KEY, value, true);
        parameters.setParameter(KEY, newValue);
        final Optional<Parameter<Integer>> parameter = parameters.getParameter(KEY, Integer.class);
        assertTrue(parameter.isPresent());
        assertTrue(parameter.get().isSetted());
        assertEquals(newValue, parameter.get().getValue());
    }

    /**
     * Test getting all the parameters.
     */
    @Test
    void testGetParameters() {
        final String key1 = "key1";
        final String key2 = "key2";
        // CHECKSTYLE: MagicNumber OFF just two casual value to test
        final Integer value1 = 10;
        final Integer value2 = 20;
        // CHECKSTYLE: MagicNumber ON

        parameters.addParameter(key1, value1, true);
        parameters.addParameter(key2, value2, true);

        final Set<Parameter<?>> parameterSet = parameters.getParameters();
        assertEquals(2, parameterSet.size());

        for (final Parameter<?> parameter : parameterSet) {
            assertTrue(parameter.isSetted());
        }
    }

    /**
     * Test getting all the parameters that are not setted.
     */
    @Test
    void testGetParametersToset() {
        final String key1 = "key1";
        final String key2 = "key2";
        final Integer value1 = 10;

        parameters.addParameter(key1, value1, true);
        parameters.addParameter(key2, Integer.class, true);

        final Set<Parameter<?>> parameterSet = parameters.getParametersToset();
        assertEquals(1, parameterSet.size());

        for (final Parameter<?> parameter : parameterSet) {
            assertFalse(parameter.isSetted());
        }
    }

    /**
     * Test adding an existing parameter.
     */
    @Test
    void testAddParameterWithParameter() {
        final Integer value = 10;
        final Parameter<Integer> parameter = new ParameterImpl<>(KEY, value, true);

        parameters.addParameter(parameter);

        final Optional<Parameter<Integer>> retrievedParameter = parameters.getParameter(KEY, Integer.class);
        assertTrue(retrievedParameter.isPresent());
        assertTrue(retrievedParameter.get().isSetted());
        assertEquals(value, retrievedParameter.get().getValue());
    }

    /**
     * Test cloning the parameters.
     */
    @Test
    void testClone() {
        final Integer value = 10;

        parameters.addParameter(KEY, value, true);

        final Parameters clone = parameters.copy();

        final Optional<Parameter<Integer>> parameter = clone.getParameter(KEY, Integer.class);
        assertTrue(parameter.isPresent());
        assertTrue(parameter.get().isSetted());
        assertEquals(value, parameter.get().getValue());
        assertNotSame(parameters, clone);
    }

    /**
     * Test adding a parameter with a null key and verify that a
     * NullPointerException is thrown.
     */
    @Test
    void testAddParameterWithNullKey() {
        assertThrows(NullPointerException.class, () -> {
            parameters.addParameter(null, Integer.class, true);
        });
    }

    /**
     * Test adding a parameter with a duplicate key and verify that a
     * IllegalArgumentException is thrown.
     */
    @Test
    void testAddParameterWithDuplicateKey() {
        final Integer value1 = 10;
        parameters.addParameter(KEY, value1, true);

        assertThrows(IllegalArgumentException.class, () -> {
            // CHECKSTYLE: MagicNumber OFF just a casual different value to test
            parameters.addParameter(KEY, 20, true);
            // CHECKSTYLE: MagicNumber ON
        });
    }

    /**
     * Test adding a parameter with a duplicate key of a different type and verify
     * that a IllegalArgumentException is thrown.
     */
    @Test
    void testAddParameterWithDuplicateKeyOfDifferentType() {
        final String key = "testKey";
        final Integer value1 = 10;
        final String value2 = "prova";

        parameters.addParameter(key, value1, true);

        assertThrows(IllegalArgumentException.class, () -> {
            parameters.addParameter(key, value2, true);
        });
    }

    /**
     * Test setting a non existing parameter and verify that a NullPointerException
     * is thrown.
     */
    @Test
    void testSetMissingParametr() {
        final Integer value = 10;

        assertThrows(IllegalArgumentException.class, () -> {
            parameters.setParameter(KEY, value);
        });
    }

    /**
     * Test setting a parameter originally created from an existing value with a
     * type different that
     * the right one and verify that a IllegalArgumentException is thrown.
     */
    @Test
    void testSetParametrOfWrongTypeWithValue() {
        final Integer value = 10;

        parameters.addParameter(KEY, value, true);

        assertThrows(IllegalArgumentException.class, () -> {
            parameters.setParameter(KEY, "test");
        });
    }

    /**
     * Test setting a parameter originally created declaring the expected type with
     * a type different
     * that the right one and verify that a IllegalArgumentException is thrown.
     */
    @Test
    void testSetParametrOfWrongTypeWithType() {
        final Class<Integer> type = Integer.class;

        parameters.addParameter(KEY, type, true);

        assertThrows(IllegalArgumentException.class, () -> {
            parameters.setParameter(KEY, "test");
        });
    }
}
