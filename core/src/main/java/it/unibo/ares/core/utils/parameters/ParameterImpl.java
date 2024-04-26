package it.unibo.ares.core.utils.parameters;

import java.util.Optional;

/**
 * Represents an implementation of the Parameter interface.
 * This class provides methods to get and set the value of a parameter,
 * as well as retrieve information about the parameter's type and key.
 *
 * @param <T> the type of the parameter value
 */
public final class ParameterImpl<T> implements Parameter<T> {

    private final Optional<ParameterDomain<T>> domain;
    private final Optional<T> value;
    private final Class<T> type;
    private final String key;
    private final Boolean userSettable;

    @SuppressWarnings("unchecked")
    private ParameterImpl(final String key, final T value,
            final Optional<ParameterDomain<T>> domain, final Boolean userSettable) {
        this.value = Optional.ofNullable(value);
        this.key = key;
        this.type = (Class<T>) value.getClass();
        this.domain = domain;
        this.userSettable = userSettable;
    }

    private ParameterImpl(final String key, final Class<T> type,
            final Optional<ParameterDomain<T>> domain, final Boolean userSettable) {
        this.key = key;
        this.type = type;
        this.domain = domain;
        this.value = Optional.empty();
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
        this(key, value, Optional.empty(), userSettable);
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
        this(key, type, Optional.empty(), userSettable);
    }

    /**
     * Creates a new parameter with the specified key and value.
     * 
     * @param key          the key of the parameter
     * @param value        the value of the parameter
     * @param domain       the domain of the parameter
     * @param userSettable if the parameter is user settable
     */
    public ParameterImpl(final String key, final T value, final ParameterDomain<T> domain, final Boolean userSettable) {
        this(key, value, Optional.ofNullable(domain), userSettable);
    }

    /**
     * Creates a new parameter with the specified key and type.
     * 
     * @param key          the key of the parameter
     * @param type         the type of the parameter
     * @param domain       the domain of the parameter
     * @param userSettable if the parameter is user settable
     */
    public ParameterImpl(final String key, final Class<T> type, final ParameterDomain<T> domain,
            final Boolean userSettable) {
        this(key, type, Optional.ofNullable(domain), userSettable);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public T getValue() {
        return this.value.orElseThrow(() -> new IllegalStateException("Value not set for parameter: " + this.key));
    }

    @Override
    public Optional<T> getOptionalValue() {
        return this.value;
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
        if (this.domain.isPresent() && !this.domain.get().isValueValid(value)) {
            throw new IllegalArgumentException("Value is not inside the domain: " + this.key);
        }
        return new ParameterImpl<>(key, value, domain, userSettable);
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

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<ParameterDomain<T>> getDomain() {
        return this.domain;
    }

    @Override
    public Boolean userSettable() {
        return this.userSettable;
    }
}
