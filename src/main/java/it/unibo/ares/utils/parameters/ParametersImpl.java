package it.unibo.ares.utils.parameters;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ParametersImpl implements Parameters  {

    private final Map<Class<?>, Map<String, Parameter<?>>> typeMap; 

    
    public ParametersImpl() {
        typeMap = new HashMap<>();
    }

    public ParametersImpl(Map<Class<?>, Map<String, Parameter<?>>> typeMap) {
        this.typeMap = typeMap;
    }

    private <T> void addParameter(String key, Parameter<T> parameter) {

        typeMap.entrySet().stream().filter(e -> e.getValue().containsKey(key)).findAny().ifPresent(e -> {
            throw new IllegalArgumentException("Parameter " + key + " already exists");
        });

        if(key == null || parameter.getType() == null){
            throw new IllegalArgumentException("Parameter key or type is null");
        }
        typeMap.computeIfAbsent(parameter.getType(), k -> new HashMap<>()).put(key, parameter);
    }


    @Override
    public void addParameter(String key, Class<?> type) {
        if(this.getParameter(key, type).isPresent()) {
            throw new IllegalArgumentException("Parameter " + key + " already exists");
        }
        
        if(key == null || type == null){
            throw new IllegalArgumentException("Parameter key or type is null");
        }
        addParameter(key, new ParameterImpl<>(key, type));
    }

    @Override
    public <T> void addParameter(String key, T value) {
        addParameter(key, new ParameterImpl<>(key, value));
    }


    @Override
    public <T> void addParameter(Parameter<T> parameter) {
        addParameter(parameter.getKey(), parameter);
    }

    @Override
    public <T> Optional<Parameter<T>> getParameter(String key, Class<T> type) {
        

        Optional<Map<String, Parameter<?>>> parameterMap =  Optional.ofNullable(typeMap.get(type));
        if (parameterMap.isPresent()) {
            @SuppressWarnings("unchecked")
            Parameter<T> parameter =  (Parameter<T>) parameterMap.get().get(key);
            return Optional.ofNullable( parameter);
        }
        return Optional.empty();
    }


    @Override
    @SuppressWarnings("unchecked")
    public <T> void setParameter(String key, T value) {
        Optional<Parameter<T>> parameter = getParameter(key,  (Class<T>) value.getClass());

        if(parameter.isPresent()) {
            typeMap.get(value.getClass()).replace(key, parameter.get().setValue(value));
        }

        parameter.orElseThrow(() -> new IllegalArgumentException("Parameter " + key + " does not exist or is not of type " + value.getClass().getName()));
    }


    private Stream<Parameter<?>> getParametersStream() {
        return typeMap.values().stream().flatMap(m -> m.values().stream());
    }

    @Override
    public Set<Parameter<?>> getParameters() {
        return getParametersStream().collect(Collectors.toSet());
    }

    @Override
    public Set<Parameter<?>> getParametersToset() {
        return getParametersStream().filter(p -> !p.isSetted()).collect(Collectors.toSet());
    }

    @Override
    public Parameters clone() {
        Map<Class<?>, Map<String, Parameter<?>>> cloneMap = new HashMap<>();; 

        typeMap.entrySet().forEach(m -> cloneMap.put(m.getKey(), new HashMap<>(m.getValue()))); 
        
        return new ParametersImpl(cloneMap);
    }
    
    
}
