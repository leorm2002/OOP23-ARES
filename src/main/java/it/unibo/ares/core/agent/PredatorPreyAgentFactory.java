package it.unibo.ares.core.agent;

import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.Optional;

import it.unibo.ares.core.utils.parameters.ParameterImpl;
import it.unibo.ares.core.utils.pos.Pos;
import it.unibo.ares.core.utils.pos.PosImpl;
import it.unibo.ares.core.utils.state.State;

/**
 * A factory class for creating predator and prey agents for the Predator-Prey
 * Model.
 */
public final class PredatorPreyAgentFactory implements AgentFactory {
    private static final String AGENT_TYPE = "agentType";
    private static final String PREDATOR = "Predator";
    private static final String PREY = "Prey";
    private Random random;

    public PredatorPreyAgentFactory() {
        this.random = new Random();
    }

    private Set<Pos> getNeighboringPositions(final State state, final Pos position, final int visionRadius) {
        return state.getPosByPosAndRadius(position, visionRadius).stream()
                .filter(p -> !p.equals(position))
                .collect(Collectors.toSet());
    }

    private Pos findEscapeRoute(final State state, final Pos position, final Set<Pos> predators) {
        // Prey will try to move in the opposite direction of the average position of
        // nearby predators
        double avgX = predators.stream().mapToInt(Pos::getX).average().orElse(position.getX());
        double avgY = predators.stream().mapToInt(Pos::getY).average().orElse(position.getY());
        Pos avgPos = new PosImpl(avgX, avgY);
        Pos diff = position.diff(avgPos);

        int newX = position.getX() + diff.getX();
        int newY = position.getY() + diff.getY();

        // Keep new position within bounds
        newX = Math.max(0, Math.min(newX, state.getDimensions().getFirst() - 1));
        newY = Math.max(0, Math.min(newY, state.getDimensions().getSecond() - 1));

        return new PosImpl(newX, newY);
    }

    private Optional<Pos> findPrey(final State state, final Pos position, final int visionRadius) {
        Set<Pos> neighbors = getNeighboringPositions(state, position, visionRadius);
        return neighbors.stream().filter(p -> state.getAgentAt(p).isPresent())
                .filter(p -> PREY.equals(state.getAgentAt(p).get().getType())).findFirst();
    }

    @Override
    public Agent createAgent() {
        AgentBuilder builder = new AgentBuilderImpl();
        String type = random.nextBoolean() ? PREDATOR : PREY;
        int visionRadius = random.nextInt(5) + 1; // Vision radius between 1 and 5

        builder.addParameter(new ParameterImpl<>(AGENT_TYPE, type));
        builder.addParameter(new ParameterImpl<>("visionRadius", visionRadius));

        if (PREY.equals(type)) {
            builder.addStrategy((state, pos) -> {
                Set<Pos> predatorPositions = getNeighboringPositions(state, pos, visionRadius).stream()
                        .filter(p -> state.getAgentAt(pos).isPresent()
                                && PREDATOR.equals(state.getAgentAt(pos).get().getType()))
                        .collect(Collectors.toSet());

                if (!predatorPositions.isEmpty()) {
                    Pos escapeRoute = findEscapeRoute(state, pos, predatorPositions);
                    state.moveAgent(pos, escapeRoute);
                }
                return state;
            });
        } else if (PREDATOR.equals(type)) {
            builder.addStrategy((state, pos) -> {
                findPrey(state, pos, visionRadius).ifPresent(preyPosition -> {
                    state.removeAgent(preyPosition, state.getAgentAt(preyPosition).get());
                    state.moveAgent(pos, preyPosition);
                });
                return state;
            });
        }

        return builder.build();
    }
}