
package it.unibo.ares.core.agent;

import it.unibo.ares.core.utils.parameters.ParameterDomainImpl;
import it.unibo.ares.core.utils.parameters.ParameterImpl;
import it.unibo.ares.core.utils.pos.Pos;
import it.unibo.ares.core.utils.pos.PosImpl;
import it.unibo.ares.core.utils.state.State;

import java.util.Set;
import java.util.stream.Collectors;

/**
 * A factory class for creating predator and prey agents for the Predator-Prey
 * Model.
 */
public final class PreyAgentFactory implements AgentFactory {
    /**
     * Prey key.
     */
    public static final String PREY = "P";

    private Set<Pos> getNeighboringPositions(final State state, final Pos position, final int visionRadius) {
        return state.getPosByPosAndRadius(position, visionRadius).stream()
                .filter(p -> !p.equals(position))
                .collect(Collectors.toSet());
    }

    private Pos findEscapeRoute(final State state, final Pos position, final Set<Pos> predators) {
        // Prey will try to move in the opposite direction of the average position of
        // nearby predators
        final double avgX = predators.stream().mapToInt(Pos::getX).average().orElse(position.getX());
        final double avgY = predators.stream().mapToInt(Pos::getY).average().orElse(position.getY());
        final Pos avgPos = new PosImpl(avgX, avgY);
        final Pos diff = position.diff(avgPos);

        int newX = position.getX() + diff.getX();
        int newY = position.getY() + diff.getY();

        // Keep new position within bounds
        newX = Math.max(0, Math.min(newX, state.getDimensions().getFirst() - 1));
        newY = Math.max(0, Math.min(newY, state.getDimensions().getSecond() - 1));

        return new PosImpl(newX, newY);
    }

    private Agent createPreyAgent() {
        final AgentBuilder builder = new AgentBuilderImpl();

        builder.addParameter(new ParameterImpl<Integer>("visionRadiusPrey", Integer.class,
                new ParameterDomainImpl<>("Raggio di visione dell'agente preda (0 - n)", (Integer i) -> i > 0), true));

        builder.addStrategy((state, pos) -> {

            final var visionRadius = state.getAgentAt(pos)
                    .orElseThrow(() -> new IllegalAccessError("No agents at that pos"))
                    .getParameters()
                    .getParameter("visionRadiusPrey", Integer.class)
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Agent has no visionRadius parameter"))
                    .getValue();

            final Set<Pos> predatorPositions = getNeighboringPositions(state, pos, visionRadius).stream()
                    .filter(p -> state.getAgentAt(pos).isPresent()
                            && PredatorAgentFactory.PREDATOR.equals(state.getAgentAt(pos).get().getType()))
                    .collect(Collectors.toSet());

            if (!predatorPositions.isEmpty()) {
                final Pos escapeRoute = findEscapeRoute(state, pos, predatorPositions);
                state.moveAgent(pos, escapeRoute);
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
