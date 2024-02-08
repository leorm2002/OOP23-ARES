package it.unibo.ares.core.controller;

import java.util.concurrent.SubmissionPublisher;
import java.util.concurrent.Flow.Processor;
import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;

public class SimulationDataProvider<T> extends SubmissionPublisher<Identifier<T>> implements Processor<Identifier<T>, Identifier<T>>{
    private Subscription subscription;

    @Override
    public void onSubscribe(Subscription subscription) {
        this.subscription = subscription;
        subscription.request(1); // Request the first item

    }

    @Override
    public void onNext(Identifier<T> item) {
        submit(item);
        subscription.request(1); // Request the next item
    }

    @Override
    public void onError(Throwable throwable) {
        throwable.printStackTrace();
    }

    @Override
    public void onComplete() {
        close(); // Close the publisher when the processor completes
    }

    /**
     * Method used from a simulation user to subscribe in order to receive the simulation data.
     * The subscriber must be a subscriber of the java.util.concurrent.Flow library. thus it must implement the methods of the Subscriber interface.
     * The data will be sent to the subscriber through the onNext method of the subscriber.
     * @param subscriber The subscriber that wants to receive the simulation data.
     * @see java.util.concurrent.Flow.Publisher#subscribe(java.util.concurrent.Flow.Subscriber)
     */
    @Override
    public void subscribe(Subscriber<? super Identifier<T>> subscriber) {
        super.subscribe(subscriber);
    }
    
}
