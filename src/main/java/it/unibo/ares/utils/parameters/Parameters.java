package it.unibo.ares.utils.parameters;

import java.util.Optional;
import java.util.Set;

public interface Parameters {

    void addParameter(String key, Class<?> type);

    <T> void addParameter(String key, T value);

    <T> void addParameter(Parameter<T> parameter);

    <T> Optional<Parameter<T>> getParameter(String key, Class<T> type);

    <T> void setParameter(String key, T value);

    Set<Parameter<?>> getParameters();

    Set<Parameter<?>> getParametersToset();

    Parameters clone();

}