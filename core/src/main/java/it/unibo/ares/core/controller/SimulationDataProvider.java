package it.unibo.ares.core.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Flow.Publisher;
import java.util.concurrent.Flow.Subscriber;

final class SimulationDataProvider<T> implements Publisher<Identifier<T>> {
    private Map<String, Subscriber<T>> subscribers = new HashMap<>();

    /**
     * Subscribes the subscriber to the data provider with the given ID.
     * 
     * @param id
     * @param subscriber
     */
    public void subscribe(final String id, final Subscriber<T> subscriber) {
        subscribers.put(id, subscriber);
    }

    /**
     * Submits the data inside the identifier to the subscriber with the same ID.
     * 
     * @param identifier
     */
    public void submit(final Identifier<T> identifier) {
        Subscriber<T> subscriber = subscribers.get(identifier.getId());
        if (subscriber != null) {
            subscriber.onNext(identifier.getData());
        } else {
            System.out.println("No subscriber found with ID: " + identifier.getId());
        }
    }

    /**
     * Not to use.
     * 
     * @param subscriber
     */
    @Deprecated
    @Override
    public void subscribe(final Subscriber<? super Identifier<T>> subscriber) {
        // Not to use
    }
}
