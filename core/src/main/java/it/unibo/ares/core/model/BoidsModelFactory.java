package it.unibo.ares.core.model;

import it.unibo.ares.core.agent.BoidsAgentFactory;
import it.unibo.ares.core.utils.UniquePositionGetter;
import it.unibo.ares.core.utils.parameters.ParameterDomainImpl;
import it.unibo.ares.core.utils.parameters.ParameterImpl;
import it.unibo.ares.core.utils.parameters.Parameters;
import it.unibo.ares.core.utils.pos.Pos;
import it.unibo.ares.core.utils.pos.PosImpl;
import it.unibo.ares.core.utils.state.State;
import it.unibo.ares.core.utils.state.StateImpl;
import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/**
 * A factory class for creating the Boids model.
 */
public final class BoidsModelFactory implements ModelFactory {

    private static final long serialVersionUID = 1L;
    private static final String MODEL_ID = "Boids";

    @Override
    public String getModelId() {
        return MODEL_ID;
    }

    private State schellingInitializer(final Parameters parameters) throws IllegalAccessException {
        final int size = parameters.getParameter(Model.SIZEKEY, Integer.class)
                .orElseThrow(IllegalAccessException::new).getValue();
        final int total = parameters.getParameter("numeroUccelli", Integer.class)
                .orElseThrow(IllegalAccessException::new).getValue();

        if (size * size < total) {
            throw new IllegalArgumentException("The number of agents is greater than the size of the grid");
        }
        final State state = new StateImpl(size, size);
        final List<Pos> validPositions = IntStream.range(0, size).boxed()
                .flatMap(i -> IntStream.range(0, size).mapToObj(j -> new PosImpl(i, j)))
                .map(Pos.class::cast)
                .toList();

        final UniquePositionGetter getter = new UniquePositionGetter(validPositions);
        final BoidsAgentFactory boidsAgentFactory = new BoidsAgentFactory();
        Stream
                .generate(boidsAgentFactory::createAgent)
                .limit(total)
                .forEach(a -> {
                    a.setType("B");
                    state.addAgent(getter.next(), a);
                });

        return state;
    }

    @Override
    /**
     * Creates a new model.
     * Should contain all the parameters needed for the model initialization:
     * - size: the size of the grid
     * - numeroUccelli: the number of agents
     * 
     * @return the model
     */
    @SuppressWarnings("PMD.PreserveStackTrace") // La causa è sempre qella
    public Model getModel() {
        // We need only one agent supplier since all agents are equal and only differs
        // in the type
        return new ModelBuilderImpl()

                .addParameter(new ParameterImpl<>("numeroUccelli", Integer.class,
                        new ParameterDomainImpl<Integer>("Numero di agenti (1-n)", n -> n > 0), true))
                .addParameter(new ParameterImpl<>(
                        Model.SIZEKEY, Integer.class,
                        new ParameterDomainImpl<Integer>("Dimensione della griglia (1-n)", n -> n > 0), true))
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
