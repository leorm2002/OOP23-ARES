package it.unibo.ares.core.api;

import java.util.concurrent.Flow.Subscriber;
import java.util.concurrent.Flow.Subscription;

import it.unibo.ares.core.controller.models.SimulationOutputData;

/**
 * This class is a simple implementation of the Subscriber interface, it is used to receive data from a Publisher.
 * It must be extended to be used, and the method onNext must be implemented, this methods is called every time some new data is available.
 * 
 */
public abstract class DataReciever implements Subscriber<SimulationOutputData> {
    @Override
    public void onError(Throwable throwable) {
        throwable.printStackTrace();
    }

    @Override
    public void onComplete() {
    }

    @Override
    public void onSubscribe(Subscription subscription) {
    }
}
