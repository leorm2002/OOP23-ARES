package it.unibo.ares.core.utils.configservice;

/**
 * The ConfigService interface provides methods to read configuration values.
 */
public interface ConfigService {

    /**
     * Reads a configuration value of the specified type from the given section and
     * key.
     *
     * @param section the section of the configuration
     * @param key     the key of the configuration
     * @param type    the type of the configuration value
     * @param <T>     the type of the configuration value
     * @return the configuration value of the specified type
     */
    <T> T read(String section, String key, Class<T> type);
}
