package it.unibo.ares.core.utils.parameters;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Implementation of the Parameters interface.
 * This class represents a collection of parameters and provides methods to add,
 * get, and set parameters.
 */
public final class ParametersImpl implements Parameters {
    private final Map<Class<?>, Map<String, Parameter<?>>> typeMap;

    /**
     * It provides a default constructor that initializes a HashMap to store
     * parameter types.
     */
    public ParametersImpl() {
        typeMap = new HashMap<>();
    }

    /**
     * Initialize a Parameters collection from a type map.
     * 
     * @param typeMap the type map to initialize the collection, it's a map of maps
     *                which contains the parameters
     */
    public ParametersImpl(final Map<Class<?>, Map<String, Parameter<?>>> typeMap) {
        this.typeMap = new HashMap<>(typeMap);
    }

    /*
     * {@inheritDoc}
     */
    private <T> void addParameter(final String key, final Parameter<T> parameter) {
        typeMap.entrySet().stream().filter(e -> e.getValue().containsKey(key)).findAny().ifPresent(e -> {
            throw new IllegalArgumentException("Parameter " + key + " already exists");
        });

        if (key == null || parameter.getType() == null) {
            throw new IllegalArgumentException("Parameter key or type is null");
        }
        typeMap.computeIfAbsent(parameter.getType(), k -> new HashMap<>()).put(key, parameter);
    }

    /*
     * {@inheritDoc}
     */
    @Override
    public void addParameter(final String key, final Class<?> type) {
        if (this.getParameter(key, type).isPresent()) {
            throw new IllegalArgumentException("Parameter " + key + " already exists");
        }
        if (key == null || type == null) {
            throw new NullPointerException("Parameter key or type is null");
        }
        addParameter(key, new ParameterImpl<>(key, type));
    }

    /*
     * {@inheritDoc}
     */
    @Override
    public <T> void addParameter(final String key, final T value) {
        addParameter(key, new ParameterImpl<>(key, value));
    }

    /*
     * {@inheritDoc}
     */
    @Override
    public <T> void addParameter(final Parameter<T> parameter) {
        addParameter(parameter.getKey(), parameter);
    }

    /*
     * {@inheritDoc}
     */
    @Override
    public <T> Optional<Parameter<T>> getParameter(final String key, final Class<T> type) {
        Optional<Map<String, Parameter<?>>> parameterMap = Optional.ofNullable(typeMap.get(type));
        if (parameterMap.isPresent()) {
            @SuppressWarnings("unchecked")
            Parameter<T> parameter = (Parameter<T>) parameterMap.get().get(key);
            return Optional.ofNullable(parameter);
        }
        return Optional.empty();
    }

    /*
     * {@inheritDoc}
     */
    @Override
    @SuppressWarnings("unchecked")
    public <T> void setParameter(final String key, final T value) {
        Optional<Parameter<T>> parameter = getParameter(key, (Class<T>) value.getClass());
        if (parameter.isPresent()) {
            typeMap.get(value.getClass()).replace(key, parameter.get().setValue(value));
        }
        parameter.orElseThrow(() -> new IllegalArgumentException(
                "Parameter " + key + " does not exist or not of type " + value.getClass().getName()));
    }

    private Stream<Parameter<?>> getParametersStream() {
        return typeMap.values().stream().flatMap(m -> m.values().stream());
    }

    /*
     * {@inheritDoc}
     */
    @Override
    public Set<Parameter<?>> getParameters() {
        return getParametersStream().collect(Collectors.toSet());
    }

    /*
     * {@inheritDoc}
     */
    @Override
    public Set<Parameter<?>> getParametersToset() {
        return getParametersStream().filter(p -> !p.isSetted()).collect(Collectors.toSet());
    }

    /*
     * {@inheritDoc}
     */
    @Override
    public Parameters copy() {
        Map<Class<?>, Map<String, Parameter<?>>> cloneMap = new HashMap<>();
        typeMap.entrySet().forEach(m -> cloneMap.put(m.getKey(), new HashMap<>(m.getValue())));
        return new ParametersImpl(cloneMap);
    }

    @Override
    public boolean areAllParametersSetted() {
        return getParametersStream().allMatch(Parameter::isSetted);
    }

    @Override
    public <T> Optional<Parameter<T>> getParameter(String key) {
        return getParametersStream().filter(p -> p.getKey().equals(key)).map(p -> (Parameter<T>) p).findAny();
    }
}
