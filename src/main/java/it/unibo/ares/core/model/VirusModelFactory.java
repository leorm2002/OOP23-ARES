
package it.unibo.ares.core.model;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import it.unibo.ares.core.agent.Agent;
import it.unibo.ares.core.agent.AgentFactory;
import it.unibo.ares.core.agent.VirusAgentFactory;
import it.unibo.ares.core.utils.UniquePositionGetter;
import it.unibo.ares.core.utils.parameters.ParameterDomainImpl;
import it.unibo.ares.core.utils.parameters.ParameterImpl;
import it.unibo.ares.core.utils.parameters.Parameters;
import it.unibo.ares.core.utils.pos.Pos;
import it.unibo.ares.core.utils.pos.PosImpl;
import it.unibo.ares.core.utils.state.State;
import it.unibo.ares.core.utils.state.StateImpl;

/**
 * This class generates an instance of a virus diffusion model.
 * It allows the parameterization of the number of agents (two types: "Person"
 * and "Infected").
 */
public final class VirusModelFactory implements ModelFactory {
    private static final String MODEL_ID = "VirusDiffusion";

    @Override
    public String getModelId() {
        return MODEL_ID;
    }

    /**
     * This method determines the type of the agent based on its index.
     *
     * @param na    The number of agents of type "Person".
     * @param index The index of the agent.
     * @return The type of the agent ("Person" or "Infected").
     */
    private static String getAgentType(final int na, final int index) {
        return index < na ? "P" : "I";
    }

    /**
     * This method initializes the state of the virus model.
     *
     * It retrieves the size of the grid, the number of healthy people, and the
     * number of infected people from the parameters.
     * It then checks if the total number of people is less than or equal to the
     * total number of cells in the grid.
     * If so, it creates a new state and populates it with agents at valid
     * positions.
     *
     * @param parameters The parameters required to initialize the model. It should
     *                   contain "size", "numeroPersoneSane", and
     *                   "numeroPersoneInfette".
     * @return The initialized state of the virus model.
     * @throws IllegalAccessException   If the required parameters are not provided.
     * @throws IllegalArgumentException If the total number of people is greater
     *                                  than the total number of cells in the grid.
     */
    private static State virusInitializer(final Parameters parameters) throws IllegalAccessException {
        int size = parameters.getParameter("size", Integer.class)
                .orElseThrow(IllegalAccessException::new).getValue();
        int p = parameters.getParameter("numeroPersoneSane", Integer.class)
                .orElseThrow(IllegalAccessException::new).getValue();
        int pInfected = parameters.getParameter("numeroPersoneInfette", Integer.class)
                .orElseThrow(IllegalAccessException::new).getValue();
        int total = p + pInfected;
        State state = new StateImpl(size, size);
        if (size * size < total) {
            throw new IllegalArgumentException("The number of agents is greater than the size of the grid");
        }
        List<Pos> validPositions = IntStream.range(0, size).boxed()
                .flatMap(i -> IntStream.range(0, size).mapToObj(j -> new PosImpl(i, j)))
                .map(Pos.class::cast)
                .toList();

        UniquePositionGetter getter = new UniquePositionGetter(validPositions);
        AgentFactory virusFactory = new VirusAgentFactory();
        for (int i = 0; i < p; i++) {
                Agent agent = virusFactory.createAgent();
                agent.setType("P");
                state.addAgent(getter.next(), agent);
        }
        for (int i = 0; i < pInfected; i++) {
                Agent agent = virusFactory.createAgent();
                agent.setType("I");
                state.addAgent(getter.next(), agent);
        }
        return state;
    }

    /**
     * This method builds and returns the virus on a network model.
     *
     * It adds parameters for the number of healthy people, the number of infected
     * people, and the grid size.
     * It also adds an exit function that checks if all agents in the old state are
     * present in the new state.
     * Finally, it adds an initialization function that initializes the virus model
     * with the provided parameters (if they are valid).
     *
     * @return The built model.
     * @throws IllegalArgumentException If the required parameters for the model
     *                                  initialization are not provided.
     */
    @Override
    public Model getModel() {
        ModelBuilder builder = new ModelBuilderImpl();
        return builder
                .addParameter(new ParameterImpl<>("numeroPersoneSane", Integer.class,
                        new ParameterDomainImpl<Integer>(
                                "Numero di persone sane (0-n)",
                                i -> i >= 0)))
                .addParameter(new ParameterImpl<>("numeroInfetti", Integer.class,
                        new ParameterDomainImpl<Integer>(
                                "Numero di persone infette (0-n)",
                                i -> i >= 0)))
                .addParameter(new ParameterImpl<>("size", Integer.class,
                        new ParameterDomainImpl<Integer>(
                                "Dimensione della griglia (1-n)",
                                i -> i > 0)))
                .addExitFunction((o, n) -> o.getAgents().stream()
                        .allMatch(p -> n.getAgents().stream().anyMatch(p::equals)))
                .addInitFunction(t -> {
                    try {
                        return virusInitializer(t);
                    } catch (IllegalAccessException e) {
                        throw new IllegalArgumentException(
                                "Missing parameters for the model initialization");
                    }
                })
                .build();
    }
}
