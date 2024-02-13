package it.unibo.ares.core.model.factories;

import java.util.List;
import java.util.function.Supplier;
import java.util.stream.IntStream;

import it.unibo.ares.core.agent.Agent;
import it.unibo.ares.core.agent.factories.SchellingsAgentFactory;
import it.unibo.ares.core.model.Model;
import it.unibo.ares.core.model.ModelBuilder;
import it.unibo.ares.core.model.ModelBuilderImpl;
import it.unibo.ares.core.utils.Pair;
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

        private State schellingInitializer(Parameters parameters) {
                int size = parameters.getParameter("size", Integer.class).get().getValue();
                int na = parameters.getParameter("numeroAgentiTipoA", Integer.class).get().getValue();
                int nb = parameters.getParameter("numeroAgentiTipoB", Integer.class).get().getValue();
                State state = new StateImpl(size, size);

                if (size * size < na + nb) {
                        throw new IllegalArgumentException("The number of agents is greater than the size of the grid");
                }
                List<Pos> pos = IntStream.range(0, size).boxed()
                                .flatMap(i -> IntStream.range(0, size).mapToObj(j -> new PosImpl(i, j)))
                                .map(Pos.class::cast)
                                .limit(na + nb)
                                .toList();

                IntStream.range(0, na).mapToObj(pos::get).forEach(p -> state.addAgent(p,
                                (Agent) parameters.getParameter("agentsSupplier", Supplier.class).get().getValue()
                                                .get()));

                IntStream.range(na - 1, na + nb).mapToObj(pos::get).forEach(p -> state.addAgent(p,
                                (Agent) parameters.getParameter("agentsSupplier", Supplier.class).get().getValue()
                                                .get()));
                return state;
        }

        public Pair<Model, State> getModel() {
                ModelBuilder builder = new ModelBuilderImpl();
                // We need only one agent supplier since all agents are equal and only differs
                // in the type
                return builder
                                .addParameter(new ParameterImpl<>("numeroAgentiTipoA", Integer.class))
                                .addParameter(new ParameterImpl<>("numeroAgentiTipoB", Integer.class))
                                .addParameter(new ParameterImpl<Supplier<Agent>>("agentsSupplier",
                                                SchellingsAgentFactory::getSchellingSegregationModelAgent))
                                .addParameter(new ParameterImpl<>("size", Integer.class))
                                .addExitFunction(s -> false)
                                .addInitFunction(this::schellingInitializer)
                                .build();

        }
}
