package it.unibo.ares.core.controller;

import it.unibo.ares.core.model.ModelFactory;
import it.unibo.ares.core.model.SchellingModelFactory;
import it.unibo.ares.core.utils.parameters.Parameters;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SimulationInitializerTest {
    private ModelFactory sf;

    @BeforeEach
    public void init() {
        sf = new SchellingModelFactory();
    }

    @Test
    void testGetModels() {
        SimulationInitializerImpl simulationInitializerImpl = new SimulationInitializerImpl();
        assertTrue(simulationInitializerImpl.getModels().contains(sf.getModelId()));
    }

    @Test
    void testInitializeModelWithoutSettingParameters() {
        SimulationInitializerImpl simulationInitializerImpl = new SimulationInitializerImpl();
        String modelId = sf.getModelId();
        String simId = simulationInitializerImpl.addNewModel(modelId);
        assertThrows(IllegalStateException.class, () -> simulationInitializerImpl.getAgentsSimplified(simId));
    }

    @SuppressWarnings("checkstyle:magicnumber")
    @Test
    void testInitializeModel() {
        SimulationInitializerImpl simulationInitializer = new SimulationInitializerImpl();
        String modelId = sf.getModelId();
        String simId = simulationInitializer.addNewModel(modelId);
        simulationInitializer.setModelParameter(simId, "numeroAgentiTipoA", 10);
        simulationInitializer.setModelParameter(simId, "numeroAgentiTipoB", 10);
        // CHECKSTYLE: MagicNumber OFF è una size per provare il metodo

        simulationInitializer.setModelParameter(simId, "size", 15);
        // CHECKSTYLE: MagicNumber ON
        assertTrue(simulationInitializer.getAgentsSimplified(simId).size() > 0);
    }

    @SuppressWarnings("checkstyle:magicnumber")
    @Test
    void testParametrizeAgent() {
        SimulationInitializerImpl simulationInitializer = new SimulationInitializerImpl();
        String modelId = sf.getModelId();
        String simId = simulationInitializer.addNewModel(modelId);
        simulationInitializer.setModelParameter(simId, "numeroAgentiTipoA", 10);
        simulationInitializer.setModelParameter(simId, "numeroAgentiTipoB", 10);
        // CHECKSTYLE: MagicNumber OFF è una size per provare il metodo
        simulationInitializer.setModelParameter(simId, "size", 15);
        // CHECKSTYLE: MagicNumber ON
        assertTrue(simulationInitializer.getAgentsSimplified(simId).size() > 0);
        Set<String> agents = simulationInitializer.getAgentsSimplified(simId);
        String agent = agents.stream().findAny().get();
        Parameters agentParams = simulationInitializer.getAgentParametersSimplified(simId, agent);
        assertTrue(agentParams.getParametersToset().size() > 0);
    }
}
