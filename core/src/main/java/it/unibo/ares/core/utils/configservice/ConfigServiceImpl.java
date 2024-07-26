package it.unibo.ares.core.utils.configservice;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.ini4j.Ini;

/**
 * Implementation of the ConfigService interface that reads
 * configuration values from a .ini file.
 * This class uses the Singleton pattern to ensure that only one instance of the
 * class is created.
 * The {@link #read(String, String, Class)} method uses a cache to store
 * previously read configuration values. This improves performance by avoiding
 * the need to read from the .ini file every time a configuration value is
 * requested.
 */
@SuppressWarnings({ "PMD.SystemPrintln", "PMD.LooseCoupling" }) // E UN PROGRAMMA CLI, Ini4j non ha interfacce
public final class ConfigServiceImpl implements ConfigService {

    /**
     * The singleton instance of the ConfigServiceImpl.
     */
    private static volatile ConfigServiceImpl instance;
    /**
     * The Ini object representing the .ini file.
     */
    private Ini ini;

    /**
     * The cache of configuration values.
     */
    private final Map<String, Object> cache = new HashMap<>();

    // Private constructor to prevent instantiation
    private ConfigServiceImpl() {
        try {
            loadConfig();
        } catch (IOException e) {
            System.err.println(e);
        }
    }

    /**
     * @return the singleton instance of the ConfigServiceImpl.
     */
    @SuppressWarnings("PMD.SingletonClassReturningNewInstance")
    public static ConfigServiceImpl getInstance() {
        final ConfigServiceImpl curr = instance;

        if (curr != null) {
            return curr;
        }
        synchronized (ConfigServiceImpl.class) {
            if (instance == null) {
                instance = new ConfigServiceImpl();
            }
            return instance;
        }
    }

    /**
     * Loads the configuration from the .ini file.
     * 
     * @throws IOException if the configuration file cannot be loaded.
     */
    private void loadConfig() throws IOException {
        final URL resource = getClass().getClassLoader().getResource("config.ini");
        if (resource == null) {
            throw new IOException("Configuration file not found");
        }

        ini = new Ini(resource);
    }

    /**
     * Reads a configuration value from the specified section and key.
     *
     * @param section The section containing the key.
     * @param key     The key of the configuration value.
     * @param type    The type of the configuration value.
     * @param <T>     The type of the configuration value.
     * @return The configuration value, or null if the key is not found.
     */
    private <T> Optional<T> read(final String section, final String key, final Class<T> type) {
        final String cacheKey = section + "." + key;
        if (cache.containsKey(cacheKey)) {
            return Optional.ofNullable(type.cast(cache.get(cacheKey)));
        }
        final String value = ini.get(section, key);
        if (value == null) {
            return Optional.ofNullable(null);
        }

        final Object parsedValue;
        if (type == String.class) {
            parsedValue = value;
        } else if (type == Integer.class) {
            parsedValue = Integer.parseInt(value);
        } else if (type == Boolean.class) {
            parsedValue = Boolean.parseBoolean(value);
        } else if (type == Double.class) {
            parsedValue = Double.parseDouble(value);
        } else if (type == Long.class) {
            parsedValue = Long.parseLong(value);
        } else {
            throw new IllegalArgumentException("Unsupported type: " + type.getName());
        }

        cache.put(cacheKey, parsedValue);
        return Optional.ofNullable(type.cast(parsedValue));
    }

    @Override
    public Boolean isAsync() {
        return read("simulation", "async", Boolean.class).map(Boolean::valueOf).orElse(false);
    }
}
