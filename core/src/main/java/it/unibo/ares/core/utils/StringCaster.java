package it.unibo.ares.core.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * A class that casts a string to a given type.
 */
public final class StringCaster {
    private static final Map<Class<?>, Function<String, ?>> CAST_MAP = new HashMap<>();

    static {
        CAST_MAP.put(Integer.class, Integer::parseInt);
        CAST_MAP.put(Double.class, Double::parseDouble);
        CAST_MAP.put(Float.class, Float::parseFloat);
        CAST_MAP.put(Boolean.class, Boolean::parseBoolean);
        CAST_MAP.put(Long.class, Long::parseLong);
        CAST_MAP.put(Short.class, Short::parseShort);
        CAST_MAP.put(Byte.class, Byte::parseByte);
        CAST_MAP.put(boolean.class, Boolean::parseBoolean);
        CAST_MAP.put(String.class, Function.identity());
    }

    /**
     * Casts a string to a given type.
     *
     * @param value The string to cast.
     * @param type  The type to cast the string to.
     * @param <T>   The type to cast the string to.
     * @return The casted value.
     */
    @SuppressWarnings({ "PMD.AvoidCatchingGenericException", "PMD.PreserveStackTrace" }) // non si è sicuri di quale
                                                                                         // eccezione
    public static <T> T cast(final String value, final Class<T> type) {
        if (!CAST_MAP.containsKey(type)) {
            throw new IllegalArgumentException("Il tipo di dato non è supportato");
        }
        try {
            return type.cast(CAST_MAP.get(type).apply(value));
        } catch (Exception e) {
            throw new IllegalArgumentException("Il valore non può essere convertito al tipo richiesto");
        }
    }

    private StringCaster() {
        throw new IllegalStateException("Utility class");
    }
}
