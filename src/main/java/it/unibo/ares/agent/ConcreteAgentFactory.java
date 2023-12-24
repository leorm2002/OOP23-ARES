package it.unibo.ares.agent;

import java.util.Optional;
import java.util.Set;
import java.util.stream.IntStream;
import it.unibo.ares.utils.parameters.ParameterImpl;
import it.unibo.ares.utils.pos.Pos;
import it.unibo.ares.utils.pos.PosImpl;
import it.unibo.ares.utils.state.State;

/**
 * A factory class for creating agents for the Schelling Segregation Model.
 */
public final class ConcreteAgentFactory {

    /**
     * Istantiate a factory for creating concrete agents.
     */
    ConcreteAgentFactory() {
    }

    private boolean isAgentOfSameType(final Agent a, final Agent b) {
            Integer type1 = a.getParameters().getParameter("type", Integer.class)
                .orElseThrow(() -> new IllegalArgumentException("Agent " + a + " has no type parameter"))
                .getValue();
            Integer type2 = b.getParameters().getParameter("type", Integer.class)
                .orElseThrow(() -> new IllegalArgumentException("Agent " + b + " has no type parameter"))
                .getValue();
            return type1.equals(type2);
    }

    private boolean isThresholdSatisfied(final State state, final Pos pos, final Agent agent) {
        Integer visionRadius = agent.getParameters().getParameter("visionRadius", Integer.class)
            .orElseThrow(() -> new IllegalArgumentException("Agent " + agent + " has no visionRadius parameter"))
            .getValue();
        Double threshold = agent.getParameters().getParameter("threshold", Double.class)
            .orElseThrow(() -> new IllegalArgumentException("Agent " + agent + " has no threshold parameter"))
            .getValue();

        Set<Agent> neighBors = state.getAgentsByPosAndRadius(pos, visionRadius);
        if (neighBors.size() == 0) {
            return true;
        }
        Double ratio = (double) neighBors.stream().filter(a -> isAgentOfSameType(a, agent)).count() / neighBors.size();

        return ratio >= threshold;
    }

    private Optional<PosImpl> getFreePositionIfAvailable(final State state, final Agent agent) {
        return IntStream.range(0, state.getDimensions().getFirst())
        .boxed()
        .flatMap(x -> IntStream.range(0, state.getDimensions().getSecond())
                .mapToObj(y -> new PosImpl(x, y)))
        .filter(p -> state.getAgentAt(p).isEmpty())
        .filter(p -> isThresholdSatisfied(state, p, agent))
        .findAny();
    }

    Agent getSchellingSegregationModelAgent(final Integer type, final Double threshold, final Integer visionRadius) {
        AgentBuilder b  =  new AgentBuilderImpl();

        b.addParameter(new ParameterImpl<Integer>("type", type));
        b.addParameter(new ParameterImpl<Double>("threshold", threshold));
        b.addParameter(new ParameterImpl<Integer>("visionRadius", visionRadius));

        b.addStrategy((state, pos) -> {
            Agent agent = state.getAgentAt(pos).get();
            if (!isThresholdSatisfied(state, pos, agent)) {
                Optional<PosImpl> newPos = getFreePositionIfAvailable(state, agent);
                newPos.ifPresent(p -> state.moveAgent(pos, newPos.get()));
            }
            return state;
        });

        return b.build();
    }
}

