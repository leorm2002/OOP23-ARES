package it.unibo.ares.utils.parameters;

public interface Parameter<T>{
    T getValue();
    Class<T> getType();
    ParameterImpl<T> setValue(T value);
    boolean isSetted();
    String getKey();
}
