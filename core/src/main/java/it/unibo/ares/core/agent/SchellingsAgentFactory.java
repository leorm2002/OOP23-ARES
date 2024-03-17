package it.unibo.ares.core.agent;

import it.unibo.ares.core.utils.parameters.ParameterDomainImpl;
import it.unibo.ares.core.utils.parameters.ParameterImpl;
import it.unibo.ares.core.utils.pos.Pos;
import it.unibo.ares.core.utils.pos.PosImpl;
import it.unibo.ares.core.utils.state.State;

import java.util.Random;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;

/**
 * A factory class for creating agents for the Schelling Segregation Model.
 */
public final class SchellingsAgentFactory implements AgentFactory {
    private static final String VISIONRADIUS = "visionRadius";
    private static final String THRESHOLD = "threshold";
    private static BiPredicate<Agent, Agent> isAgentOfSameType = (a, b) -> {
        String typeA = a.getType();

        String typeB = b.getType();
        return typeA.equals(typeB);
    };

    private static Set<Agent> getNeighborgs(final State state, final Integer visionRadius, final Pos pos,
            final Agent agent) {
        return state.getAgentsByPosAndRadius(pos, visionRadius)
                .stream()
                .filter(a -> !a.equals(agent))
                .collect(Collectors.toSet());

    }

    private static double getRatio(final State state, final Integer visioRadius, final Pos pos, final Agent agent) {
        Set<Agent> neighBors = getNeighborgs(state, visioRadius, pos, agent);
        return neighBors.stream().filter(a -> isAgentOfSameType.test(a, agent))
                .count()
                / (double) neighBors.size();

    }

    private static boolean isThresholdSatisfied(final State state, final Pos pos, final Agent agent) {
        Integer visionRadius = agent.getParameters().getParameter(
                VISIONRADIUS, Integer.class)
                .orElseThrow(() -> new IllegalArgumentException("Agent " + agent + " has no visionRadius parameter"))
                .getValue();
        Double threshold = agent.getParameters().getParameter(
                THRESHOLD, Double.class)
                .orElseThrow(() -> new IllegalArgumentException("Agent " + agent + " has no threshold parameter"))
                .getValue();

        Set<Agent> neighBors = getNeighborgs(state, visionRadius, pos, agent);

        return neighBors.isEmpty() || getRatio(state, visionRadius, pos, agent) >= threshold;
    }

    private static PosImpl getNewRandomPosition(final State state, final Agent agent) {
        Random r = new Random();
        PosImpl newPos = new PosImpl(r.nextInt(state.getDimensions().getFirst()),
                r.nextInt(state.getDimensions().getSecond()));
        while (!state.isFree(newPos)) {
            newPos = new PosImpl(r.nextInt(state.getDimensions().getFirst()),
                    r.nextInt(state.getDimensions().getSecond()));
        }
        return newPos;
    }

    /**
     * Creates a new agent for the Schelling Segregation Model.
     * 
     * @param type         The type of the agent.
     * @param threshold    The threshold of the agent.
     * @param visionRadius The vision radius of the agent.
     * @return the agent.
     */
    public Agent getSchellingSegregationModelAgent(final String type, final Double threshold,
            final Integer visionRadius) {
        AgentBuilder b = new AgentBuilderImpl();

        b.addParameter(new ParameterImpl<Double>(THRESHOLD, threshold, true));
        b.addParameter(new ParameterImpl<Integer>(VISIONRADIUS, visionRadius, true));
        b.addStrategy((state, pos) -> {
            Agent agent = state.getAgentAt(pos).get();
            if (!isThresholdSatisfied(state, pos, agent)) {
                state.moveAgent(pos, getNewRandomPosition(state, agent));
            }
            return state;
        });

        Agent a = b.build();
        a.setType(type);
        return a;
    }

    @Override
    public Agent createAgent() {
        AgentBuilder b = new AgentBuilderImpl();

        b.addParameter(new ParameterImpl<Double>("threshold", Double.class, new ParameterDomainImpl<>(
                "Treshold di tolleranza dell'agente (0.0-1.0)", (Double d) -> d >= 0.0 && d <= 1.0), true));
        b.addParameter(new ParameterImpl<Integer>("visionRadius", Integer.class,
                new ParameterDomainImpl<>("Raggio di visione dell'agente (0 - n)", (Integer i) -> i > 0), true));

        b.addStrategy((state, pos) -> {
            Agent agent = state.getAgentAt(pos).get();
            if (!isThresholdSatisfied(state, pos, agent)) {
                state.moveAgent(pos, getNewRandomPosition(state, agent));
            }
            return state;
        });

        return b.build();
    }
}
