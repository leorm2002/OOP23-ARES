package it.unibo.ares.core.model;

import it.unibo.ares.core.agent.AgentFactory;
import it.unibo.ares.core.agent.PredatorPreyAgentFactory;
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
 * A factory class for creating the Predator-Prey model.
 */
public final class PredatorPreyModelFactory implements ModelFactory {
        private static final String MODEL_ID = "PredatorPrey";

        @Override
        public String getModelId() {
                return MODEL_ID;
        }

        private State predatorPreyInitializer(final Parameters parameters) throws IllegalAccessException {
                int size = parameters.getParameter("size", Integer.class)
                                .orElseThrow(IllegalAccessException::new).getValue();
                int numAgents = parameters.getParameter("numeroAgenti", Integer.class)
                                .orElseThrow(IllegalAccessException::new).getValue();

                State state = new StateImpl(size, size);
                if (size * size < numAgents) {
                        throw new IllegalArgumentException("The number of agents is greater than the size of the grid");
                }

                List<Pos> validPositions = IntStream.range(0, size).boxed()
                                .flatMap(i -> IntStream.range(0, size).mapToObj(j -> new PosImpl(i, j)))
                                .collect(Collectors.toList());

                UniquePositionGetter getter = new UniquePositionGetter(validPositions);
                AgentFactory predatorPreyFactory = new PredatorPreyAgentFactory();
                Stream
                                .generate(predatorPreyFactory::createAgent)
                                .limit(numAgents)
                                .forEach(a -> state.addAgent(getter.next(), a));

                return state;
        }

        @Override
        public Model getModel() {
                ModelBuilder builder = new ModelBuilderImpl();
                return builder
                                .addParameter(new ParameterImpl<>("numeroAgenti", Integer.class,
                                                new ParameterDomainImpl<Integer>("Numero di agenti", n -> n >= 0),
                                                true))
                                .addParameter(new ParameterImpl<>("size", Integer.class,
                                                new ParameterDomainImpl<Integer>("Dimensione della griglia",
                                                                n -> n >= 0),
                                                true))
                                .addExitFunction(
                                                (o, n) -> n.getAgents().stream().map(a -> a.getSecond().getType())
                                                                .distinct().count() < 2)
                                .addInitFunction(t -> {
                                        try {
                                                return predatorPreyInitializer(t);
                                        } catch (IllegalAccessException e) {
                                                throw new IllegalArgumentException(
                                                                "Missing parameters for the model initialization");
                                        }
                                })
                                .build();
        }
}
