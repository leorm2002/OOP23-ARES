package it.unibo.ares.core.controller;

import it.unibo.ares.core.api.SimulationOutputData;

import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Flow.Subscriber;
import java.util.stream.Collectors;

final class SimulationsControllerImpl extends SimulationsController {
    private final ConcurrentMap<String, Simulation> simulations;
    private final SimulationDataProvider<SimulationOutputData> processor;

    SimulationsControllerImpl() {
        this.simulations = new ConcurrentHashMap<>();
        this.processor = new SimulationDataProvider<>();
    }

    @Override
    void addSimulation(final String id, final Simulation simulation) {
        simulations.put(id, simulation);
    }

    @Override
    public void removeSimulation(final String id) {
        simulations.remove(id);
    }

    @Override
    public void startSimulation(final String id) {
        if (!simulations.get(id).isRunning()) {
            simulations.get(id).start();
            return;
        }
        throw new IllegalStateException("The simulation is already running");
    }

    @Override
    void makeModelsTick() {
        boolean async = false;
        if (!async) {
            simulations.entrySet().stream()
                    .filter(e -> e.getValue().isRunning())
                    .map(e -> e.getValue().tickSync(e.getKey())) // Starting the calculation and mapping the future to
                                                                 // the
                    // id of
                    // the simulation
                    .forEach(simData -> processor.submit(
                            new Identifier<>(simData.getSimulationId(), simData))); // Processing
        } else {

            simulations.entrySet().stream()
                    .filter(e -> e.getValue().isRunning())
                    .map(e -> e.getValue().tick(e.getKey())) // Starting the calculation and mapping the future to the
                                                             // id of
                    // the simulation
                    .forEach(f -> f
                            .thenAccept(simData -> processor.submit(
                                    new Identifier<>(simData.getSimulationId(), simData)))); // Processing
        }
    }

    @Override
    void subscribe(final String id, final Subscriber<SimulationOutputData> subscriber) {
        processor.subscribe(id, subscriber);
    }

    @Override
    public void pauseSimulation(String id) {
        if (simulations.get(id).isRunning()) {
            simulations.get(id).pause();
            return;
        }
        throw new IllegalStateException("The simulation is not running");
    }

    @Override
    public List<String> getRunningSimulations() {
        return simulations.entrySet().stream().filter(e -> e.getValue().isRunning())
                .map(e -> e.getKey()).collect(Collectors.toList());
    }

}
