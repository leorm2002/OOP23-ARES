package it.unibo.ares.core.agent;

import java.util.Comparator;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import it.unibo.ares.core.utils.parameters.ParameterDomainImpl;
import it.unibo.ares.core.utils.parameters.ParameterImpl;
import it.unibo.ares.core.utils.pos.Pos;
import it.unibo.ares.core.utils.state.State;

/**
 * A factory class for creating predator agents for the Predator-Prey Model.
 */
public final class PredatorAgentFactory implements AgentFactory {

    private static final long serialVersionUID = 1L;

    /**
     * This class represents a Predator Agent Factory.
     */
    public static final String PREDATOR = "H";

    // Parameter keys and descriptions
    private static final String VISION_RADIUS_PREDATOR = "visionRadiusPredator";
    private static final String VISION_RADIUS_DESCRIPTION = "Raggio di visione dell'agente predatore (0 - n)";

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

    private Integer getDistanceBetweeenPos(final Pos p1, final Pos p2) {
        final Pos diff = p1.diff(p2);
        return Math.abs(diff.getX()) + Math.abs(diff.getY());
    }

    /**
     * Finds the position of a prey within the given vision radius.
     *
     * @param state        the current state
     * @param position     the position of the predator
     * @param visionRadius the vision radius of the predator
     * @return an optional position of the prey if found
     */
    private Optional<Pos> findPrey(final State state, final Pos position, final int visionRadius) {
        return getNeighboringPositions(state, position, visionRadius).stream()
                .filter(p -> state.getAgentAt(p).isPresent())
                .filter(p -> PreyAgentFactory.PREY.equals(state.getAgentAt(p).get().getType()))
                .sorted(Comparator.comparingDouble(pos -> getDistanceBetweeenPos(pos, position)))
                .findFirst();
    }

    /**
     * Calculates the next position for the predator to move one step towards the
     * prey.
     *
     * @param state      the current state
     * @param currentPos the current position of the predator
     * @param preyPos    the position of the prey
     * @return the next position towards the prey
     */
    private Pos getNextPositionTowardsPrey(final State state, final Pos currentPos, final Pos preyPos) {
        return getNeighboringPositions(state, currentPos, 1).stream()
                .filter(state::isFree)
                .sorted(Comparator.comparingDouble(pos -> getDistanceBetweeenPos(pos, preyPos)))
                .findFirst()
                .orElse(currentPos); // If no free position is found, stay in the current position
    }

    /**
     * Creates a predator agent with predefined parameters and strategies.
     *
     * @return the created predator agent
     */
    private Agent createPredatorAgent() {
        final AgentBuilder builder = new AgentBuilderImpl();

        builder.addParameter(new ParameterImpl<>(
                VISION_RADIUS_PREDATOR, Integer.class,
                new ParameterDomainImpl<>(VISION_RADIUS_DESCRIPTION, (Integer i) -> i > 0),
                true));

        builder.addStrategy((state, pos) -> {
            final int visionRadius = state.getAgentAt(pos)
                    .orElseThrow(() -> new IllegalStateException("No agents at that pos"))
                    .getParameters()
                    .getParameter(VISION_RADIUS_PREDATOR, Integer.class)
                    .orElseThrow(() -> new IllegalArgumentException("Agent has no visionRadiusPredator parameter"))
                    .getValue();

            findPrey(state, pos, visionRadius).ifPresentOrElse(preyPosition -> {
                if (state.getPosByPosAndRadius(preyPosition, 1).contains(pos)) {
                    state.removeAgent(preyPosition, state.getAgentAt(preyPosition).get());
                    state.moveAgent(pos, preyPosition);
                    return;
                }
                state.moveAgent(pos, getNextPositionTowardsPrey(state, pos, preyPosition));

            }, () -> {
                final Pos newPos = getNeighboringPositions(state, pos, 1).stream()
                        .filter(state::isFree)
                        .findFirst()
                        .orElse(pos); // If no free positions are found, stay in place
                state.moveAgent(pos, newPos);
            });
            return state;
        });

        final Agent agent = builder.build();
        agent.setType(PREDATOR);
        return agent;
    }

    @Override
    public Agent createAgent() {
        return createPredatorAgent();
    }
}
