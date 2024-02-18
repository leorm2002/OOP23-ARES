package it.unibo.ares.cli.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * A class that casts a string to a given type.
 */
public class StringCaster {
    private static final Map<Class<?>, Function<String, ?>> castMap = new HashMap<>();

    static {
        castMap.put(Integer.class, Integer::parseInt);
        castMap.put(Double.class, Double::parseDouble);
        castMap.put(Float.class, Float::parseFloat);
        castMap.put(Boolean.class, Boolean::parseBoolean);
        castMap.put(Long.class, Long::parseLong);
        castMap.put(Short.class, Short::parseShort);
        castMap.put(Byte.class, Byte::parseByte);
        castMap.put(boolean.class, Boolean::parseBoolean);
        castMap.put(String.class, s -> s);
    }

    /**
     * Casts a string to a given type.
     * 
     * @param value The string to cast.
     * @param type  The type to cast the string to.
     * @return The casted value.
     */
    public <T> T cast(String value, Class<T> type) {
        if (!castMap.containsKey(type)) {
            throw new IllegalArgumentException("Il tipo di dato non è supportato");
        }
        try {
            return type.cast(castMap.get(type).apply(value));
        } catch (Exception e) {
            throw new IllegalArgumentException("Il valore non può essere convertito al tipo richiesto");
        }
    }
}