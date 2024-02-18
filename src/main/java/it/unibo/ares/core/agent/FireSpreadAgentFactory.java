package it.unibo.ares.core.agent;

import java.util.Set;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import it.unibo.ares.core.utils.directionvector.DirectionVector;
import it.unibo.ares.core.utils.directionvector.DirectionVectorImpl;
import it.unibo.ares.core.utils.parameters.ParameterImpl;
import it.unibo.ares.core.utils.pos.Pos;
import it.unibo.ares.core.utils.pos.PosImpl;
import it.unibo.ares.core.utils.state.State;

/**
 * A factory class for creating agents for the Fire Spread Model.
 */
public final class FireSpreadAgentFactory {

        private static Integer FIRE_TYPE = 1;
        private static Integer TREE_TYPE = 2;

        private static BiPredicate<Agent, Agent> isAgentOfDiffType = (a, b) -> {
                Integer typeA = a.getParameters().getParameter("type", Integer.class)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Agent " + a + " has no type parameter"))
                                .getValue();
                Integer typeB = b.getParameters().getParameter("type", Integer.class)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Agent " + b + " has no type parameter"))
                                .getValue();
                return !typeA.equals(typeB);
        };

        /**
         * Verify if a Tree-type Agent nearby can be burnt.
         */
        private static boolean isFlammable(Agent a) {
                Double flammable = a.getParameters()
                                .getParameter("flammability", Double.class)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Agent " + a + " has no flammable parameter"))
                                .getValue();

                return flammable > 0;
        };

        /**
         * Verify if a Fire-type Agent is extinguished.
         */
        private static boolean isExtinguished(final State state, final Pos pos, final Agent agent) {
                Integer visionRadius = agent.getParameters().getParameter("visionRadius", Integer.class)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Agent " + agent + " has no visionRadius parameter"))
                                .getValue();

                // Verify if at current position the Tree Agent can sustain the Fire Agent.
                Boolean notFueled = agent.getParameters().getParameter("fuel", Double.class)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Agent " + agent + " has no fuel parameter"))
                                .getValue() <= 0;

                // Verify if there are near trees that can spread the fire.
                Boolean cannotSpread = state.getAgentsByPosAndRadius(pos, visionRadius)
                                .stream()
                                .filter(a -> isAgentOfDiffType.test(a, agent))
                                .filter(a -> isFlammable(a))
                                .count() == 0;

                return cannotSpread && notFueled;
        }

        /**
         * Creates a new Fire-type Agent replacing the Tree-type Agent at pos.
         */
        public static void spreadFire(final State state, final Pos pos, final Agent fireAgent) {
                Agent oldAgent = state.getAgentAt(pos).get(); // old tree

                Double cons = oldAgent.getParameters()
                                .getParameter("flammability", Double.class)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Agent " + oldAgent + " has no flammability parameter"))
                                .getValue();

                Integer spread = fireAgent.getParameters()
                                .getParameter("spread", Integer.class)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Agent " + fireAgent + " has no spread parameter"))
                                .getValue();

                Integer visionRadius = fireAgent.getParameters()
                                .getParameter("visionRadius", Integer.class)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Agent " + fireAgent + " has no visionRadius parameter"))
                                .getValue();

                DirectionVector dir = fireAgent.getParameters()
                                .getParameter("direction", DirectionVectorImpl.class)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Agent " + fireAgent + " has no direction parameter"))
                                .getValue();

                Double fuel = oldAgent.getParameters()
                                .getParameter("fuel", Double.class)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Agent " + oldAgent + " has no fuel parameter"))
                                .getValue();

                /* Start a new fire */
                Agent newAgent = getFireModelAgent();
                newAgent.setParameter("spread", spread);
                newAgent.setParameter("direction", dir);
                newAgent.setParameter("visionRadius", visionRadius);
                newAgent.setParameter("fuel", fuel);

                fireAgent.setParameter("fuel", fireAgent.getParameters()
                                .getParameter("fuel", Double.class)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Agent " + fireAgent + " has no fuel parameter"))
                                .getValue() - cons);

                state.removeAgent(pos, oldAgent);
                state.addAgent(pos, newAgent);

        }

        /**
         * Gets the spread of the Fire-type Agent.
         */
        private static Set<Pos> getSpreadPositionIfAvailable(final State state, final Pos pos, final Agent agent) {
                Integer visionRadius = agent.getParameters()
                                .getParameter("visionRadius", Integer.class)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Agent " + agent + " has no visionRadius parameter"))
                                .getValue();

                return IntStream.range(0, state.getDimensions().getFirst())
                                .boxed()
                                .flatMap(x -> IntStream.range(0, state.getDimensions().getSecond())
                                                .mapToObj(y -> new PosImpl(x, y)))
                                .filter(p -> state.getPosByPosAndRadius(pos, visionRadius).contains(p))
                                .filter(p -> state.getAgentAt(p).isPresent())
                                .filter(p -> isAgentOfDiffType.test(agent, state.getAgentAt(p).get()))
                                .filter(p -> isFlammable(state.getAgentAt(p).get()))
                                // .filter(p -> isExtinguished(state, p, agent))
                                .collect(Collectors.toSet());
        }

        private State tickFunction(final State currentState, final Pos agentPosition) {
                Agent agent = currentState.getAgentAt(agentPosition).get();

                if (!isExtinguished(currentState, agentPosition, agent)) {
                        Set<Pos> spreadPos = getSpreadPositionIfAvailable(currentState, agentPosition, agent);
                        spreadPos.forEach(newPos -> spreadFire(currentState, newPos, agent));
                } else {
                        currentState.removeAgent(agentPosition, agent);
                }
                return currentState;
        }

        /**
         * Builds the Fire-type Agent.
         */
        public Agent getFireModelAgent(final Integer type, final Integer visionRadius,
                        final DirectionVector dir, final Integer spread, final Double fuel) {
                AgentBuilder builder = new AgentBuilderImpl();

                return builder
                                .addParameter(new ParameterImpl<>("type", FIRE_TYPE))
                                .addParameter(new ParameterImpl<>("visionRadius", visionRadius))
                                .addParameter(new ParameterImpl<>("direction", dir))
                                .addParameter(new ParameterImpl<>("spread", spread))
                                .addParameter(new ParameterImpl<>("fuel", fuel))
                                .addStrategy(this::tickFunction)
                                .build();
        }

        public static Agent getFireModelAgent() {
                AgentBuilder builder = new AgentBuilderImpl();

                return builder
                                .addParameter(new ParameterImpl<>("type", FIRE_TYPE))
                                .addParameter(new ParameterImpl<>("visionRadius", Integer.class))
                                .addParameter(new ParameterImpl<>("direction", DirectionVectorImpl.class))
                                .addParameter(new ParameterImpl<>("spread", Integer.class))
                                .addParameter(new ParameterImpl<>("fuel", Double.class))
                                .addStrategy((state, pos) -> {
                                        Agent agent = state.getAgentAt(pos).get();

                                        if (!isExtinguished(state, pos, agent)) {
                                                Set<Pos> spreadPos = getSpreadPositionIfAvailable(state, pos, agent);
                                                spreadPos.forEach(newPos -> spreadFire(state, newPos, agent));
                                        } else {
                                                state.removeAgent(pos, agent);
                                        }
                                        return state;
                                })
                                .build();
        }

        /**
         * Builds the Tree-type Agent.
         */
        public Agent getTreeModelAgent(final Integer type, final Double fuel, final Double flammability) {
                AgentBuilder builder = new AgentBuilderImpl();

                builder.addParameter(new ParameterImpl<>("type", TREE_TYPE));
                builder.addParameter(new ParameterImpl<>("fuel", fuel));
                builder.addParameter(new ParameterImpl<>("flammability", flammability));

                builder.addStrategy((state, pos) -> {
                        return state;
                });

                return builder.build();
        }

        /**
         * Builds the Tree-type Agent.
         */
        public static Agent getTreeModelAgent() {
                AgentBuilder builder = new AgentBuilderImpl();

                builder.addParameter(new ParameterImpl<>("type", TREE_TYPE));
                builder.addParameter(new ParameterImpl<>("fuel", Double.class));
                builder.addParameter(new ParameterImpl<>("flammability", Double.class));

                builder.addStrategy((state, pos) -> {
                        return state;
                });

                return builder.build();
        }
}
