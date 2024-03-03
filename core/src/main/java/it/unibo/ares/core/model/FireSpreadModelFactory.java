
package it.unibo.ares.core.model;

import it.unibo.ares.core.agent.Agent;
import it.unibo.ares.core.agent.AgentFactory;
import it.unibo.ares.core.agent.FireSpreadAgentFactory;
import it.unibo.ares.core.utils.UniquePositionGetter;
import it.unibo.ares.core.utils.parameters.ParameterImpl;
import it.unibo.ares.core.utils.parameters.Parameters;
import it.unibo.ares.core.utils.pos.Pos;
import it.unibo.ares.core.utils.pos.PosImpl;
import it.unibo.ares.core.utils.state.State;
import it.unibo.ares.core.utils.state.StateImpl;

import java.util.List;
import java.util.stream.IntStream;

/**
 * Generate an instance of a fire spread model. It permits the
 * paramtrization of:
 * the number of agents (two types: Fire and Tree)
 */
public class FireSpreadModelFactory implements ModelFactory {
        private static final String MODEL_ID = "FireSpread";

        /**
         * @return the model ID of the FireSpreadModelFactory.
         */
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
                Integer size = parameters.getParameter("size", Integer.class).orElseThrow().getValue();
                Integer nf = parameters.getParameter("numeroAgentiTipoF", Integer.class).get().getValue();

                Integer total = size * size;

                State state = new StateImpl(size, size);
                if (total < nf) {
                        throw new IllegalArgumentException("The number of agents is greater than the size of the grid");
                }

                List<Pos> validPositions = IntStream.range(0, size).boxed()
                                .flatMap(i -> IntStream.range(0, size).mapToObj(j -> new PosImpl(i, j)))
                                .map(Pos.class::cast)
                                .toList();

                UniquePositionGetter getter = new UniquePositionGetter(validPositions);
                AgentFactory fireSpreadFactory = new FireSpreadAgentFactory();

                for (int i = 0; i < nf; i++) {
                        Agent agent = ((FireSpreadAgentFactory) fireSpreadFactory).getFireModelAgent();
                        agent.setType("F");
                        state.addAgent(getter.next(), agent);
                }
                for (int i = 0; i < total - nf; i++) {
                        Agent agent = ((FireSpreadAgentFactory) fireSpreadFactory).getTreeModelAgent();
                        agent.setType("T");
                        state.addAgent(getter.next(), agent);
                }

                return state;
        }

        /**
         * Returns a fire spread model, before calling initialize you should set:
         * numeroAgentiTipoF (integer), numeroAgentiTipoT (integer), size (integer),
         * direction (DirectionVector), and consumption (double).
         * 
         * @return an instance of the Fire Spread model.
         * @throws IllegalAccessException If the required parameters are not provided.
         */
        public Model getModel() {
                ModelBuilder builder = new ModelBuilderImpl();
                // We need only one agent supplier since all agents are equal and only differs
                // in the type
                return builder
                                .addParameter(new ParameterImpl<>("numeroAgentiTipoF", Integer.class, true))
                                .addParameter(new ParameterImpl<>("size", Integer.class, true))
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
