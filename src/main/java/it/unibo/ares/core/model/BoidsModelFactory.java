package it.unibo.ares.core.model;

import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import it.unibo.ares.core.agent.BoidsAgentFactory;
import it.unibo.ares.core.utils.parameters.ParameterImpl;
import it.unibo.ares.core.utils.parameters.Parameters;
import it.unibo.ares.core.utils.pos.Pos;
import it.unibo.ares.core.utils.pos.PosImpl;
import it.unibo.ares.core.utils.state.State;
import it.unibo.ares.core.utils.state.StateImpl;
import it.unibo.ares.core.utils.uniquepositiongetter.UniquePositionGetter;

public class BoidsModelFactory implements ModelFactory {
    private static final String MODEL_ID = "Boids";

    public String getModelId() {
        return MODEL_ID;
    }

    private State schellingInitializer(final Parameters parameters) throws IllegalAccessException {
        int size = parameters.getParameter("size", Integer.class)
                .orElseThrow(IllegalAccessException::new).getValue();
        int total = parameters.getParameter("numeroUccelli", Integer.class)
                .orElseThrow(IllegalAccessException::new).getValue();

        State state = new StateImpl(size, size);
        if (size * size < total) {
            throw new IllegalArgumentException("The number of agents is greater than the size of the grid");
        }
        List<Pos> validPositions = IntStream.range(0, size).boxed()
                .flatMap(i -> IntStream.range(0, size).mapToObj(j -> new PosImpl(i, j)))
                .map(Pos.class::cast)
                .toList();

        UniquePositionGetter getter = new UniquePositionGetter(validPositions);
        BoidsAgentFactory boidsAgentFactory = new BoidsAgentFactory();
        Stream
                .generate(boidsAgentFactory::createAgent)
                .limit(total)
                .forEach(a -> {
                    a.setType("B");
                    state.addAgent(getter.get(), a);
                });

        return state;
    }

    public Model getModel() {
        ModelBuilder builder = new ModelBuilderImpl();
        // We need only one agent supplier since all agents are equal and only differs
        // in the type
        return builder
                .addParameter(new ParameterImpl<>("numeroUccelli", Integer.class))
                .addParameter(new ParameterImpl<>("size", Integer.class))
                .addExitFunction((o, n) -> false)
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
