package it.unibo.ares.core.agent;

import it.unibo.ares.core.utils.directionvector.DirectionVector;
import it.unibo.ares.core.utils.directionvector.DirectionVectorImpl;
import it.unibo.ares.core.utils.parameters.ParameterDomainImpl;
import it.unibo.ares.core.utils.parameters.ParameterImpl;
import it.unibo.ares.core.utils.pos.Pos;
import it.unibo.ares.core.utils.pos.PosImpl;
import it.unibo.ares.core.utils.state.State;
import it.unibo.ares.core.utils.state.StateImpl;

import java.util.Random;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * A factory class for creating Fire agents.
 */
public final class FireAgentFactory implements AgentFactory {
        private static final Integer DIRRANDOMNUMBERCEIL = 20;
        private static final Integer VISION_ANGLE = 360;
        private static final Double CONSFACTOR = 0.1;
        private static final Double WINDCHANGEBASEPROB = 0.2;
        private DirectionVector windDirection;
        private Double windChange;
        private Random r;
        private ExtingueshedAgentFactory eaf;

        /**
         * Constructor for the FireAgentFactory.
         */
        public FireAgentFactory() {
                this.r = new Random();
                this.windChange = 0.0;
                this.windDirection = getRandomDirection();
                this.eaf = new ExtingueshedAgentFactory();
        }

        /**
         * A predicate to verify if two agents are of different types.
         */
        private static BiPredicate<Agent, Agent> isAgentOfDiffType = (a, b) -> {
                String typeA = a.getType();
                String typeB = b.getType();

                return !typeA.equals(typeB);
        };

        /**
         * Get a random direction.
         * 
         * @return a random direction vector.
         */
        private DirectionVectorImpl getRandomDirection() {
                return new DirectionVectorImpl(
                                r.nextInt(-DIRRANDOMNUMBERCEIL, DIRRANDOMNUMBERCEIL) + 1,
                                r.nextInt(-DIRRANDOMNUMBERCEIL, DIRRANDOMNUMBERCEIL) + 1);
        }

        /**
         * Verify if a Fire Agent is extinguished.
         * 
         * @param agent current fire agent
         * @return True if extinguished, false either way.
         */
        private static boolean isExtinguished(final Agent agent) {
                // Verify if at current position the Tree Agent can sustain the Fire Agent.
                Boolean notFueled = agent.getParameters().getParameter("fuel", Double.class)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Agent " + agent + " has no fuel parameter"))
                                .getValue() <= 0;

                return notFueled;
        }

        /**
         * Consumes the fuel of the Fire Agent.
         * 
         * @param agent current fire agent.
         */
        private void consumeFuel(final Agent agent) {
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

        private void changeWindDirection(final State state) {
                Integer nf = state.getAgents().stream()
                                .filter(a -> a.getSecond().getType().equals("F"))
                                .collect(Collectors.toList())
                                .size();

                if (r.nextDouble(0.0, 0.5) < windChange / nf) {
                        this.windDirection = getRandomDirection();
                        windChange = 0.0;
                } else {
                        windChange += WINDCHANGEBASEPROB / nf;
                }
        }

        /**
         * Creates a new Fire Agent replacing the Tree Agent.
         * 
         * @param state     current state
         * @param pos       current position
         * @param fireAgent spreading fire agent
         */
        private void spreadFire(final State state, final Pos pos, final Agent fireAgent) {
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

                Double cons = fireAgent.getParameters()
                                .getParameter("consumption", Double.class)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Agent " + fireAgent + " has no consumption parameter"))
                                .getValue();

                Double newCons = flammability == 0.0 ? 0.0 : flammability + (cons * CONSFACTOR);

                /* Starts a new fire */
                Agent newAgent = getFireModelAgent(spread, newFuel, newCons);

                state.removeAgent(pos, treeAgent);
                state.addAgent(pos, newAgent);
        }

        /**
         * Verify if a position is inside the cone of the Fire Agent.
         * 
         * @param pos      position to test
         * @param center   center of the cone
         * @param dir      direction of the cone
         * @param distance distance of the cone
         * @param angle    angle of the cone
         * 
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
         * Compute the positions inside the cone of the Fire Agent.
         * 
         * @param pos      position to test
         * @param dir      direction of the cone
         * @param distance distance of the cone
         * @param angle    angle of the cone
         * 
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
         * 
         * @return the positions where fire will spread if available.
         */
        private Set<Pos> getSpreadPositionIfAvailable(final State state, final Pos pos, final Agent agent) {
                DirectionVector dir = this.windDirection;

                Integer spread = agent.getParameters()
                                .getParameter("spread", Integer.class)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Agent " + agent + " has no spread parameter"))
                                .getValue();

                return IntStream.range(0, state.getDimensions().getFirst())
                                .boxed()
                                .flatMap(x -> IntStream.range(0, state.getDimensions().getSecond())
                                                .mapToObj(y -> new PosImpl(x, y)))
                                .filter(p -> computeCloseCells(pos, dir, spread, VISION_ANGLE).contains(p))
                                .filter(p -> state.getAgentAt(p).isPresent())
                                .filter(p -> isAgentOfDiffType.test(agent, state.getAgentAt(p).get()))
                                .filter(p -> TreeAgentFactory.isFlammable(state.getAgentAt(p).get()))
                                .collect(Collectors.toSet());
        }

        /**
         * The tick function for the Fire Agent.
         * 
         * @param currentState  current state
         * @param agentPosition current position of the fire agent
         * 
         * @return the new state after the tick.
         */
        private State tickFunction(final State currentState, final Pos agentPosition) {
                Agent agent = currentState.getAgentAt(agentPosition).get();
                changeWindDirection(currentState);
                consumeFuel(agent);

                if (isExtinguished(agent)) {
                        Agent exAgent = eaf.createAgent();
                        currentState.removeAgent(agentPosition, agent);
                        currentState.addAgent(agentPosition, exAgent);
                } else {
                        Set<Pos> spreadPos = getSpreadPositionIfAvailable(currentState, agentPosition, agent);
                        spreadPos.forEach(newPos -> spreadFire(currentState, newPos, agent));
                }
                return currentState;
        }

        /**
         * Builds the Fire Agent (only for tests).
         * 
         * @param spread spread radius
         * @param fuel   starting fuel
         * @param cons   starting consumption of the fuel
         * 
         * @return An instance of the Fire Agent.
         */
        private Agent getFireModelAgent(final Integer spread, final Double fuel, final Double cons) {
                AgentBuilder b = new AgentBuilderImpl();
                b
                                .addParameter(new ParameterImpl<>("spread", spread, false))
                                .addParameter(new ParameterImpl<>("fuel", fuel, false))
                                .addParameter(new ParameterImpl<>("consumption", cons, false))
                                .addStrategy((state, pos) -> tickFunction(state, pos))
                                .build();

                Agent a = b.build();
                a.setType("F");
                return a;
        }

        /**
         * Builds the Fire Agent.
         * 
         * @return An instance of the Fire Agent.
         */
        @Override
        public Agent createAgent() {
                AgentBuilder b = new AgentBuilderImpl();

                b
                                .addParameter(new ParameterImpl<>("spread", Integer.class,
                                                new ParameterDomainImpl<>("Range of spread (RoS) (1 - n)",
                                                                (Integer i) -> i > 0),
                                                true))
                                .addParameter(new ParameterImpl<>("fuel", Double.class,
                                                new ParameterDomainImpl<>(
                                                                "Capacità di combustibile",
                                                                (Double d) -> d >= 0.0),
                                                true))
                                .addParameter(new ParameterImpl<>("consumption", Double.class,
                                                new ParameterDomainImpl<>(
                                                                "Combustibile consumato ad ogni tick",
                                                                (Double d) -> d >= 0.0),
                                                true))
                                .addStrategy((state, pos) -> tickFunction(state, pos))
                                .build();

                Agent a = b.build();
                a.setType("F");
                return a;
        }
}
