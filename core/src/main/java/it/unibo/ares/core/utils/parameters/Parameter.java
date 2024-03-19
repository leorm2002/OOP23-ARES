package it.unibo.ares.core.utils.parameters;

import java.util.Optional;

/**
 * Represents a parameter with a generic type.
 *
 * @param <T> the type of the parameter value
 */
public interface Parameter<T> {
    /**
     * Gets the value of the parameter.
     *
     * @return the value of the parameter
     * @throws IllegalStateException if not setted
     */
    T getValue();

    /**
     * Gets the value of the parameter.
     *
     * @return optional of the value of the parameter
     */
    Optional<T> getOptionalValue();

    /**
     * Gets the type of the parameter.
     *
     * @return the type of the parameter
     */
    Class<T> getType();

    /**
     * Sets the value of the parameter.
     *
     * @param value the value to set
     * @return the updated parameter object
     */
    ParameterImpl<T> setValue(T value);

    /**
     * Checks if the parameter has been set.
     *
     * @return true if the parameter has been set, false otherwise
     */
    boolean isSetted();

    /**
     * Gets the key of the parameter.
     *
     * @return the key of the parameter
     */
    String getKey();

    /**
     * Gets the domain of the parameter.
     *
     * @return the domain of the parameter
     */
    Optional<ParameterDomain<T>> getDomain();

    /**
     * If the user can view thus set the parameter.
     * 
     * @return true if the parameter is user settable, false otherwise
     */
    Boolean userSettable();
}
