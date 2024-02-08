package it.unibo.ares.core.controller;

import java.util.Map;

public class SimulationController{
    Map<String, Simulation> simulations;
    SimulationDataProvider<SimulationData> processor = new SimulationDataProvider<>();

    void addSimulation(String id, Simulation simulation) {
        simulations.put(id, simulation);
    }

    void removeSimulation(String id) {
        simulations.remove(id);
    }

    void startSimulation(String id) {
        simulations.get(id).start();
    }

    void onTick(){
        simulations.entrySet().stream()
            .filter(e -> e.getValue().isRunning())
            .map(e -> e.getValue().tick(e.getKey())) // Starting the calculation and mapping the future to the id of the simulation
            .forEach(f -> f.thenAccept(simData -> processor.submit(new Identifier<>(simData.getSimulationId(), simData)))); // Processing the future
    }


}
