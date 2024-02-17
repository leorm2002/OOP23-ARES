package it.unibo.ares.core.utils.parameters;

import java.util.Optional;
import java.util.Set;

/**
 * The Parameters interface represents a collection of parameters that can be
 * added, retrieved, and modified.
 * The collection can manage parameters of different types.
 */
public interface Parameters {

    /**
     * Adds a parameter with the specified key and type.
     * 
     * @param key  the key of the parameter
     * @param type the type of the parameter
     * @throws IllegalArgumentException if a parameter with the specified key
     *                                  already exists
     * @throws NullPointerException     if the key or the type is null
     */
    void addParameter(String key, Class<?> type);

    /**
     * Adds a parameter with the specified key and value.
     * 
     * @param key   the key of the parameter
     * @param value the value of the parameter
     * @param <T>   the type of the value
     * @throws IllegalArgumentException if a parameter with the specified key
     *                                  already exists
     * @throws NullPointerException     if the key is null
     */
    <T> void addParameter(String key, T value);

    /**
     * Adds a parameter.
     * 
     * @param parameter the parameter to add
     * @param <T>       the type of the parameter
     * @throws IllegalArgumentException if a parameter with the specified key
     *                                  already exists
     */
    <T> void addParameter(Parameter<T> parameter);

    /**
     * Retrieves a parameter with the specified key and type.
     * 
     * @param key  the key of the parameter
     * @param type the type of the parameter
     * @param <T>  the type of the parameter
     * @return an Optional containing the parameter, or an empty Optional if not
     *         found
     */
    <T> Optional<Parameter<T>> getParameter(String key, Class<T> type);

    /**
     * Sets the value of a parameter with the specified key.
     * 
     * @param key   the key of the parameter
     * @param value the new value of the parameter
     * @param <T>   the type of the value
     * @throws IllegalArgumentException if the parameter does not exist
     */
    <T> void setParameter(String key, T value);

    /**
     * Retrieves all the parameters.
     * 
     * @return a set of all the parameters
     */
    Set<Parameter<?>> getParameters();

    /**
     * Retrieves all the parameters that can be set.
     * 
     * @return a set of all the parameters that can must be set
     */
    Set<Parameter<?>> getParametersToset();

    boolean areAllParametersSetted();

    Parameters clone();

}
