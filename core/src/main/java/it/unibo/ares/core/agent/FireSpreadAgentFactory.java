package it.unibo.ares.core.agent;

import java.util.Random;
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
        static Random r;

        /**
         * Constructor for the FireSpreadAgentFactory.
         */
        public FireSpreadAgentFactory() {
                r = new Random();
        }

        private static BiPredicate<Agent, Agent> isAgentOfDiffType = (a, b) -> {
                String typeA = a.getType();
                String typeB = b.getType();

                return !typeA.equals(typeB);
        };

        /**
         * @return a random direction vector for the fire spread model agents.
         */
        private static DirectionVectorImpl getRandomDirection() {
                r = new Random();
                Integer wn = r.nextInt(4);
                switch (wn) {
                        case 0:
                                return new DirectionVectorImpl(1, 0);
                        case 1:
                                return new DirectionVectorImpl(0, 1);
                        case 2:
                                return new DirectionVectorImpl(-1, 0);
                        case 3:
                                return new DirectionVectorImpl(0, -1);
                        default:
                                return new DirectionVectorImpl(1, 0);
                }
        }

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
                Integer spread = agent.getParameters().getParameter("spread", Integer.class)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Agent " + agent + " has no spread parameter"))
                                .getValue();

                // Verify if at current position the Tree Agent can sustain the Fire Agent.
                Boolean notFueled = agent.getParameters().getParameter("fuel", Double.class)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Agent " + agent + " has no fuel parameter"))
                                .getValue() <= 0;

                // Verify if there are near trees that can spread the fire.
                Boolean cannotSpread = state.getAgentsByPosAndRadius(pos, spread)
                                .stream()
                                .filter(a -> isAgentOfDiffType.test(a, agent))
                                .filter(a -> isFlammable(a))
                                .count() == 0;

                return cannotSpread && notFueled;
        }

        /**
         * Consumes the fuel of the Fire-type Agent.
         * 
         * @param agent current fire agent.
         */
        private static void consumeFuel(final Agent agent) {
                Double fuel = agent.getParameters()
                                .getParameter("fuel", Double.class)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Agent " + agent + " has no fuel parameter"))
                                .getValue();

                Double cons = agent.getParameters()
                                .getParameter("consumption", Double.class)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Agent " + agent + " has no consumption parameter"))
                                .getValue();

                agent.setParameter("fuel", (fuel - cons <= 0.0) ? 0.0 : fuel - cons);
        }

        /**
         * Creates a new Fire-type Agent replacing the Tree-type Agent at position pos.
         * 
         * @param state     current state
         * @param pos       current position
         * @param fireAgent spreading fire agent
         */
        public static void spreadFire(final State state, final Pos pos, final Agent fireAgent) {
                Agent treeAgent = state.getAgentAt(pos).get(); // tree agent to be replaced

                Double flammability = treeAgent.getParameters()
                                .getParameter("flammability", Double.class)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Agent " + treeAgent + " has no flammability parameter"))
                                .getValue();

                Double newFuel = treeAgent.getParameters()
                                .getParameter("fuel", Double.class)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Agent " + treeAgent + " has no fuel parameter"))
                                .getValue();

                Integer spread = fireAgent.getParameters()
                                .getParameter("spread", Integer.class)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Agent " + fireAgent + " has no spread parameter"))
                                .getValue();

                /* Starts a new fire */
                Agent newAgent = getFireModelAgent(spread, newFuel, flammability);

                state.removeAgent(pos, treeAgent);
                state.addAgent(pos, newAgent);
        }

        /**
         * Verify if a position is inside the cone of the Fire-type Agent.
         * 
         * @param pos      position to test
         * @param center   center of the cone
         * @param dir      direction of the cone
         * @param distance distance of the cone
         * @param angle    angle of the cone
         * @return True if inside the cone, false either way.
         */
        private static boolean insideCone(final Pos pos, final Pos center, final DirectionVector dir,
                        final Integer distance, final Integer angle) {
                double radAng = Math.toRadians(angle);

                DirectionVector vectorToNewPoint = new DirectionVectorImpl(pos.diff(center).getX(),
                                pos.diff(center).getY());
                double dotProduct = dir.getNormalized().pointProduct(vectorToNewPoint.getNormalized());
                double radAngleBetween = Math.acos(dotProduct);

                return radAngleBetween <= radAng && vectorToNewPoint.getMagnitude() <= distance;
        }

        /**
         * Compute the positions inside the cone of the Fire-type Agent.
         * 
         * @param pos      position to test
         * @param dir      direction of the cone
         * @param distance distance of the cone
         * @param angle    angle of the cone
         * @return the positions inside the cone
         */
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
         * Get the positions where fire will spread.
         * 
         * @param state current state
         * @param pos   current position of fire agent
         * @param agent current fire agent
         * @return the positions where fire will spread if available (Tree-type Agents).
         */
        private static Set<Pos> getSpreadPositionIfAvailable(final State state, final Pos pos, final Agent agent) {
                DirectionVector dir = getRandomDirection();
                Integer spread = agent.getParameters()
                                .getParameter("spread", Integer.class)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Agent " + agent + " has no spread parameter"))
                                .getValue();

                return IntStream.range(0, state.getDimensions().getFirst())
                                .boxed()
                                .flatMap(x -> IntStream.range(0, state.getDimensions().getSecond())
                                                .mapToObj(y -> new PosImpl(x, y)))
                                .filter(p -> computeCloseCells(pos, dir, spread, 0).contains(p))
                                .filter(p -> state.getAgentAt(p).isPresent())
                                .filter(p -> isAgentOfDiffType.test(agent, state.getAgentAt(p).get()))
                                .filter(p -> isFlammable(state.getAgentAt(p).get()))
                                .collect(Collectors.toSet());
        }

        /**
         * The tick function for the Fire-type Agent.
         * 
         * @param currentState  current state
         * @param agentPosition current position of the fire agent
         * @return the new state after the tick
         */
        private static State tickFunction(final State currentState, final Pos agentPosition) {
                Agent agent = currentState.getAgentAt(agentPosition).get();
                consumeFuel(agent);

                if (!isExtinguished(currentState, agentPosition, agent)) {
                        Set<Pos> spreadPos = getSpreadPositionIfAvailable(currentState, agentPosition, agent);
                        spreadPos.forEach(newPos -> spreadFire(currentState, newPos, agent));
                } else {
                        currentState.removeAgent(agentPosition, agent);
                }
                return currentState;
        }

        /**
         * Builds the Fire-type Agent (only for tests).
         * 
         * @param spread spread radius
         * @param fuel   starting fuel
         * @param cons   starting consumption of the fuel
         * @return An instance of the Fire-type Agent
         */
        public static Agent getFireModelAgent(final Integer spread, final Double fuel, final Double cons) {
                AgentBuilder b = new AgentBuilderImpl();
                b
                                .addParameter(new ParameterImpl<>("spread", spread))
                                .addParameter(new ParameterImpl<>("fuel", fuel))
                                .addParameter(new ParameterImpl<>("consumption", cons))
                                .addStrategy((state, pos) -> tickFunction(state, pos))
                                .build();

                Agent a = b.build();
                a.setType("F");
                return a;
        }

        /**
         * Builds the Fire-type Agent.
         * 
         * @return An instance of the Fire-type Agent (with default parameters).
         */
        public Agent getFireModelAgent() {
                AgentBuilder b = new AgentBuilderImpl();

                b
                                .addParameter(new ParameterImpl<>("spread", Integer.class,
                                                new ParameterDomainImpl<>(
                                                                "Range of spread (RoS) of the agent (0 - n)",
                                                                (Integer i) -> i > 0)))
                                .addParameter(new ParameterImpl<>("fuel", Double.class,
                                                new ParameterDomainImpl<>(
                                                                "Amount of fuel available for combustion (0.0-1.0)",
                                                                (Double d) -> d >= 0.0 && d <= 1.0)))
                                .addParameter(new ParameterImpl<>("consumption", Double.class,
                                                new ParameterDomainImpl<>(
                                                                "Amount of fuel consumption every tick (0.0-1.0)",
                                                                (Double d) -> d >= 0.0 && d <= 1.0)))
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
         * Builds the Tree-type Agent (only for tests).
         * 
         * @param fuel         starting fuel
         * @param flammability flammability
         * @return An instance of the Tree-type Agent
         */
        public Agent getTreeModelAgent(final Double fuel, final Double flammability) {
                AgentBuilder b = new AgentBuilderImpl();

                b
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
         * @return An instance of the Tree-type Agent (with default parameters).
         */
        public Agent getTreeModelAgent() {
                AgentBuilder b = new AgentBuilderImpl();

                b
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

        /**
         * Builds the Fire Spread Agent.
         * 
         * @return An instance of the Fire Spread Agent.
         */
        @Override
        public Agent createAgent() {
                AgentBuilder b = new AgentBuilderImpl();

                return b
                                .addParameter(new ParameterImpl<Double>("fuel", Double.class))
                                // .addStrategy((state, pos) -> {
                                // return state;
                                // })
                                .build();
        }
}
