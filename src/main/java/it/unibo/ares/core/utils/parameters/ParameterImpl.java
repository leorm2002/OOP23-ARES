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

    private final Optional<T> value;
    private final Class<T> type;
    private final String key;

    /**
     * Creates a new parameter with the specified key and value.
     * @param key    the key of the parameter
     * @param value  the value of the parameter
     */
    @SuppressWarnings("unchecked")
    public ParameterImpl(final String key, final T value) {
        this.value = Optional.of(value);
        this.key = key;
        this.type = (Class<T>) value.getClass();
    }

    /**
     * Creates a new parameter with the specified key and type.
     * @param key   the key of the parameter
     * @param type  the type of the parameter
     */
    public ParameterImpl(final String key, final Class<T> type) {
        this.type = type;
        this.key = key;
        this.value = Optional.empty();
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
        if (this.type.isInstance(value)) {
            return new ParameterImpl<>(key, value);
        } else {
            throw new IllegalArgumentException("Value is not of type " + this.type.getName());
        }
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
}
