
package it.unibo.ares.core.model;

import it.unibo.ares.core.agent.Agent;
import it.unibo.ares.core.agent.AgentFactory;
import it.unibo.ares.core.agent.SchellingsAgentFactory;
import it.unibo.ares.core.utils.Pair;
import it.unibo.ares.core.utils.UniquePositionGetter;
import it.unibo.ares.core.utils.parameters.Parameter;
import it.unibo.ares.core.utils.parameters.ParameterDomainImpl;
import it.unibo.ares.core.utils.parameters.ParameterImpl;
import it.unibo.ares.core.utils.parameters.Parameters;
import it.unibo.ares.core.utils.pos.Pos;
import it.unibo.ares.core.utils.pos.PosImpl;
import it.unibo.ares.core.utils.state.State;
import it.unibo.ares.core.utils.state.StateImpl;
import it.unibo.ares.core.utils.statistics.Statistics;
import it.unibo.ares.core.utils.statistics.StatisticsGenerator;

import java.util.List;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * Generate an instance of a schelling segregation model. It permits the
 * paramtrization of:
 * the number of agents (two types)
 */
public final class SchellingModelFactory implements ModelFactory {
    private static final String MODEL_ID = "Schelling";
    private static final StatisticsGenerator GENERATOR;

    static {
        GENERATOR = s -> new Statistics() {
            @Override
            public List<Pair<String, String>> getStatistics() {
                final OptionalDouble vTot = s.getAgents().stream().parallel()
                        .map(Pair::getSecond)
                        .map(Agent::getParameters)
                        .map(p -> p.getParameter(SchellingsAgentFactory.CURRENT_RATIO, Double.class))
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .map(Parameter::getOptionalValue)
                        .filter(Optional::isPresent)
                        .mapToDouble(Optional::get)
                        .average();
                final OptionalDouble va = s.getAgents().stream().parallel()
                        .map(Pair::getSecond)
                        .filter(a -> "A".equals(a.getType()))
                        .map(Agent::getParameters)
                        .map(p -> p.getParameter(SchellingsAgentFactory.CURRENT_RATIO, Double.class))
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .map(Parameter::getOptionalValue)
                        .filter(Optional::isPresent)
                        .mapToDouble(Optional::get).average();

                final OptionalDouble vb = s.getAgents().stream().parallel()
                        .map(Pair::getSecond)
                        .filter(a -> "B".equals(a.getType()))
                        .map(Agent::getParameters)
                        .map(p -> p.getParameter(SchellingsAgentFactory.CURRENT_RATIO, Double.class))
                        .filter(Optional::isPresent)
                        .map(Optional::get)
                        .map(Parameter::getOptionalValue)
                        .filter(Optional::isPresent)
                        .mapToDouble(Optional::get).average();
                final String out = vTot.isPresent() ? String.valueOf(vTot.getAsDouble()) : "";
                final String outA = va.isPresent() ? String.valueOf(va.getAsDouble()) : "";
                final String outB = vb.isPresent() ? String.valueOf(vb.getAsDouble()) : "";
                return List.of(new Pair<>("Avg total ratio:", out),
                        new Pair<>("Avg A ratio:", outA),
                        new Pair<>("Avg B ratio:", outB));
            }
        };
    }

    @Override
    public String getModelId() {
        return MODEL_ID;
    }

    private static String getAgentType(final int na, final int index) {
        return index < na ? "A" : "B";
    }

    private static State schellingInitializer(final Parameters parameters) throws IllegalAccessException {
        final int size = parameters.getParameter(
                Model.SIZEKEY, Integer.class)
                .orElseThrow(IllegalAccessException::new).getValue();
        final int na = parameters.getParameter("numeroAgentiTipoA", Integer.class)
                .orElseThrow(IllegalAccessException::new).getValue();
        final int nb = parameters.getParameter("numeroAgentiTipoB", Integer.class)
                .orElseThrow(IllegalAccessException::new).getValue();
        final int total = na + nb;
        if (size * size < total) {
            throw new IllegalArgumentException("The number of agents is greater than the size of the grid");
        }
        final State state = new StateImpl(size, size);
        final List<Pos> validPositions = IntStream.range(0, size).boxed()
                .flatMap(i -> IntStream.range(0, size).mapToObj(j -> new PosImpl(i, j)))
                .map(Pos.class::cast)
                .toList();

        final UniquePositionGetter getter = new UniquePositionGetter(validPositions);
        final AgentFactory schellingFactory = new SchellingsAgentFactory();
        final List<Agent> agents = Stream
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
    @SuppressWarnings("PMD.PreserveStackTrace") // La causa è sempre qella
    public Model getModel() {
        // We need only one agent supplier since all agents are equal and only differs
        // in the type
        return new ModelBuilderImpl()
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
                .addStatisticsGenerator(GENERATOR)
                .build();
    }

}
