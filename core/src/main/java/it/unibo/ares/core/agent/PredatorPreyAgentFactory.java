package it.unibo.ares.core.agent;

import it.unibo.ares.core.utils.parameters.ParameterDomainImpl;
import it.unibo.ares.core.utils.parameters.ParameterImpl;
import it.unibo.ares.core.utils.pos.Pos;
import it.unibo.ares.core.utils.pos.PosImpl;
import it.unibo.ares.core.utils.state.State;

import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * A factory class for creating predator and prey agents for the Predator-Prey
 * Model.
 */
public final class PredatorPreyAgentFactory implements AgentFactory {
    private static final String PREDATOR = "Predator";
    private static final String PREY = "Prey";
    private static final Integer DEFAULT_MAX_RADIUS = 5;
    private Random random;

    /**
     * Create a new instance of the factory.
     */
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

    public Agent createPreyAgent() {
        AgentBuilder builder = new AgentBuilderImpl();

        builder.addParameter(new ParameterImpl<Integer>("visionRadiusPrey", Integer.class,
                new ParameterDomainImpl<>("Raggio di visione dell'agente preda (0 - n)", (Integer i) -> i > 0), true));

        builder.addStrategy((state, pos) -> {

            var visionRadius = state.getAgentAt(pos)
                    .orElseThrow(() -> new IllegalAccessError("No agents at that pos"))
                    .getParameters()
                    .getParameter("visionRadiusPrey", Integer.class)
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Agent has no visionRadius parameter"))
                    .getValue();

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

        var agent = builder.build();
        agent.setType(PREY);
        return agent;
    }

    public Agent createPredatorAgent() {
        AgentBuilder builder = new AgentBuilderImpl();

        builder.addParameter(new ParameterImpl<Integer>("visionRadiusPredator", Integer.class,
                new ParameterDomainImpl<>("Raggio di visione dell'agente predatore (0 - n)", (Integer i) -> i > 0),
                true));

        builder.addStrategy((state, pos) -> {
            var visionRadius = state.getAgentAt(pos)
                    .orElseThrow(() -> new IllegalAccessError("No agents at that pos"))
                    .getParameters()
                    .getParameter("visionRadiusPredator", Integer.class)
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Agent has no visionRadiusPredator parameter"))
                    .getValue();

            findPrey(state, pos, visionRadius).ifPresent(preyPosition -> {
                state.removeAgent(preyPosition, state.getAgentAt(preyPosition).get());
                state.moveAgent(pos, preyPosition);
            });
            return state;
        });

        var agent = builder.build();
        agent.setType(PREDATOR);
        return agent;
    }

    @Override
    public Agent createAgent() {
        AgentBuilder builder = new AgentBuilderImpl();
        String type = random.nextBoolean() ? PREDATOR : PREY;
        int visionRadius = random.nextInt(DEFAULT_MAX_RADIUS) + 1; // Vision radius between 1 and 5

        builder.addParameter(new ParameterImpl<>("visionRadius", visionRadius, true));

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

        var agent = builder.build();
        agent.setType(type);
        return agent;
    }
}
