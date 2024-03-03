package it.unibo.ares.core.utils.parameters;

public interface ParameterDomain<T> {
    /**
     * Get the domain description.
     * 
     * @return a description in natural language of the domain.
     */
    String getDescription();

    boolean isValueValid(T value);
}
