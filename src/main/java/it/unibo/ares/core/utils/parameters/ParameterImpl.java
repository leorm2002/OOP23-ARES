package it.unibo.ares.core.utils.parameters;

import java.util.Optional;

import javax.annotation.concurrent.Immutable;

/**
 * Represents an implementation of the Parameter interface.
 * This class provides methods to get and set the value of a parameter,
 * as well as retrieve information about the parameter's type and key.
 *
 * @param <T> the type of the parameter value
 */
@Immutable
public class ParameterImpl<T> implements Parameter<T> {

    private final Optional<ParameterDomain<T>> domain;
    private final Optional<T> value;
    private final Class<T> type;
    private final String key;

    @SuppressWarnings("unchecked")
    private ParameterImpl(final String key, final T value,
            final Optional<ParameterDomain<T>> domain) {
        this.value = Optional.of(value);
        this.key = key;
        this.type = (Class<T>) value.getClass();
        this.domain = domain;
    }

    private ParameterImpl(final String key, final Class<T> type,
            final Optional<ParameterDomain<T>> domain) {
        this.key = key;
        this.type = type;
        this.domain = domain;
        this.value = Optional.empty();
    }

    /**
     * Creates a new parameter with the specified key and value.
     * 
     * @param key   the key of the parameter
     * @param value the value of the parameter
     */
    public ParameterImpl(final String key, final T value) {
        this(key, value, Optional.empty());
    }

    /**
     * Creates a new parameter with the specified key and type.
     * 
     * @param key  the key of the parameter
     * @param type the type of the parameter
     */
    public ParameterImpl(final String key, final Class<T> type) {
        this(key, type, Optional.empty());
    }

    /**
     * Creates a new parameter with the specified key and value.
     * 
     * @param key    the key of the parameter
     * @param value  the value of the parameter
     * @param domain the domain of the parameter
     */
    public ParameterImpl(final String key, final T value, final ParameterDomain<T> domain) {
        this(key, value, Optional.of(domain));
    }

    /**
     * Creates a new parameter with the specified key and type.
     * 
     * @param key    the key of the parameter
     * @param type   the type of the parameter
     * @param domain the domain of the parameter
     */
    public ParameterImpl(final String key, final Class<T> type, final ParameterDomain<T> domain) {
        this(key, type, Optional.of(domain));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T getValue() {
        return this.value.orElseThrow(() -> new IllegalStateException("Value not set for parameter: " + this.key));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Class<T> getType() {
        return this.type;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ParameterImpl<T> setValue(final T value) {
        if (!this.type.isInstance(value)) {
            throw new IllegalArgumentException("Value is not of type " + this.type.getName());
        }
        if (this.domain.isPresent() && !this.domain.get().isValueValid(value)) {
            throw new IllegalArgumentException("Value is not inside the domain: " + this.key);
        }
        return new ParameterImpl<>(key, value, domain);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isSetted() {
        return this.value.isPresent();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getKey() {
        return this.key;
    }

    @Override
    public Optional<ParameterDomain<T>> getDomain() {
        return this.domain;
    }
}
