package it.unibo.ares.core.utils.configservice;

/**
 * The ConfigService interface provides methods for accessing configuration
 * settings.
 */
public interface ConfigService {
    /**
     * Checks if the configuration service is asynchronous.
     *
     * @return true if the service is asynchronous, false otherwise.
     */
    Boolean isAsync();
}
