package it.unibo.ares.core.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Flow.Publisher;
import java.util.concurrent.Flow.Subscriber;

import it.unibo.ares.core.controller.models.Identifier;

public class SimulationDataProvider<T> implements Publisher<Identifier<T>> {
    private Map<String, Subscriber<T>> subscribers = new HashMap<>();

    // Method to subscribe a subscriber with an ID
    public void subscribe(String id, Subscriber<T> subscriber) {
        subscribers.put(id, subscriber);
    }

    // Method to publish data to a specific subscriber by ID
    public void submit(Identifier<T> identifier) {
        Subscriber<T> subscriber = subscribers.get(identifier.getId());
        if (subscriber != null) {
            subscriber.onNext(identifier.getData());
        } else {
            System.out.println("No subscriber found with ID: " + identifier.getId());
        }
    }

    @Override
    public void subscribe(Subscriber<? super Identifier<T>> subscriber) {
        // Not to use
    }
    
}
