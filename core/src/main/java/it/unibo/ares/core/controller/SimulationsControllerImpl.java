package it.unibo.ares.core.controller;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.Flow.Subscriber;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import it.unibo.ares.core.utils.configservice.ConfigServiceImpl;

final class SimulationsControllerImpl extends SimulationsController {
    private final ConcurrentMap<String, Simulation> simulations;
    private final SimulationDataProvider<SimulationOutputData> processor;
    private final SimulationManager manager;

    SimulationsControllerImpl() {
        this.simulations = new ConcurrentHashMap<>();
        this.processor = new SimulationDataProvider<>();
        this.manager = new SimulationManagerImpl();
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
        final boolean async = ConfigServiceImpl.getInstance().isAsync();

        final Predicate<Map.Entry<String, Simulation>> isRunning = e -> e.getValue().isRunning();

        if (!async) {
            simulations.entrySet().stream()
                    .filter(isRunning)
                    .map(e -> e.getValue().tickSync(e.getKey())) // Starting the calculation and mapping the future to
                                                                 // the
                    // id of
                    // the simulation
                    .filter(Optional::isPresent)
                    .map(Optional::get)
                    .forEach(simData -> processor.submit(
                            new Identifier<>(simData.getSimulationId(), simData))); // Processing
        } else {

            simulations.entrySet().stream()
                    .filter(isRunning)
                    .map(e -> e.getValue().tick(e.getKey())) // Starting the calculation and mapping the future to the
                                                             // id of
                    // the simulation
                    .forEach(f -> f
                            .thenAccept(simData -> simData.ifPresent(d -> processor.submit(
                                    new Identifier<>(d.getSimulationId(), d))))); // Processing
        }
    }

    @Override
    void subscribe(final String id, final Subscriber<SimulationOutputData> subscriber) {
        processor.subscribe(id, subscriber);
    }

    @Override
    public void pauseSimulation(final String id) {
        if (simulations.get(id).isRunning()) {
            simulations.get(id).pause();
            return;
        }
        throw new IllegalStateException("The simulation is not running");
    }

    @Override
    public List<String> getRunningSimulations() {
        return simulations.entrySet().stream().filter(e -> e.getValue().isRunning())
                .map(Entry::getKey).collect(Collectors.toList());
    }

    @Override
    public Integer getTickRate(final String id) {
        return simulations.get(id).getTickRate();
    }

    @Override
    public void setTickRate(final String id, final Integer tickRate) {
        simulations.get(id).setTickRate(tickRate);
    }

    @Override
    public String saveSimulation(final String id) {
        return manager.save(simulations.remove(id));
    }

}
