package it.unibo.ares.core.utils.parameters;

import java.io.Serializable;

/**
 * A domain for a parameter.
 * 
 * @param <T> the type of the domain.
 */
public interface ParameterDomain<T extends Serializable> extends Serializable {
    /**
     * Get the domain description.
     * 
     * @return a description in natural language of the domain.
     */
    String getDescription();

    /**
     * Check if a value is valid for the domain.
     * 
     * @param value the value to check.
     * @return true if the value is valid, false otherwise.
     */
    boolean isValueValid(T value);
}
