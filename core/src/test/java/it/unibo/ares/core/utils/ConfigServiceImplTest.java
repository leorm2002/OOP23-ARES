package it.unibo.ares.core.utils;

import it.unibo.ares.core.utils.configservice.ConfigServiceImpl;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ConfigServiceImplTest {

    private static final String CONFIG_SERVICE = "it.unibo.ares.core.utils.configservice.ConfigServiceImpl";
    private static final String METHOD_NAME = "read";

    @Test
    void testFlagFalse() throws NoSuchMethodException, SecurityException, ClassNotFoundException,
            IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        // Get the method
        final Method method = Class.forName(CONFIG_SERVICE)
                .getDeclaredMethod(
                        METHOD_NAME, String.class, String.class, Class.class);

        method.setAccessible(true);

        // Create an instance of ConfigServiceImpl
        ConfigServiceImpl instance = ConfigServiceImpl.getInstance();

        // Invoke the method
        final Object obj = method.invoke(instance, "Test", "flag_false",
                Boolean.class);
        assertNotNull(obj);
        final Optional<Boolean> flagValue = (Optional<Boolean>) obj;

        // Check the result
        assertEquals(false, flagValue.orElse(true));
    }

    @Test
    void testFlagTrue() throws NoSuchMethodException, SecurityException, ClassNotFoundException,
            IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        // Get the method
        final Method method = Class.forName(CONFIG_SERVICE)
                .getDeclaredMethod(
                        METHOD_NAME, String.class, String.class, Class.class);

        method.setAccessible(true);

        // Create an instance of ConfigServiceImpl
        ConfigServiceImpl instance = ConfigServiceImpl.getInstance();

        // Invoke the method
        final Object obj = method.invoke(instance, "Test", "flag_true",
                Boolean.class);
        assertNotNull(obj);
        final Optional<Boolean> flagValue = (Optional<Boolean>) obj;
        // Check the result
        assertEquals(true, flagValue.orElse(false));
    }
}