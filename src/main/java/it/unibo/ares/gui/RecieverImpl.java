package it.unibo.ares.gui;

import it.unibo.ares.core.api.DataReciever;
import it.unibo.ares.core.controller.models.SimulationOutputData;

/**
 * RecieverImpl is a class that extends the DataReciever class.
 * It is used to handle the receipt of simulation output data.
 */
public class RecieverImpl extends DataReciever {

    @Override
    /**
     * The onNext method is called when new SimulationOutputData is received.
     * This method is meant to be overridden with implementation specific to the
     * received data.
     *
     * @param item the SimulationOutputData instance representing the received data
     */
    public void onNext(final SimulationOutputData item) {
    }
}
