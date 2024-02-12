package it.unibo.ares.core.controller.models;

public class Identifier<T> {
    private final String id;
    private final T data;

    public Identifier(String id, T data) {
        this.id = id;
        this.data = data;
    }

    public String getId() {
        return id;
    }

    public T getData() {
        return data;
    }

}
