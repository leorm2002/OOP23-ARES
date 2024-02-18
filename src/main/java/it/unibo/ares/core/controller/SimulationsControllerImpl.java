package it.unibo.ares.core.controller;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Flow.Subscriber;

import it.unibo.ares.core.controller.models.Identifier;
import it.unibo.ares.core.controller.models.SimulationOutputData;

final class SimulationsControllerImpl extends SimulationsController {
    private final ConcurrentMap<String, Simulation> simulations;
    private final SimulationDataProvider<SimulationOutputData> processor;

    SimulationsControllerImpl() {
        this.simulations = new ConcurrentHashMap<>();
        this.processor = new SimulationDataProvider<>();
    }

    @Override
    protected void addSimulation(final String id, final Simulation simulation) {
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
    protected void makeModelsTick() {
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
    public void pauseSimulation(String id) {
        if (simulations.get(id).isRunning()) {
            simulations.get(id).pause();
        }
        throw new IllegalStateException("The simulation is not running");
    }

}
