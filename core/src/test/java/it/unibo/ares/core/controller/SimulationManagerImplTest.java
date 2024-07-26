package it.unibo.ares.core.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import it.unibo.ares.core.agent.Agent;
import it.unibo.ares.core.model.Model;
import it.unibo.ares.core.utils.Pair;
import it.unibo.ares.core.utils.pos.Pos;
import it.unibo.ares.core.utils.pos.PosImpl;
import it.unibo.ares.core.utils.state.State;

class SimulationManagerImplTest {

    @Mock(serializable = true)
    private State mockState;

    @Mock(serializable = true)
    private Model mockModel;

    @Mock(serializable = true)
    private Agent mockAgent;

    @Mock(serializable = true)
    private Pos mockPos;

    private SimulationImpl simulation;
    private SimulationManager simulationManager;
    private static final Integer TICKRATE = 100;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        // CHECKSTYLE: MagicNumber OFF
        when(mockState.getAgents()).thenReturn(
                IntStream.range(1, 5)
                        .boxed()
                        .flatMap(i -> IntStream.range(1, 3).mapToObj(j -> new PosImpl(i, j)))
                        .map(p -> new Pair<>((Pos) p, mockAgent)).collect(Collectors.toSet()));
        when(mockState.getDimensions()).thenReturn(new Pair<>(10, 10));
        simulation = new SimulationImpl(mockState, mockModel, TICKRATE);
        simulationManager = new SimulationManagerImpl();
    }

    @Test
    void testSaveLoadSimulation() {
        // Save the simulation
        final String filePath = simulationManager.save(simulation);

        assertNotNull(filePath);
        assertFalse(filePath.isEmpty());

        final Simulation loadedSimulation = simulationManager.load(filePath);

        assertNotNull(loadedSimulation);

        assertEquals(simulation.getState().getAgents().stream().map(Pair::getFirst).collect(Collectors.toSet()),
                loadedSimulation.getState().getAgents().stream().map(Pair::getFirst).collect(Collectors.toSet()));
        assertEquals(simulation.getState().getDimensions(), loadedSimulation.getState().getDimensions());
        assertEquals(simulation.getModel().getClass(), loadedSimulation.getModel().getClass());
    }

}
