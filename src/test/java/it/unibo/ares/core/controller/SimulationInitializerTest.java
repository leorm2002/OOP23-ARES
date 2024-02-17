package it.unibo.ares.core.controller;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.unibo.ares.core.model.MockModelProvider;
import it.unibo.ares.core.model.Model;
import it.unibo.ares.core.model.ModelFactory;
import it.unibo.ares.core.model.SchellingModelFactories;
import it.unibo.ares.core.utils.parameters.Parameters;

public class SimulationInitializerTest {
    ModelFactory sf;

    @BeforeEach
    public void init() {
        sf = new SchellingModelFactories();
    }

    @Test
    public void testGetModels() {
        SimulationInitializerImpl simulationInitializerImpl = new SimulationInitializerImpl();
        assertTrue(simulationInitializerImpl.getModels().contains(sf.getModelId()));
    }

    @Test
    public void testInitializeModelWithoutSettingParameters() {
        SimulationInitializerImpl simulationInitializerImpl = new SimulationInitializerImpl();
        String modelId = sf.getModelId();
        String simId = simulationInitializerImpl.setModel(modelId);
        assertThrows(IllegalStateException.class, () -> simulationInitializerImpl.getAgentsSimplified(simId));
    }

    @Test
    public void testInitializeModel() {
        SimulationInitializerImpl simulationInitializer = new SimulationInitializerImpl();
        String modelId = sf.getModelId();
        String simId = simulationInitializer.setModel(modelId);
        simulationInitializer.setModelParameter(simId, "numeroAgentiTipoA", 10);
        simulationInitializer.setModelParameter(simId, "numeroAgentiTipoB", 10);
        simulationInitializer.setModelParameter(simId, "size", 15);
        assertTrue(simulationInitializer.getAgentsSimplified(simId).size() > 0);
    }

    @Test
    public void testParametrizeAgent() {
        SimulationInitializerImpl simulationInitializer = new SimulationInitializerImpl();
        String modelId = sf.getModelId();
        String simId = simulationInitializer.setModel(modelId);
        Parameters params = simulationInitializer.getModelParametersParameters(simId);
        simulationInitializer.setModelParameter(simId, "numeroAgentiTipoA", 10);
        simulationInitializer.setModelParameter(simId, "numeroAgentiTipoB", 10);
        simulationInitializer.setModelParameter(simId, "size", 15);
        assertTrue(simulationInitializer.getAgentsSimplified(simId).size() > 0);
        Set<String> agents = simulationInitializer.getAgentsSimplified(simId);
        String agent = agents.stream().findAny().get();
        Parameters agentParams = simulationInitializer.getAgentParametersSimplified(simId, agent);
        assertTrue(agentParams.getParametersToset().size() > 0);
    }

    @Test
    public void test1() {
        Model m = MockModelProvider.getMockModel();
        var state = m.initilize();
        state = m.tick(state);
        state = m.tick(state);
        state.debugPrint();
    }
}
