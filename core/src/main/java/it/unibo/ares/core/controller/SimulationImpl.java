package it.unibo.ares.core.controller;

import it.unibo.ares.core.api.SimulationOutputData;
import it.unibo.ares.core.model.Model;
import it.unibo.ares.core.utils.Pair;
import it.unibo.ares.core.utils.state.State;

import java.util.HashMap;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

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
    SimulationImpl(final State state, final Model model) {
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
        return this.state;
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
    public void pause() {
        this.running = false;
    }

    @Override
    public boolean isRunning() {
        return this.running;
    }

    private SimulationOutputData mapStateToSimulationData(final State state, final String simulationSessionId) {
        return new SimulationOutputData(state.getAgents().stream()
                .collect(Collectors.toMap(
                        Pair::getFirst,
                        pair -> pair.getSecond().getType(),
                        (existingValue, newValue) -> newValue,
                        HashMap::new)),
                simulationSessionId, state.getDimensions().getFirst(), state.getDimensions().getSecond());
    }

    private void tickSim() {
        this.state = this.model.tick(this.state);
    }

    @Override
    public CompletableFuture<SimulationOutputData> tick(final String simulationSessionId) {
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
            future.complete(mapStateToSimulationData(this.state, simulationSessionId));
            this.calculating = false;
        }).start();

        return future;
    }

    @Override
    public SimulationOutputData tickSync(final String simulationSessionId) {
        if (!this.running) {
            throw new IllegalStateException("Simulation is not running");
        }
        if (this.calculating) {
            throw new IllegalStateException("Simulation is already calculating");
        }

        this.calculating = true;
        tickSim();
        SimulationOutputData data = mapStateToSimulationData(this.state, simulationSessionId);
        this.calculating = false;

        return data;
    }

}
