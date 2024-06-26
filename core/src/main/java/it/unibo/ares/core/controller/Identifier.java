package it.unibo.ares.core.controller;

/**
 * A simple class used to identify a data with a string.
 * 
 * @param <T> The type of the data to identify.
 */
public final class Identifier<T> {
    private final String id;
    private final T data;

    /**
     * Creates a new identifier with the given id and data.
     * 
     * @param id   The id of the identifier.
     * @param data The data of the identifier.
     */
    public Identifier(final String id, final T data) {
        this.id = id;
        this.data = data;
    }

    /**
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * @return the data
     */
    public T getData() {
        return data;
    }

}
