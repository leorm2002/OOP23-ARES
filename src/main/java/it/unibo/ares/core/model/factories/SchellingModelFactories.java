package it.unibo.ares.core.model.factories;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import it.unibo.ares.core.agent.Agent;
import it.unibo.ares.core.agent.factories.SchellingsAgentFactory;
import it.unibo.ares.core.model.Model;
import it.unibo.ares.core.model.ModelBuilder;
import it.unibo.ares.core.model.ModelBuilderImpl;
import it.unibo.ares.core.utils.parameters.ParameterImpl;
import it.unibo.ares.core.utils.parameters.Parameters;
import it.unibo.ares.core.utils.pos.Pos;
import it.unibo.ares.core.utils.pos.PosImpl;
import it.unibo.ares.core.utils.state.State;
import it.unibo.ares.core.utils.state.StateImpl;

/**
 * Generate an instance of a schelling segregation model. It permits the
 * paramtrization of:
 * the number of agents (two types)
 */
public class SchellingModelFactories {
        private class UniquePositionGetter {
                List<Pos> positions;
                Random r;
                Set<Integer> extracted;

                UniquePositionGetter(final List<Pos> positions) {
                        r = new Random();
                        this.positions = positions;
                        this.extracted = new HashSet<>();
                }

                Pos get() {
                        int position = Stream.generate(() -> r.nextInt(positions.size()))
                                        .filter(i -> !extracted.contains(i))
                                        .limit(1)
                                        .findAny()
                                        .orElseThrow();
                        extracted.add(position);
                        return positions.get(position);
                }
        }

        private int getAgentType(final int na, final int nb, final int index) {
                return index < na ? 0 : 1;
        }

        private State schellingInitializer(final Parameters parameters) {
                int size = parameters.getParameter("size", Integer.class).get().getValue();
                int na = parameters.getParameter("numeroAgentiTipoA", Integer.class).get().getValue();
                int nb = parameters.getParameter("numeroAgentiTipoB", Integer.class).get().getValue();
                State state = new StateImpl(size, size);
                if (size * size < na + nb) {
                        throw new IllegalArgumentException("The number of agents is greater than the size of the grid");
                }
                List<Pos> validPositions = IntStream.range(0, size).boxed()
                                .flatMap(i -> IntStream.range(0, size).mapToObj(j -> new PosImpl(i, j)))
                                .map(Pos.class::cast)
                                .toList();

                UniquePositionGetter getter = new UniquePositionGetter(validPositions);

                List<Agent> agents = Stream
                                .generate(SchellingsAgentFactory::getSchellingSegregationModelAgent)
                                .limit(na + nb)
                                .collect(Collectors.toList());
                IntStream.range(0, na + nb)
                                .forEach(i -> agents.get(i).setParameter("type", getAgentType(na, nb, i)));
                IntStream.range(0, na + nb)
                                .forEach(i -> state.addAgent(getter.get(), agents.get(i)));

                return state;
        }

        /**
         * Returna a schelling model, before calling initialize you should set:
         * numeroAgentiTipoA (integer)
         * numeroAgentiTipoB (integer)
         * size (integer)
         * 
         * @return
         */
        public Model getModel() {
                ModelBuilder builder = new ModelBuilderImpl();
                // We need only one agent supplier since all agents are equal and only differs
                // in the type
                return builder
                                .addParameter(new ParameterImpl<>("numeroAgentiTipoA", Integer.class))
                                .addParameter(new ParameterImpl<>("numeroAgentiTipoB", Integer.class))
                                .addParameter(new ParameterImpl<Supplier<Agent>>("agentsSupplier",
                                                SchellingsAgentFactory::getSchellingSegregationModelAgent))
                                .addParameter(new ParameterImpl<>("size", Integer.class))
                                .addExitFunction((o, n) -> o.getAgents().stream()
                                                .allMatch(p -> n.getAgents().stream().anyMatch(p::equals)))
                                .addInitFunction(this::schellingInitializer)
                                .build();

        }
}
