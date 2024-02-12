package it.unibo.ares.core.agent.factories;

import java.util.Optional;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import it.unibo.ares.core.agent.Agent;
import it.unibo.ares.core.agent.AgentBuilder;
import it.unibo.ares.core.agent.AgentBuilderImpl;
import it.unibo.ares.core.utils.parameters.ParameterImpl;
import it.unibo.ares.core.utils.pos.Pos;
import it.unibo.ares.core.utils.pos.PosImpl;
import it.unibo.ares.core.utils.state.State;

/**
 * A factory class for creating agents for the Schelling Segregation Model.
 */
public final class SchellingsAgentFactory {
    //private boolean isAgentOfSameType(final Agent a, final Agent b) {
    private BiPredicate<Agent, Agent> isAgentOfSameType = (a, b) -> {
        Integer typeA = a.getParameters().getParameter("type", Integer.class)
            .orElseThrow(() -> new IllegalArgumentException("Agent " + a + " has no type parameter"))
            .getValue();
        Integer typeB = b.getParameters().getParameter("type", Integer.class)
            .orElseThrow(() -> new IllegalArgumentException("Agent " + b + " has no type parameter"))
            .getValue();
        return typeA.equals(typeB);
    };

    private boolean isThresholdSatisfied(final State state, final Pos pos, final Agent agent) {
        Integer visionRadius = agent.getParameters().getParameter("visionRadius", Integer.class)
            .orElseThrow(() -> new IllegalArgumentException("Agent " + agent + " has no visionRadius parameter"))
            .getValue();
        Double threshold = agent.getParameters().getParameter("threshold", Double.class)
            .orElseThrow(() -> new IllegalArgumentException("Agent " + agent + " has no threshold parameter"))
            .getValue();

        Set<Agent> neighBors =
            state.getAgentsByPosAndRadius(pos, visionRadius)
            .stream()
            .filter(a -> !a.equals(agent))
            .collect(Collectors.toSet());
        if (neighBors.size() == 0) {
            return true;
        }
        Double ratio = (double) neighBors.stream().filter(a -> isAgentOfSameType.test(a, agent)).count() / neighBors.size();

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

    public Agent getSchellingSegregationModelAgent(final Integer type, final Double threshold, final Integer visionRadius) {
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
