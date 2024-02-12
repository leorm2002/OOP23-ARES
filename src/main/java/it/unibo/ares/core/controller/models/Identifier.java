package it.unibo.ares.core.controller.models;

/**
 * A simple class used to identify a data with a string.
 * 
 * @param <T> The type of the data to identify.
 */
public final class Identifier<T> {
    private final String id;
    private final T data;

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
