package it.unibo.ares.core.controller;

import java.util.concurrent.CompletableFuture;

import it.unibo.ares.core.controller.models.SimulationOutputData;
import it.unibo.ares.core.model.Model;
import it.unibo.ares.core.utils.state.State;

public interface Simulation {
    public State getState();
    public Model getModel();
    public void start();
    public boolean isRunning();
    public  CompletableFuture<SimulationOutputData> tick(String simulationId);
}
