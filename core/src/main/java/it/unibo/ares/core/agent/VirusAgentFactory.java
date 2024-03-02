package it.unibo.ares.core.agent;

import it.unibo.ares.core.utils.Pair;
import it.unibo.ares.core.utils.directionvector.DirectionVector;
import it.unibo.ares.core.utils.directionvector.DirectionVectorImpl;
import it.unibo.ares.core.utils.parameters.ParameterDomainImpl;
import it.unibo.ares.core.utils.parameters.ParameterImpl;
import it.unibo.ares.core.utils.pos.Pos;
import it.unibo.ares.core.utils.pos.PosImpl;
import it.unibo.ares.core.utils.state.State;

import java.util.Random;
import java.util.function.BiPredicate;

/**
 * A factory class for creating agents for the Schelling Segregation Model.
 */
public final class VirusAgentFactory implements AgentFactory {

        private Random r;


        public VirusAgentFactory() {
                r = new Random();
        }

        /*
         * A predicate to check if two agents are of the same type.
         */
        private static BiPredicate<Agent, Agent> isAgentOfSameType = (a, b) -> {
                String typeA = a.getType();
                String typeB = b.getType();
                return typeA.equals(typeB);
        };

        /**
         * Moves the agent in the given direction by the given step size.
         *
         * @param initialPos The initial position of the agent.
         * @param dir        The direction in which the agent should move.
         * @param stepSize   The number of steps the agent should take.
         * @return The new position of the agent.
         */
        private Pos move(final Pos initialPos, final DirectionVector dir, final Integer stepSize) {
                return new PosImpl(initialPos.getX() + dir.getNormalizedX() * stepSize,
                                initialPos.getY() + dir.getNormalizedY() * stepSize);
        }

        /**
         * Limits the given value to the range [0, max - 1].
         *
         * @param curr The current value.
         * @param max  The maximum value.
         * @return The limited value.
         */
        private int limit(final int curr, final int max) {
                return curr < 0 ? 0 : curr > (max - 1) ? (max - 1) : curr;
        }

        /**
         * Limits the given position to the size of the environment.
         *
         * @param pos  The current position.
         * @param size The size of the environment.
         * @return The limited position.
         */
        private Pos limit(final Pos pos, final Pair<Integer, Integer> size) {
                return new PosImpl(limit(pos.getX(), size.getFirst()), limit(pos.getY(), size.getSecond()));
        }

        /**
         * Generates a random direction for the agent to move in.
         *
         * @return The random direction.
         */
        private DirectionVectorImpl getRandomDirection() {
                return new DirectionVectorImpl(r.nextInt(20) + 1, r.nextInt(20) + 1);
        }

        /**
         * Updates the state of the agent based on its current state and position.
         *
         * @param currentState  The current state of the agent.
         * @param agentPosition The position of the agent.
         * @return The updated state of the agent.
         */
        private State tickFunction(final State currentState, final Pos agentPosition) {
                if (!currentState.getAgentAt(agentPosition).isPresent()) {
                        return currentState;
                }
                Agent agent = currentState.getAgentAt(agentPosition).get();
                if (!agent.getParameters().getParametersToset().isEmpty()) {
                        throw new RuntimeException("Parameters not set");
                }

                DirectionVector dir = agent.getParameters()
                                .getParameter("direction", DirectionVectorImpl.class).get().getValue();

                int stepSize = agent.getParameters().getParameter("stepSize", Integer.class)
                                .get().getValue();
                Pos newPos = move(agentPosition, dir, stepSize);
                if (!currentState.isInside(newPos)) {
                        dir = new DirectionVectorImpl(-dir.getX(), -dir.getY());
                        newPos = limit(move(agentPosition, dir, stepSize), currentState.getDimensions());
                }
                if (!currentState.isFree(newPos)) {
                        if (!isAgentOfSameType.test(currentState.getAgentAt(newPos).get(),
                                        currentState.getAgentAt(agentPosition).get())) {
                                currentState.getAgentAt(agentPosition).get().setType("I");
                        }
                }
                if (currentState.isFree(newPos)) {
                        currentState.moveAgent(agentPosition, newPos);
                }
                return currentState;
        }

        @Override
        public Agent createAgent() {
                AgentBuilder b = new AgentBuilderImpl();
                b.addParameter(new ParameterImpl<>("stepSize", Integer.class,
                                new ParameterDomainImpl<>("la dimensione del passo (1-10)",
                                                (Integer d) -> d > 0 && d <= 10)));
                b.addParameter(new ParameterImpl<>("direction", getRandomDirection()));
                b.addStrategy(this::tickFunction);
                return b.build();
        }
}
