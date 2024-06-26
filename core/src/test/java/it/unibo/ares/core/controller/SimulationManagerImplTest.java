package it.unibo.ares.core.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Test;

import it.unibo.ares.core.model.Model;
import it.unibo.ares.core.utils.state.State;

class SimulationManagerImplTest {

    @Test
    void testSaveAndLoadSimulation() {
        // Create a mock or an actual implementation of Simulation
        State mockState = mock(State.class);
        Model mockModel = mock(Model.class);

        // Create an instance of SimulationImpl using the mocks
        SimulationImpl simulation = new SimulationImpl(mockState, mockModel, 50);

        // Simulate ticking the simulation twice

        // Create an instance of SimulationManagerImpl
        SimulationManagerImpl simulationManager = new SimulationManagerImpl();

        // Save the simulation
        String filePath = simulationManager.save(simulation);
        assertFalse(filePath.isEmpty(), "The file path should not be empty");

        // Load the simulation
        Simulation loadedSimulation = simulationManager.load(filePath);
        assertNotNull(loadedSimulation, "The loaded simulation should not be null");

        // assertEquals(loadedSimulation.getModel(), simulation.getModel());
        // assertEquals(simulation.hashCode(), loadedSimulation.hashCode(),
        // "The loaded simulation should be equal to the saved simulation");
    }
}