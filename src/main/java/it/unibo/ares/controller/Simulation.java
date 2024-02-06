package it.unibo.ares.controller;

import java.util.concurrent.CompletableFuture;

import it.unibo.ares.model.Model;
import it.unibo.ares.utils.state.State;

public interface Simulation {
    public State getState();
    public Model getModel();
    public void start();
    public boolean isRunning();
    public  CompletableFuture<SimulationData> tick(String simulationId);
}
