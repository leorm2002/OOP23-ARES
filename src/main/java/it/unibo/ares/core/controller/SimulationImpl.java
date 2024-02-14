package it.unibo.ares.core.controller;

import java.util.concurrent.CompletableFuture;

import it.unibo.ares.core.controller.models.SimulationOutputData;
import it.unibo.ares.core.model.Model;
import it.unibo.ares.core.utils.state.State;

/**
 * A simulation is a class that contains the state of the simulation and the
 * model of the simulation.
 */
final class SimulationImpl implements Simulation {

    /**
     * Creates a new simulation with the given state and model.
     * 
     * @param state The state of the simulation.
     * @param model The model of the simulation.
     */
    public SimulationImpl(final State state, final Model model) {
        this.state = state;
        this.model = model;
        calculating = false;
    }

    private State state;
    private final Model model;
    private boolean running; // may be sincronized if we want to make it usable to await termination
    private boolean calculating;

    @Override
    public State getState() {
        return null;
    }

    @Override
    public Model getModel() {
        return this.model;
    }

    @Override
    public void start() {
        this.running = true;
    }

    @Override
    public boolean isRunning() {
        return this.running;
    }

    private SimulationOutputData mapStateToSimulationData(final State state, final String simulationId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'mapStateToSimulationData'");
    }

    private void tickSim() {
        this.state = this.model.tick(this.state);
    }

    @Override
    public CompletableFuture<SimulationOutputData> tick(final String simulationId) {
        if (!this.running) {
            throw new IllegalStateException("Simulation is not running");
        }
        if (this.calculating) {
            throw new IllegalStateException("Simulation is already calculating");
        }

        CompletableFuture<SimulationOutputData> future = new CompletableFuture<>();

        new Thread(() -> {
            this.calculating = true;
            tickSim();
            future.complete(mapStateToSimulationData(this.state, simulationId));
            this.calculating = false;
        }).start();

        return future;
    }

}
