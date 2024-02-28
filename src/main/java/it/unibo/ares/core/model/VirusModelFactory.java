package it.unibo.ares.core.model;

import java.util.List;
import java.util.stream.IntStream;
import java.util.stream.Stream;

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
 * A factory class for implementing the virus on a network model.
 */
public final class VirusModelFactory implements ModelFactory {
    private static final String MODEL_ID = "Virus";

    /**
     * Initializes the state of the virus model.
     *
     * @param parameters The parameters required to initialize the model. It should
     *                   contain "size" and "numeroPersone".
     * @return The initialized state of the virus model.
     * @throws IllegalAccessException   If the required parameters are not provided.
     * @throws IllegalArgumentException If the number of agents is greater than the
     *                                  size of the grid.
     */
    private State virusInitializer(final Parameters parameters) throws IllegalAccessException {
        int size = parameters.getParameter("size", Integer.class)
                .orElseThrow(IllegalAccessException::new).getValue();
        int total = parameters.getParameter("numeroPersone", Integer.class)
                .orElseThrow(IllegalAccessException::new).getValue();
        State state = new StateImpl(size, size);
        //check if the number of agents is greater than the size of the grid
        if (size * size < total) {
            throw new IllegalArgumentException("The number of agents is greater than the size of the grid");
        }
        //create a list of valid positions
        List<Pos> validPositions = IntStream.range(0, size).boxed()
                .flatMap(i -> IntStream.range(0, size).mapToObj(j -> new PosImpl(i, j)))
                .map(Pos.class::cast)
                .toList();
        /*
         * create a UniquePositionGetter for having an unique position for each agent
         * and a VirusAgentFactory for creating n agents of the model, then add the 
         * agents to the state
         */
        UniquePositionGetter getter = new UniquePositionGetter(validPositions);
        VirusAgentFactory virusAgentFactory = new VirusAgentFactory();
        Stream.generate(virusAgentFactory::createAgent)
            .limit(total).forEach(a -> {
                a.setType("P");
                state.addAgent(getter.next(), a);
            });
        return state;
    }

    /**
     * This method returns the model ID.
     *
     * @return The model ID.
     */
    @Override
    public String getModelId() {
        return MODEL_ID;
    }


    /**
     * This method builds and returns the model.
     *
     * @return The built model.
     */
    @Override
    public Model getModel() {
        ModelBuilder builder = new ModelBuilderImpl();
        /*
         * adding the parameters needed for the model initialization, only one agent
         */
        return builder
                .addParameter(new ParameterImpl<>("numeroPersone", Integer.class,
                        new ParameterDomainImpl<Integer>("Numero di agenti (1-n)", n -> n > 0)))
                .addParameter(new ParameterImpl<>("size", Integer.class,
                        new ParameterDomainImpl<Integer>("Dimensione della griglia (1-n)", n -> n > 0)))
                .addExitFunction((o, n) -> false)
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
