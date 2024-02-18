package it.unibo.ares.gui;

import java.util.Map;

import it.unibo.ares.core.api.DataReciever;
import it.unibo.ares.core.controller.models.SimulationOutputData;
import it.unibo.ares.core.utils.pos.Pos;

/**
 * RecieverImpl is a class that extends the DataReciever class.
 * It is used to handle the receipt of simulation output data.
 */
public class RecieverImpl extends DataReciever {

    /**
     * The onNext method is called when new SimulationOutputData is received.
     * This method is meant to be overridden with implementation specific to the
     * received data.
     *
     * @param item the SimulationOutputData instance representing the received data
     */
    @Override
    public void onNext(final SimulationOutputData item) {
        Map<Pos, String> pos = item.getData();
    }
}
