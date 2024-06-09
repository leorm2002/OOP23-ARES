package it.unibo.ares.core.utils.parameters;

import java.io.Serializable;
import java.util.Optional;

/**
 * Represents an implementation of the Parameter interface.
 * This class provides methods to get and set the value of a parameter,
 * as well as retrieve information about the parameter's type and key.
 *
 * @param <T> the type of the parameter value
 */
public final class ParameterImpl<T extends Serializable> implements Parameter<T>, Serializable {

    private static final long serialVersionUID = 1L;
    private final ParameterDomain<T> domain;
    private final T value;
    private final Class<T> type;
    private final String key;
    private final Boolean userSettable;

    @SuppressWarnings("unchecked")
    public ParameterImpl(final String key, final T value,
            final ParameterDomain<T> domain, final Boolean userSettable) {
        this.value = value;
        this.key = key;
        this.type = (Class<T>) value.getClass();
        this.domain = domain;
        this.userSettable = userSettable;
    }

    public ParameterImpl(final String key, final Class<T> type,
            final ParameterDomain<T> domain, final Boolean userSettable) {
        this.key = key;
        this.type = type;
        this.domain = domain;
        this.value = null;
        this.userSettable = userSettable;

    }

    /**
     * Constructs a new ParameterImpl object with the specified key, value, and
     * userSettable flag.
     *
     * @param key          the key associated with the parameter
     * @param value        the value of the parameter
     * @param userSettable a flag indicating whether the parameter can be set by the
     *                     user
     */
    public ParameterImpl(final String key, final T value, final Boolean userSettable) {
        this(key, value, null, userSettable);
    }

    /**
     * Creates a new parameter with the specified key and type.
     * 
     * @param key  the key of the parameter
     * @param type the type of the parameter
     */
    /**
     * Constructs a new instance of the ParameterImpl class with the specified key,
     * type, and userSettable flag.
     *
     * @param key          The key of the parameter.
     * @param type         The type of the parameter.
     * @param userSettable A boolean flag indicating whether the parameter is user
     *                     settable or not.
     */
    public ParameterImpl(final String key, final Class<T> type, final Boolean userSettable) {
        this(key, type, null, userSettable);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T getValue() {
        return Optional.ofNullable(this.value)
                .orElseThrow(() -> new IllegalStateException("Value not set for parameter: " + this.key));
    }

    @Override
    public Optional<T> getOptionalValue() {
        return Optional.ofNullable(this.value);
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
    public ParameterImpl<T> updateValue(final T value) {
        if (!this.type.isInstance(value)) {
            throw new IllegalArgumentException("Value is not of type " + this.type.getName());
        }
        if (domain != null && !domain.isValueValid(value)) {
            throw new IllegalArgumentException("Value is not inside the domain: " + this.key);
        }
        // if (this.domain.isPresent() && !this.domain.get().isValueValid(value)) {
        // throw new IllegalArgumentException("Value is not inside the domain: " +
        // this.key);
        // }
        return new ParameterImpl<>(key, value, domain, userSettable);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isSetted() {
        return this.value != null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getKey() {
        return this.key;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<ParameterDomain<T>> getDomain() {
        return Optional.ofNullable(this.domain);
    }

    @Override
    public Boolean userSettable() {
        return this.userSettable;
    }
}
