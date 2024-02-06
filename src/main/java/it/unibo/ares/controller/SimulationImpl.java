package it.unibo.ares.controller;

import java.util.concurrent.CompletableFuture;

import it.unibo.ares.model.Model;
import it.unibo.ares.utils.state.State;

public class SimulationImpl implements Simulation {
    
    public SimulationImpl(State state, Model model) {
        this.state = state;
        this.model = model;
        calculating = false;
    }

    private State state;
    private final Model model;
    private boolean running; //may be sincronized if we want to make it usable to await termination
    private boolean calculating;


    @Override
    public State getState() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'getState'");
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

    private SimulationData mapStateToSimulationData(State state, String simulationId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'mapStateToSimulationData'");
    }

    private void tickSim(){
        this.state = this.model.tick(this.state);
    }

    @Override
    public CompletableFuture<SimulationData> tick(String simulationId) {
        if(!this.running) {
            throw new IllegalStateException("Simulation is not running");
        }
        if(this.calculating) {            
            throw new IllegalStateException("Simulation is already calculating");
        }

        CompletableFuture<SimulationData> future = new CompletableFuture<>();

        new Thread(() -> {
            this.calculating = true;
            tickSim();
            future.complete(mapStateToSimulationData(this.state, simulationId));
            this.calculating = false;
        }).start();
    
        return future;
    }
    
}
