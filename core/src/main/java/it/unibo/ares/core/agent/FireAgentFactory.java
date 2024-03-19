package it.unibo.ares.core.agent;

import it.unibo.ares.core.utils.ComputationUtils;
import it.unibo.ares.core.utils.directionvector.DirectionVector;
import it.unibo.ares.core.utils.directionvector.DirectionVectorImpl;
import it.unibo.ares.core.utils.parameters.ParameterDomainImpl;
import it.unibo.ares.core.utils.parameters.ParameterImpl;
import it.unibo.ares.core.utils.pos.Pos;
import it.unibo.ares.core.utils.pos.PosImpl;
import it.unibo.ares.core.utils.state.State;

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
        private static final String FUEL = "fuel";
        private DirectionVector windDirection;
        private Double windChange;
        private final Random r;
        private final FireAgentFactory faf;
        private final ExtingueshedAgentFactory eaf;

        private static BiPredicate<Agent, Agent> agentOfDiffType = (a, b) -> {
                final String typeA = a.getType();
                final String typeB = b.getType();

                return !typeA.equals(typeB);
        };

        /**
         * Constructor for the FireAgentFactory.
         */
        public FireAgentFactory() {
                this.r = new Random();
                this.windChange = 0.0;
                this.windDirection = getRandomDirection();
                this.eaf = new ExtingueshedAgentFactory();
                this.faf = new FireAgentFactory();
        }

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
                return agent.getParameters().getParameter(FUEL, Double.class)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Agent " + agent + " has no fuel parameter"))
                                .getValue() <= 0;
        }

        /**
         * Consumes the fuel of the Fire Agent.
         * 
         * @param agent current fire agent.
         */
        private void consumeFuel(final Agent agent) {
                final Double fuel = agent.getParameters()
                                .getParameter(FUEL, Double.class)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Agent " + agent + " has no fuel parameter"))
                                .getValue();

                final Double cons = agent.getParameters()
                                .getParameter("consumption", Double.class)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Agent " + agent + " has no consumption parameter"))
                                .getValue();

                agent.setParameter(FUEL, (fuel - cons <= 0.0) ? 0.0 : fuel - cons);
        }

        private void changeWindDirection(final State state) {
                final Integer nf = state.getAgents().stream()
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
                final Agent treeAgent = state.getAgentAt(pos).get(); // tree agent to be replaced

                final Double flammability = treeAgent.getParameters()
                                .getParameter("flammability", Double.class)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Agent " + treeAgent + " has no flammability parameter"))
                                .getValue();

                final Double newFuel = treeAgent.getParameters()
                                .getParameter(FUEL, Double.class)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Agent " + treeAgent + " has no fuel parameter"))
                                .getValue();

                final Integer spread = fireAgent.getParameters()
                                .getParameter("spread", Integer.class)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Agent " + fireAgent + " has no spread parameter"))
                                .getValue();

                final Double cons = fireAgent.getParameters()
                                .getParameter("consumption", Double.class)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Agent " + fireAgent + " has no consumption parameter"))
                                .getValue();

                final Double newCons = flammability == 0.0 ? 0.0 : flammability + (cons * CONSFACTOR);

                /* Starts a new fire */
                final Agent newAgent = getFireAgent(spread, newFuel, newCons);

                state.removeAgent(pos, treeAgent);
                state.addAgent(pos, newAgent);
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
                final DirectionVector dir = this.windDirection;

                final Integer spread = agent.getParameters()
                                .getParameter("spread", Integer.class)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Agent " + agent + " has no spread parameter"))
                                .getValue();

                return IntStream.range(0, state.getDimensions().getFirst())
                                .boxed()
                                .flatMap(x -> IntStream.range(0, state.getDimensions().getSecond())
                                                .mapToObj(y -> new PosImpl(x, y)))
                                .filter(p -> ComputationUtils.computeCloseCells(pos, dir, spread, VISION_ANGLE)
                                                .contains(p))
                                .filter(p -> state.getAgentAt(p).isPresent())
                                .filter(p -> agentOfDiffType.test(agent, state.getAgentAt(p).get()))
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
                final Agent agent = currentState.getAgentAt(agentPosition).get();
                changeWindDirection(currentState);
                consumeFuel(agent);

                if (isExtinguished(agent)) {
                        final Agent exAgent = eaf.createAgent();
                        currentState.removeAgent(agentPosition, agent);
                        currentState.addAgent(agentPosition, exAgent);
                } else {
                        final Set<Pos> spreadPos = getSpreadPositionIfAvailable(currentState, agentPosition, agent);
                        spreadPos.forEach(newPos -> spreadFire(currentState, newPos, agent));
                }
                return currentState;
        }

        /**
         * Get a new Fire Agent with values.
         * 
         * @param spread spread radius
         * @param fuel   starting fuel
         * @param cons   starting consumption of the fuel
         * 
         * @return An instance of the Fire Agent.
         */
        private Agent getFireAgent(final Integer spread, final Double fuel, final Double cons) {
                Agent a = faf.createAgent();
                a.setParameter(FUEL, fuel);
                a.setParameter("spread", spread);
                a.setParameter("consumption", cons);
                return a;
        }

        /**
         * Builds the Fire Agent.
         * 
         * @return An instance of the Fire Agent.
         */
        @Override
        public Agent createAgent() {
                final AgentBuilder b = new AgentBuilderImpl();

                b
                                .addParameter(new ParameterImpl<>("spread", Integer.class,
                                                new ParameterDomainImpl<>("Range of spread (RoS) (1 - n)",
                                                                (Integer i) -> i > 0),
                                                true))
                                .addParameter(new ParameterImpl<>(FUEL, Double.class,
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
