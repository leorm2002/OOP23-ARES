package it.unibo.ares.core.agent;

import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;

import it.unibo.ares.core.utils.parameters.ParameterImpl;
import it.unibo.ares.core.utils.Pair;
import it.unibo.ares.core.utils.directionvector.DirectionVector;
import it.unibo.ares.core.utils.directionvector.DirectionVectorImpl;
import it.unibo.ares.core.utils.parameters.ParameterDomainImpl;
import it.unibo.ares.core.utils.pos.Pos;
import it.unibo.ares.core.utils.pos.PosImpl;
import it.unibo.ares.core.utils.state.State;
import it.unibo.ares.core.utils.state.StateImpl;

/**
 * A factory class for creating agents for the Schelling Segregation Model.
 */
public final class VirusAgentFactory implements AgentFactory {

        Random r;

        public VirusAgentFactory() {
                r = new Random();
        }
        
        private static BiPredicate<Agent, Agent> isAgentOfSameType = (a, b) -> {
                String typeA = a.getType();

                String typeB = b.getType();
                return typeA.equals(typeB);
        };

        private Pos move(final Pos initialPos, final DirectionVector dir, final Integer stepSize) {
                return new PosImpl(initialPos.getX() + dir.getNormalizedX() * stepSize,
                                initialPos.getY() + dir.getNormalizedY() * stepSize);
        }

        private int limit(final int curr, final int max) {
                return curr < 0 ? 0 : curr > (max - 1) ? (max - 1) : curr;
        }

        private Pos limit(final Pos pos, final Pair<Integer, Integer> size) {
                return new PosImpl(limit(pos.getX(), size.getFirst()), limit(pos.getY(), size.getSecond()));
        }

        private DirectionVectorImpl getRandomDirection() {
                return new DirectionVectorImpl(r.nextInt(20) + 1, r.nextInt(20) + 1);
        }

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
                        if(currentState.getAgentAt(newPos).get().getType().equals("I")
                                        && currentState.getAgentAt(agentPosition).get().getType().equals("P")) {
                                //check probabilità infezione
                                currentState.getAgentAt(agentPosition).get().setType("I");
                        }
                        if (!isAgentOfSameType.test(currentState.getAgentAt(newPos).get(),
                                        currentState.getAgentAt(agentPosition).get())) {
                                if (currentState.getAgentAt(newPos).get().getType().equals("I")) {
                                        //check probabilità infezione
                                        currentState.getAgentAt(agentPosition).get().setType("I");
                                }
                                else {
                                        currentState.getAgentAt(newPos).get().setType("I");
                                }
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
