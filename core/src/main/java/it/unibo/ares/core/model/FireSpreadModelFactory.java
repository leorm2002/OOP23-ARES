
package it.unibo.ares.core.model;

import it.unibo.ares.core.agent.FireAgentFactory;
import it.unibo.ares.core.agent.TreeAgentFactory;
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
 * Generate an instance of a fire spread model. It permits the
 * paramtrization of:
 * the number of the Fire agents and the size of the grid.
 */
public class FireSpreadModelFactory implements ModelFactory {
    private static final String MODEL_ID = "FireSpread";

    /**
     * @return the model ID of the FireSpreadModelFactory.
     */
    @Override
    public String getModelId() {
        return MODEL_ID;
    }

    /**
     * This method initializes the state of the fire spread model.
     * 
     * It retrieves the size of the grid and the number of Fire-type agentsfrom the
     * parameters.
     * It then checks if the total number of agents is less than or equal to the
     * total number of cells in the grid.
     * If so, it creates a new state and populates it with agents at valid
     * positions.
     * 
     * @param parameters The parameters required to initialize the model. It should
     *                   contain "size" and numeroAgentiTipoF".
     * @return The initialized state of the fire spread model.
     * @throws IllegalAccessException   If the required parameters are not provided.
     * @throws IllegalArgumentException If the total number of agents is greater
     *                                  than the total number of cells in the grid.
     */
    private static State fireSpreadInitializer(final Parameters parameters) throws IllegalAccessException {
        final Integer size = parameters.getParameter(Model.SIZEKEY, Integer.class).orElseThrow().getValue();
        final Integer nf = parameters.getParameter("numFire", Integer.class).get().getValue();
        final Double veg = parameters.getParameter("vegetation", Double.class).get().getValue();

        final Integer total = size * size;
        if (total < nf) {
            throw new IllegalArgumentException("The number of agents is greater than the size of the grid");
        }

        final Integer nt = (int) ((total - nf) * veg);
        final State state = new StateImpl(size, size);
        final List<Pos> validPositions = IntStream.range(0, size).boxed()
                .flatMap(i -> IntStream.range(0, size).mapToObj(j -> new PosImpl(i, j)))
                .map(Pos.class::cast)
                .toList();
        final UniquePositionGetter getter = new UniquePositionGetter(validPositions);

        final FireAgentFactory fireAgentFactory = new FireAgentFactory();
        Stream
                .generate(fireAgentFactory::createAgent)
                .limit(nf)
                .forEach(a -> state.addAgent(getter.next(), a));

        final TreeAgentFactory treeAgentFactory = new TreeAgentFactory();
        Stream
                .generate(treeAgentFactory::createAgent)
                .limit(nt)
                .forEach(a -> state.addAgent(getter.next(), a));

        return state;
    }

    /**
     * Returns a fire spread model, before calling initialize you should set:
     * numeroAgentiTipoF (integer) and size (integer).
     * 
     * @return an instance of the Fire Spread model.
     * @throws IllegalAccessException If the required parameters are not provided.
     */
    @Override
    public Model getModel() {
        return new ModelBuilderImpl()
                .addParameter(new ParameterImpl<>("numFire", Integer.class,
                        new ParameterDomainImpl<Integer>("Numero di agenti fuoco (1-n)",
                                n -> n > 0),
                        true))
                .addParameter(new ParameterImpl<>("vegetation", Double.class,
                        new ParameterDomainImpl<Double>("Percentuale di vegetazione (0.0-1.0)",
                                (Double d) -> d >= 0.0 && d <= 1.0),
                        true))
                .addParameter(new ParameterImpl<>(
                        Model.SIZEKEY, Integer.class,
                        new ParameterDomainImpl<Integer>("Dimensione della griglia (1-n)",
                                n -> n > 0),
                        true))
                .addExitFunction((o, n) -> n.getAgents().stream()
                        .map(a -> a.getSecond().getType()).distinct()
                        .allMatch(t -> !"F".equals(t)))
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
