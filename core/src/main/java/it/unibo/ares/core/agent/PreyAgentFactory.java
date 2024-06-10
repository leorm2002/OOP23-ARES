package it.unibo.ares.core.agent;

import it.unibo.ares.core.utils.parameters.ParameterDomainImpl;
import it.unibo.ares.core.utils.parameters.ParameterImpl;
import it.unibo.ares.core.utils.pos.Pos;
import it.unibo.ares.core.utils.pos.PosImpl;
import it.unibo.ares.core.utils.state.State;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * A factory class for creating prey agents for the Predator-Prey Model.
 */
public final class PreyAgentFactory implements AgentFactory {

    private static final long serialVersionUID = 1L;

    /**
     * This class represents a Prey Agent Factory.
     */
    public static final String PREY = "P";

    // Parameter keys and descriptions
    private static final String VISION_RADIUS_PREY = "visionRadiusPrey";
    private static final String VISION_RADIUS_DESCRIPTION = "Raggio di visione dell'agente preda (0 - n)";

    /**
     * Retrieves neighboring positions within a given radius.
     *
     * @param state        the current state
     * @param position     the position to find neighbors for
     * @param visionRadius the radius to search within
     * @return a set of neighboring positions
     */
    private Set<Pos> getNeighboringPositions(final State state, final Pos position, final int visionRadius) {
        return state.getPosByPosAndRadius(position, visionRadius).stream()
                .filter(p -> !p.equals(position))
                .collect(Collectors.toSet());
    }

    /**
     * Determines the best escape route for the prey based on predator positions.
     *
     * @param state     the current state
     * @param position  the position of the prey
     * @param predators the positions of nearby predators
     * @return the position to which the prey should move
     */
    private Pos findEscapeRoute(final State state, final Pos position, final Set<Pos> predators) {
        final double avgX = predators.stream().mapToInt(Pos::getX).average().orElse(position.getX());
        final double avgY = predators.stream().mapToInt(Pos::getY).average().orElse(position.getY());
        final Pos avgPos = new PosImpl(avgX, avgY);
        final Pos diff = position.diff(avgPos);

        int moveX = (int) Math.signum(diff.getX());
        int moveY = (int) Math.signum(diff.getY());

        int newX = position.getX() + moveX;
        int newY = position.getY() + moveY;

        // Keep new position within bounds
        newX = Math.max(0, Math.min(newX, state.getDimensions().getFirst() - 1));
        newY = Math.max(0, Math.min(newY, state.getDimensions().getSecond() - 1));

        Pos pos = new PosImpl(newX, newY);
        if (state.isFree(pos)) {
            return pos;
        }

        return getNeighboringPositions(state, pos, 1).stream()
                .filter(state::isFree)
                .findFirst()
                .orElse(position); // If no free positions are found, stay in place

    }

    /**
     * Creates a prey agent with predefined parameters and strategies.
     *
     * @return the created prey agent
     */
    private Agent createPreyAgent() {
        final AgentBuilder builder = new AgentBuilderImpl();

        builder.addParameter(new ParameterImpl<>(
                VISION_RADIUS_PREY, Integer.class,
                new ParameterDomainImpl<>(VISION_RADIUS_DESCRIPTION, (Integer i) -> i > 0),
                true));

        builder.addStrategy((state, pos) -> {
            final int visionRadius = state.getAgentAt(pos)
                    .orElseThrow(() -> new IllegalStateException("No agents at that pos"))
                    .getParameters()
                    .getParameter(VISION_RADIUS_PREY, Integer.class)
                    .orElseThrow(() -> new IllegalArgumentException("Agent has no visionRadiusPrey parameter"))
                    .getValue();

            final Set<Pos> predatorPositions = getNeighboringPositions(state, pos, visionRadius).stream()
                    .filter(state::isOccupied)
                    .filter(p -> PredatorAgentFactory.PREDATOR.equals(state.getAgentAt(p).get().getType()))
                    .collect(Collectors.toSet());

            if (!predatorPositions.isEmpty()) {
                final Pos escapeRoute = findEscapeRoute(state, pos, predatorPositions);
                state.moveAgent(pos, escapeRoute);
            } else {
                final Pos newPos = getNeighboringPositions(state, pos, 1).stream()
                        .filter(state::isFree)
                        .findFirst()
                        .orElse(pos); // If no free positions are found, stay in place
                state.moveAgent(pos, newPos);
            }

            return state;
        });

        final var agent = builder.build();
        agent.setType(PREY);
        return agent;
    }

    @Override
    public Agent createAgent() {
        return createPreyAgent();
    }
}
