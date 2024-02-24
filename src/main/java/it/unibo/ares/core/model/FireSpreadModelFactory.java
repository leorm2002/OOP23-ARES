
package it.unibo.ares.core.model;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import it.unibo.ares.core.agent.Agent;
import it.unibo.ares.core.agent.AgentFactory;
import it.unibo.ares.core.agent.FireSpreadAgentFactory;
import it.unibo.ares.core.utils.directionvector.DirectionVectorImpl;
import it.unibo.ares.core.utils.parameters.ParameterImpl;
import it.unibo.ares.core.utils.parameters.Parameters;
import it.unibo.ares.core.utils.pos.Pos;
import it.unibo.ares.core.utils.pos.PosImpl;
import it.unibo.ares.core.utils.state.State;
import it.unibo.ares.core.utils.state.StateImpl;
import it.unibo.ares.core.utils.UniquePositionGetter;

/**
 * Generate an instance of a fire spread model. It permits the
 * paramtrization of:
 * the number of agents (two types: Fire and Tree)
 */
public class FireSpreadModelFactory implements ModelFactory {
        private static final String MODEL_ID = "FireSpread";

        public String getModelId() {
                return MODEL_ID;
        }

        private static String getAgentType(final int nf, final int index) {
                return index < nf ? "F" : "T";
        }

        private static State fireSpreadInitializer(final Parameters parameters) throws IllegalAccessException {
                Integer size = parameters.getParameter("size", Integer.class).orElseThrow().getValue();
                Integer nf = parameters.getParameter("numeroAgentiTipoF", Integer.class).get().getValue();
                Integer nt = parameters.getParameter("numeroAgentiTipoT", Integer.class).get().getValue();

                Integer total = nf + nt;

                State state = new StateImpl(size, size);
                if (size * size < total) {
                        throw new IllegalArgumentException("The number of agents is greater than the size of the grid");
                }

                List<Pos> validPositions = IntStream.range(0, size).boxed()
                                .flatMap(i -> IntStream.range(0, size).mapToObj(j -> new PosImpl(i, j)))
                                .map(Pos.class::cast)
                                .toList();

                UniquePositionGetter getter = new UniquePositionGetter(validPositions);
                AgentFactory fireSpreadFactory = new FireSpreadAgentFactory();
                List<Agent> agents = Stream
                                .generate(fireSpreadFactory::createAgent)
                                .limit(total)
                                .collect(Collectors.toList());

                IntStream.range(0, nf)
                                .forEach(i -> agents.get(i).setType("F"));
                IntStream.range(nf + 1, nt)
                                .forEach(i -> agents.get(i).setType("T"));

                IntStream.range(0, total)
                                .forEach(i -> state.addAgent(getter.next(), agents.get(i)));
                return state;
        }

        /**
         * Returna a schelling model,
         * before calling initialize you should set:
         * numeroAgentiTipoF (integer)
         * numeroAgentiTipoT (integer)
         * size (integer)
         * direction (2d vector)
         * fuel consumption (double)
         * 
         * @return an instance of the Fire Spread model.
         */
        public Model getModel() {
                ModelBuilder builder = new ModelBuilderImpl();
                // We need only one agent supplier since all agents are equal and only differs
                // in the type
                return builder
                                .addParameter(new ParameterImpl<>("numeroAgentiTipoA", Integer.class))
                                .addParameter(new ParameterImpl<>("numeroAgentiTipoB", Integer.class))
                                .addParameter(new ParameterImpl<>("size", Integer.class))
                                .addParameter(new ParameterImpl<>("direction", DirectionVectorImpl.class))
                                .addParameter(new ParameterImpl<>("consumption", Double.class))
                                .addExitFunction((o, n) -> o.getAgents().stream()
                                                .allMatch(p -> n.getAgents().stream().anyMatch(p::equals)))
                                .addInitFunction(t -> {
                                        try {
                                                return fireSpreadInitializer(t);
                                        } catch (IllegalAccessException e) {
                                                throw new IllegalArgumentException(
                                                                "Missing parameters for the model initialization");
                                        }
                                })
                                .build();
        }
}
