package it.unibo.ares.core.agent;

import java.util.Set;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import it.unibo.ares.core.utils.directionvector.DirectionVector;
import it.unibo.ares.core.utils.directionvector.DirectionVectorImpl;
import it.unibo.ares.core.utils.parameters.ParameterDomainImpl;
import it.unibo.ares.core.utils.parameters.ParameterImpl;
import it.unibo.ares.core.utils.pos.Pos;
import it.unibo.ares.core.utils.pos.PosImpl;
import it.unibo.ares.core.utils.state.State;
import it.unibo.ares.core.utils.state.StateImpl;

/**
 * A factory class for creating agents for the Fire Spread Model.
 */
public final class FireSpreadAgentFactory implements AgentFactory {

        private static BiPredicate<Agent, Agent> isAgentOfDiffType = (a, b) -> {
                String typeA = a.getType();
                String typeB = b.getType();

                return !typeA.equals(typeB);
        };

        /**
         * Verify if a Tree-type Agent nearby can be burnt.
         * 
         * @param a Tree agent to test flammability
         * @return True if flammable, false either way.
         */
        private static boolean isFlammable(final Agent a) {
                Double flammable = a.getParameters()
                                .getParameter("flammability", Double.class)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Agent " + a + " has no flammable parameter"))
                                .getValue();

                return flammable > 0;
        };

        /**
         * Verify if a Fire-type Agent is extinguished.
         * 
         * @param state current state
         * @param pos   current position
         * @param agent current fire agent
         * @return True if extinguished, false either way.
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
         * 
         * @param state     current state
         * @param pos       current position
         * @param fireAgent current fire agent
         */
        public static void spreadFire(final State state, final Pos pos, final Agent fireAgent) {
                Agent oldAgent = state.getAgentAt(pos).get(); // old tree

                Double flammability = oldAgent.getParameters()
                                .getParameter("flammability", Double.class)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Agent " + oldAgent + " has no flammability parameter"))
                                .getValue();

                Double fuel = fireAgent.getParameters()
                                .getParameter("fuel", Double.class)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Agent " + fireAgent + " has no fuel parameter"))
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

                Double newFuel = oldAgent.getParameters()
                                .getParameter("fuel", Double.class)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Agent " + oldAgent + " has no fuel parameter"))
                                .getValue();

                fireAgent.setParameter("fuel", (fuel - flammability < 0.0) ? 0.0 : fuel - flammability);

                /* Starts a new fire */
                Agent newAgent = getFireModelAgent(visionRadius, dir, spread, newFuel);
                newAgent.setParameter("spread", spread);
                newAgent.setParameter("direction", dir);
                newAgent.setParameter("visionRadius", visionRadius);
                newAgent.setParameter("fuel", newFuel);

                state.removeAgent(pos, oldAgent);
                state.addAgent(pos, newAgent);
        }

        private static boolean insideCone(final Pos pos, final Pos center, final DirectionVector dir,
                        final Integer distance,
                        final Integer angle) {
                double radAng = Math.toRadians(angle);

                DirectionVector vectorToNewPoint = new DirectionVectorImpl(pos.diff(center).getX(),
                                pos.diff(center).getY());
                double dotProduct = dir.getNormalized().pointProduct(vectorToNewPoint.getNormalized());
                double radAngleBetween = Math.acos(dotProduct);

                return radAngleBetween <= radAng && vectorToNewPoint.getMagnitude() <= distance;
        }

        private static Set<Pos> computeCloseCells(final Pos pos, final DirectionVector dir, final Integer distance,
                        final Integer angle) {
                int xSign = dir.getX() > 0 ? 1 : -1;
                int ySign = dir.getY() > 0 ? 1 : -1;

                State a = new StateImpl(
                                Math.abs(pos.getX() + xSign * (distance + 1)),
                                Math.abs(pos.getY() + ySign * (distance + 1)));

                return a.getPosByPosAndRadius(pos, distance)
                                .stream()
                                .filter(p -> insideCone(p, pos, dir, distance, angle))
                                .collect(Collectors.toSet());
        }

        /**
         * Gets the spread of the Fire-type Agent.
         * 
         * @param state current state
         * @param pos   current position
         * @param agent current fire agent
         * @return the positions where fire will spread
         */
        private static Set<Pos> getSpreadPositionIfAvailable(final State state, final Pos pos, final Agent agent) {
                Integer visionRadius = agent.getParameters()
                                .getParameter("visionRadius", Integer.class)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Agent " + agent + " has no visionRadius parameter"))
                                .getValue();
                DirectionVector dir = agent.getParameters()
                                .getParameter("direction",
                                                DirectionVectorImpl.class)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Agent " + agent + " has no direction parameter"))
                                .getValue();

                return IntStream.range(0, state.getDimensions().getFirst())
                                .boxed()
                                .flatMap(x -> IntStream.range(0, state.getDimensions().getSecond())
                                                .mapToObj(y -> new PosImpl(x, y)))
                                .filter(p -> computeCloseCells(pos, dir, visionRadius, 0).contains(p))
                                .filter(p -> state.getAgentAt(p).isPresent())
                                .filter(p -> isAgentOfDiffType.test(agent, state.getAgentAt(p).get()))
                                .filter(p -> isFlammable(state.getAgentAt(p).get()))
                                // .filter(p -> isExtinguished(state, p, agent))
                                .collect(Collectors.toSet());
        }

        private static State tickFunction(final State currentState, final Pos agentPosition) {
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
         * 
         * @param visionRadius vision radius of the agent
         * @param dir          direction of the agent
         * @param spread       spread radius of the agent
         * @param fuel         starting fuel of the agent
         * @return An instance of the Fire-type Agent
         */
        public static Agent getFireModelAgent(final Integer visionRadius, final DirectionVector dir,
                        final Integer spread, final Double fuel) {
                AgentBuilder b = new AgentBuilderImpl();
                b
                                .addParameter(new ParameterImpl<>("type", "F"))
                                .addParameter(new ParameterImpl<>("visionRadius", visionRadius))
                                .addParameter(new ParameterImpl<>("direction", dir))
                                .addParameter(new ParameterImpl<>("spread", spread))
                                .addParameter(new ParameterImpl<>("fuel", fuel))
                                .addStrategy((state, pos) -> tickFunction(state, pos))
                                .build();

                Agent a = b.build();
                a.setType("F");
                return a;
        }

        /**
         * Builds the Fire-type Agent.
         * 
         * @return An instance of the Fire-type Agent
         */
        public Agent getFireModelAgent() {
                AgentBuilder b = new AgentBuilderImpl();

                b
                                .addParameter(new ParameterImpl<>("type", "F"))
                                .addParameter(new ParameterImpl<>("visionRadius", Integer.class,
                                                new ParameterDomainImpl<>(
                                                                "Range of vision of the agent (0 - n)",
                                                                (Integer i) -> i > 0)))
                                .addParameter(new ParameterImpl<>("spread", Integer.class,
                                                new ParameterDomainImpl<>(
                                                                "Range of spread (RoS) of the agent (0 - n)",
                                                                (Integer i) -> i > 0)))
                                .addParameter(new ParameterImpl<>("fuel", Double.class,
                                                new ParameterDomainImpl<>(
                                                                "Amount of fuel available for combustion (0.0-1.0)",
                                                                (Double d) -> d >= 0.0 && d <= 1.0)))
                                .addParameter(new ParameterImpl<>("direction", DirectionVectorImpl.class))
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

                Agent a = b.build();
                a.setType("F");
                return a;
        }

        /**
         * Builds the Tree-type Agent.
         * 
         * @param fuel         starting fuel of the agent
         * @param flammability flammability of the agent
         * @return An instance of the Tree-type Agent
         */
        public Agent getTreeModelAgent(final Double fuel, final Double flammability) {
                AgentBuilder b = new AgentBuilderImpl();

                b
                                .addParameter(new ParameterImpl<>("type", "T"))
                                .addParameter(new ParameterImpl<>("fuel", fuel))
                                .addParameter(new ParameterImpl<>("flammability", flammability))
                                .addStrategy((state, pos) -> {
                                        return state;
                                })
                                .build();

                Agent a = b.build();
                a.setType("T");
                return a;
        }

        /**
         * Builds the Tree-type Agent.
         * 
         * @return An instance of the Tree-type Agent
         */
        public Agent getTreeModelAgent() {
                AgentBuilder b = new AgentBuilderImpl();

                b
                                .addParameter(new ParameterImpl<>("type", "T"))
                                .addParameter(new ParameterImpl<>("fuel", Double.class,
                                                new ParameterDomainImpl<>(
                                                                "Amount of fuel available for combustion (0.0-1.0)",
                                                                (Double d) -> d >= 0.0 && d <= 1.0)))
                                .addParameter(new ParameterImpl<>("flammability", Double.class,
                                                new ParameterDomainImpl<>(
                                                                "Measure of how quick can burn (0.0-1.0)",
                                                                (Double d) -> d >= 0.0 && d <= 1.0)))
                                .addStrategy((state, pos) -> {
                                        return state;
                                })
                                .build();

                Agent a = b.build();
                a.setType("T");
                return a;
        }

        @Override
        public Agent createAgent() {
                AgentBuilder b = new AgentBuilderImpl();

                return b
                                .addParameter(new ParameterImpl<Integer>("type", Integer.class))
                                .addParameter(new ParameterImpl<Double>("fuel", Double.class))
                                // .addStrategy((state, pos) -> {
                                // return state;
                                // })
                                .build();
        }
}
