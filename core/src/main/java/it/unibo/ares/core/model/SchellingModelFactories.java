
package it.unibo.ares.core.model;

import it.unibo.ares.core.agent.Agent;
import it.unibo.ares.core.agent.AgentFactory;
import it.unibo.ares.core.agent.SchellingsAgentFactory;
import it.unibo.ares.core.utils.UniquePositionGetter;
import it.unibo.ares.core.utils.parameters.ParameterDomainImpl;
import it.unibo.ares.core.utils.parameters.ParameterImpl;
import it.unibo.ares.core.utils.parameters.Parameters;
import it.unibo.ares.core.utils.pos.Pos;
import it.unibo.ares.core.utils.pos.PosImpl;
import it.unibo.ares.core.utils.state.State;
import it.unibo.ares.core.utils.state.StateImpl;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Generate an instance of a schelling segregation model. It permits the
 * paramtrization of:
 * the number of agents (two types)
 */
public final class SchellingModelFactories implements ModelFactory {
    private static final String MODEL_ID = "Schelling";

    @Override
    public String getModelId() {
        return MODEL_ID;
    }

    private static String getAgentType(final int na, final int index) {
        return index < na ? "A" : "B";
    }

    private static State schellingInitializer(final Parameters parameters) throws IllegalAccessException {
        int size = parameters.getParameter(
                Model.SIZEKEY, Integer.class)
                .orElseThrow(IllegalAccessException::new).getValue();
        int na = parameters.getParameter("numeroAgentiTipoA", Integer.class)
                .orElseThrow(IllegalAccessException::new).getValue();
        int nb = parameters.getParameter("numeroAgentiTipoB", Integer.class)
                .orElseThrow(IllegalAccessException::new).getValue();
        int total = na + nb;
        State state = new StateImpl(size, size);
        if (size * size < total) {
            throw new IllegalArgumentException("The number of agents is greater than the size of the grid");
        }
        List<Pos> validPositions = IntStream.range(0, size).boxed()
                .flatMap(i -> IntStream.range(0, size).mapToObj(j -> new PosImpl(i, j)))
                .map(Pos.class::cast)
                .toList();

        UniquePositionGetter getter = new UniquePositionGetter(validPositions);
        AgentFactory schellingFactory = new SchellingsAgentFactory();
        List<Agent> agents = Stream
                .generate(schellingFactory::createAgent)
                .limit(total)
                .collect(Collectors.toList());
        IntStream.range(0, total)
                .forEach(i -> agents.get(i).setType(getAgentType(na, i)));
        IntStream.range(0, total)
                .forEach(i -> state.addAgent(getter.next(), agents.get(i)));

        return state;
    }

    /**
     * Returna a schelling model, before calling initialize you should set:
     * numeroAgentiTipoA (integer)
     * numeroAgentiTipoB (integer)
     * size (integer).
     * 
     * @return a scheeling model
     */
    @Override
    public Model getModel() {
        ModelBuilder builder = new ModelBuilderImpl();
        // We need only one agent supplier since all agents are equal and only differs
        // in the type
        return builder
                .addParameter(new ParameterImpl<>("numeroAgentiTipoA", Integer.class,
                        new ParameterDomainImpl<Integer>(
                                "Numero di agenti del primo tipo (0-n)",
                                i -> i >= 0),
                        true))
                .addParameter(new ParameterImpl<>("numeroAgentiTipoB", Integer.class,
                        new ParameterDomainImpl<Integer>(
                                "Numero di agenti del secondo tipo (0-n)",
                                i -> i >= 0),
                        true))
                .addParameter(new ParameterImpl<>(
                        Model.SIZEKEY, Integer.class,
                        new ParameterDomainImpl<Integer>(
                                "Dimensione della griglia (1-n)",
                                i -> i > 0),
                        true))
                .addExitFunction((o, n) -> o.getAgents().stream()
                        .allMatch(p -> n.getAgents().stream().anyMatch(p::equals)))
                .addInitFunction(t -> {
                    try {
                        return schellingInitializer(t);
                    } catch (IllegalAccessException e) {
                        throw new IllegalArgumentException(
                                "Missing parameters for the model initialization");
                    }
                })
                .build();

    }
}
