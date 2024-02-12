package it.unibo.ares.core.controller;

public interface SimulationsController {

    void addSimulation(String id, Simulation simulation);

    void removeSimulation(String id);

    void startSimulation(String id);

    void onTick();

}