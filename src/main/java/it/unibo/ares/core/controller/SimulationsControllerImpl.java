package it.unibo.ares.core.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Flow.Subscriber;

import it.unibo.ares.core.controller.models.Identifier;
import it.unibo.ares.core.controller.models.SimulationOutputData;

final class SimulationsControllerImpl extends SimulationsController {
    private final Map<String, Simulation> simulations;
    private final SimulationDataProvider<SimulationOutputData> processor;

    SimulationsControllerImpl() {
        this.simulations = new HashMap<>();
        this.processor = new SimulationDataProvider<>();
    }

    @Override
    public void addSimulation(final String id, final Simulation simulation) {
        simulations.put(id, simulation);
    }

    @Override
    public void removeSimulation(final String id) {
        simulations.remove(id);
    }

    @Override
    public void startSimulation(final String id) {
        simulations.get(id).start();
    }

    @Override
    public void makeModelsTick() {
        simulations.entrySet().stream()
                .filter(e -> e.getValue().isRunning())
                .map(e -> e.getValue().tick(e.getKey())) // Starting the calculation and mapping the future to the id of
                                                         // the simulation
                .forEach(f -> f
                        .thenAccept(simData -> processor.submit(
                                new Identifier<>(simData.getSimulationId(), simData)))); // Processing
    }

    @Override
    void subscribe(final String id, final Subscriber<SimulationOutputData> subscriber) {
        processor.subscribe(id, subscriber);
    }

    @Override
    public void pauseSimulation(final String id) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'pauseSimulation'");
    }

}
