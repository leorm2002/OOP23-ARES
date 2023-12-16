package it.unibo.ares.utils.parameters;

import java.util.Optional;

import javax.annotation.concurrent.Immutable;

@Immutable
public class ParameterImpl<T> implements Parameter<T> {

    private final Optional<T> value;
    private final Class<T> type;
    private final String key;

    @SuppressWarnings("unchecked")
    public ParameterImpl(String key, T value) {
        this.value = Optional.of(value);
        this.key = key;
        this.type = (Class<T>) value.getClass();
    }

    public ParameterImpl(String key, Class<T> type) {
        this.type = type;
        this.key = key;
        this.value = Optional.empty();
    }

    @Override
    public T getValue() {
        return this.value.orElseThrow(() -> new IllegalStateException("Value not set for parameter: " + this.key));
    }

    @Override
    public Class<T> getType() {
        return this.type;
    }

    @Override
    public ParameterImpl<T> setValue(T value) {
        if( this.type.isInstance(value)){
            return new ParameterImpl<>(key, value);
        }else{
            throw new IllegalArgumentException("Value is not of type " + this.type.getName());
        }
    }

    @Override
    public boolean isSetted() {
        return this.value.isPresent();
    }

    @Override
    public String getKey() {
        return this.key;
    }
}
