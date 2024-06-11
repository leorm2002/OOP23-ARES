package it.unibo.ares.core.agent;

import java.util.Comparator;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import it.unibo.ares.core.utils.Pair;
import it.unibo.ares.core.utils.parameters.ParameterDomainImpl;
import it.unibo.ares.core.utils.parameters.ParameterImpl;
import it.unibo.ares.core.utils.pos.Pos;
import it.unibo.ares.core.utils.state.State;

public final class ConsumerAgentFactory implements AgentFactory {

    private static final long serialVersionUID = 1L;
    public static final String CONSUMER = "C";

    private Set<Pos> getNeighboringPositions(final State state, final Pos position, final int visionRadius) {
        return state.getPosByPosAndRadius(position, visionRadius).stream()
                .filter(p -> !p.equals(position))
                .collect(Collectors.toSet());

    }

    private Integer getCompetionForSugar(final State state, final Pos sugarPos, final int visionRadius,
            final int distance) {
        final Integer relativeVisionRadius = visionRadius - distance;
        return (int) state.getPosByPosAndRadius(sugarPos, relativeVisionRadius).stream()
                .filter(p -> state.getAgentAt(p).isPresent())
                .filter(p -> CONSUMER.equals(state.getAgentAt(p).get().getType()))
                .filter(p -> getDistanceBetweeenPos(p, sugarPos) <= distance)
                .count();

    }

    private Integer getDistanceBetweeenPos(final Pos p1, final Pos p2) {
        Pos diff = p1.diff(p2);
        return Math.abs(diff.getX()) + Math.abs(diff.getY());
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
    private Pos getNextPositionTowardsPos(final State state, final Pos currentPos, final Pos sugarPos) {
        return getNeighboringPositions(state, currentPos, 1).stream()
                .filter(state::isFree)
                .sorted(Comparator.comparingDouble(pos -> getDistanceBetweeenPos(pos, sugarPos)))
                .findFirst()
                .orElse(currentPos);
    }

    private Set<Pair<Pos, Integer>> getSugarPositions(final State state, final Pos position,
            final int visionRadius, final long maxSteps) {
        return state.getPosByPosAndRadius(position, visionRadius).stream()
                .filter(p -> state.getAgentAt(p).isPresent())
                .filter(p -> SugarAgentFactory.SUGAR.equals(state.getAgentAt(p).get().getType()))
                .filter(p -> getDistanceBetweeenPos(p, position) <= maxSteps) // If I can't reach it in
                                                                              // time, don't
                                                                              // consider it
                .map(s -> new Pair<>(s,
                        getCompetionForSugar(state, s,
                                visionRadius,
                                getDistanceBetweeenPos(s, position))))
                .collect(Collectors.toSet());
    }

    private Agent createSonConsumerAgent(Agent father, int initialSugar) {

        final AgentBuilder builder = new AgentBuilderImpl();
        builder.addParameter(new ParameterImpl<Integer>("visionRadius", father.getParameters()
                .getParameter("visionRadius", Integer.class).get().getValue(), false));

        builder.addParameter(new ParameterImpl<Integer>("metabolismRate", father.getParameters()
                .getParameter("metabolismRate", Integer.class).get().getValue(), false));

        builder.addParameter(new ParameterImpl<Integer>("sugar", initialSugar, false));

        builder.addParameter(new ParameterImpl<Integer>("maxSugar", father.getParameters()
                .getParameter("maxSugar", Integer.class).get().getValue(), false));

        builder.addStrategy((state, pos) -> {
            final int visionRadius = state.getAgentAt(pos)
                    .orElseThrow(() -> new IllegalStateException("No agents at that pos"))
                    .getParameters()
                    .getParameter("visionRadius", Integer.class)
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Agent has no visionRadius parameter"))
                    .getValue();

            final int metabolismRate = state.getAgentAt(pos)
                    .orElseThrow(() -> new IllegalStateException("No agents at that pos"))
                    .getParameters()
                    .getParameter("metabolismRate", Integer.class)
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Agent has no metabolismRate parameter"))
                    .getValue();

            final int sugar = state.getAgentAt(pos)
                    .orElseThrow(() -> new IllegalStateException("No agents at that pos"))
                    .getParameters()
                    .getParameter("sugar", Integer.class)
                    .orElseThrow(() -> new IllegalArgumentException("Agent has no sugar parameter"))
                    .getValue();

            final int maxSugar = state.getAgentAt(pos)
                    .orElseThrow(() -> new IllegalStateException("No agents at that pos"))
                    .getParameters()
                    .getParameter("maxSugar", Integer.class)
                    .orElseThrow(() -> new IllegalArgumentException("Agent has no sugar parameter"))
                    .getValue();

            if (sugar < metabolismRate) {
                state.removeAgent(pos, state.getAgentAt(pos).get());
                return state;
            }

            state.getAgentAt(pos)
                    .ifPresent(agent -> agent.getParameters().setParameter("sugar",
                            sugar - metabolismRate));

            getSugarPositions(state, pos, visionRadius,
                    sugar / metabolismRate)
                    .stream()
                    .sorted(Comparator.comparingDouble(pp -> {
                        if (pp.getSecond() == 0) {
                            return Double.NEGATIVE_INFINITY;
                        }
                        return -(state.getAgentAt(pp.getFirst()).get()
                                .getParameters()
                                .getParameter("sugarAmount", Integer.class)
                                .orElseThrow(() -> new IllegalArgumentException(
                                        "Agent has no sugarAmount parameter"))
                                .getValue() / pp.getSecond());
                    }))
                    .map(Pair::getFirst)
                    .findFirst()
                    .ifPresent(sugarPos -> {
                        if (getDistanceBetweeenPos(sugarPos, pos) == 1) {
                            final int sugarAmount = state.getAgentAt(sugarPos)
                                    .orElseThrow(() -> new IllegalStateException(
                                            "No agents at that pos"))
                                    .getParameters()
                                    .getParameter("sugarAmount", Integer.class)
                                    .orElseThrow(() -> new IllegalArgumentException(
                                            "Agent has no sugarAmount parameter"))
                                    .getValue();

                            final int maxSugarIntake = Math.min(maxSugar - sugar, sugarAmount);
                            state.getAgentAt(pos)
                                    .ifPresent(agent -> agent.getParameters().setParameter(
                                            "sugar",
                                            sugar + maxSugarIntake));

                            state.getAgentAt(sugarPos)
                                    .ifPresent(agent -> agent.getParameters().setParameter(
                                            "sugarAmount",
                                            sugarAmount - maxSugarIntake));

                            if (sugar >= maxSugar * 0.75) {
                                getNeighboringPositions(state, pos, 1)
                                        .stream()
                                        .filter(state::isOccupied)
                                        .filter(p -> CONSUMER.equals(state.getAgentAt(p).get().getType()))
                                        .filter(p -> state.getAgentAt(p).get().getParameters()
                                                .getParameter("sugar", Integer.class)
                                                .get().getValue() >= maxSugar * 0.75)
                                        .findFirst()
                                        .ifPresent(p -> {
                                            getNeighboringPositions(state, pos, 1)
                                                    .stream()
                                                    .filter(state::isFree)
                                                    .findFirst()
                                                    .ifPresent(pp -> state.addAgent(pp,
                                                            createSonConsumerAgent(state.getAgentAt(pos).get(), 3)));
                                        });
                            }
                        } else {

                            state.moveAgent(pos, getNextPositionTowardsPos(state, pos, sugarPos));
                        }

                    });

            return state;
        });

        final var agent = builder.build();
        agent.setType(CONSUMER);
        return agent;

    }

    private Agent createConsumerAgent() {
        final AgentBuilder builder = new AgentBuilderImpl();
        builder.addParameter(new ParameterImpl<>(
                "visionRadius", Integer.class,
                new ParameterDomainImpl<>("raggio di visione consumer", (Integer i) -> i > 0),
                true));
        builder.addParameter(new ParameterImpl<>(
                "metabolismRate", Integer.class,
                new ParameterDomainImpl<>("velocita metaboilismo consumer", (Integer i) -> i > 0),
                true));
        builder.addParameter(new ParameterImpl<>(
                "sugar", Integer.class,
                new ParameterDomainImpl<>("zucchero iniziale consumer", (Integer i) -> i >= 0),
                true));
        builder.addParameter(new ParameterImpl<>(
                "maxSugar", Integer.class,
                new ParameterDomainImpl<>("max zucchero consumer", (Integer i) -> i > 0),
                true));

        builder.addStrategy((state, pos) -> {
            final int visionRadius = state.getAgentAt(pos)
                    .orElseThrow(() -> new IllegalStateException("No agents at that pos"))
                    .getParameters()
                    .getParameter("visionRadius", Integer.class)
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Agent has no visionRadius parameter"))
                    .getValue();

            final int metabolismRate = state.getAgentAt(pos)
                    .orElseThrow(() -> new IllegalStateException("No agents at that pos"))
                    .getParameters()
                    .getParameter("metabolismRate", Integer.class)
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Agent has no metabolismRate parameter"))
                    .getValue();

            final int sugar = state.getAgentAt(pos)
                    .orElseThrow(() -> new IllegalStateException("No agents at that pos"))
                    .getParameters()
                    .getParameter("sugar", Integer.class)
                    .orElseThrow(() -> new IllegalArgumentException("Agent has no sugar parameter"))
                    .getValue();

            final int maxSugar = state.getAgentAt(pos)
                    .orElseThrow(() -> new IllegalStateException("No agents at that pos"))
                    .getParameters()
                    .getParameter("maxSugar", Integer.class)
                    .orElseThrow(() -> new IllegalArgumentException("Agent has no sugar parameter"))
                    .getValue();

            if (sugar < metabolismRate) {
                state.removeAgent(pos, state.getAgentAt(pos).get());
                return state;
            }

            state.getAgentAt(pos)
                    .ifPresent(agent -> agent.getParameters().setParameter("sugar",
                            sugar - metabolismRate));

            getSugarPositions(state, pos, visionRadius,
                    sugar / metabolismRate)
                    .stream()
                    .sorted(Comparator.comparingDouble(pp -> {
                        if (pp.getSecond() == 0) {
                            return Double.NEGATIVE_INFINITY;
                        }
                        return -(state.getAgentAt(pp.getFirst()).get()
                                .getParameters()
                                .getParameter("sugarAmount", Integer.class)
                                .orElseThrow(() -> new IllegalArgumentException(
                                        "Agent has no sugarAmount parameter"))
                                .getValue() / pp.getSecond());
                    }))
                    .map(Pair::getFirst)
                    .findFirst()
                    .ifPresent(sugarPos -> {
                        if (getDistanceBetweeenPos(sugarPos, pos) == 1) {
                            final int sugarAmount = state.getAgentAt(sugarPos)
                                    .orElseThrow(() -> new IllegalStateException(
                                            "No agents at that pos"))
                                    .getParameters()
                                    .getParameter("sugarAmount", Integer.class)
                                    .orElseThrow(() -> new IllegalArgumentException(
                                            "Agent has no sugarAmount parameter"))
                                    .getValue();

                            final int maxSugarIntake = Math.min(maxSugar - sugar, sugarAmount);
                            state.getAgentAt(pos)
                                    .ifPresent(agent -> agent.getParameters().setParameter(
                                            "sugar",
                                            sugar + maxSugarIntake));

                            state.getAgentAt(sugarPos)
                                    .ifPresent(agent -> agent.getParameters().setParameter(
                                            "sugarAmount",
                                            sugarAmount - maxSugarIntake));

                            if (sugar >= maxSugar * 0.75) {
                                getNeighboringPositions(state, pos, 1)
                                        .stream()
                                        .filter(state::isOccupied)
                                        .filter(p -> CONSUMER.equals(state.getAgentAt(p).get().getType()))
                                        .filter(p -> state.getAgentAt(p).get().getParameters()
                                                .getParameter("sugar", Integer.class)
                                                .get().getValue() >= maxSugar * 0.75)
                                        .findFirst()
                                        .ifPresent(p -> {
                                            getNeighboringPositions(state, pos, 1)
                                                    .stream()
                                                    .filter(state::isFree)
                                                    .findFirst()
                                                    .ifPresent(pp -> state.addAgent(pp,
                                                            createSonConsumerAgent(state.getAgentAt(pos).get(), 3)));
                                        });
                            }
                        } else {

                            state.moveAgent(pos, getNextPositionTowardsPos(state, pos, sugarPos));
                        }

                    });

            return state;
        });

        final var agent = builder.build();
        agent.setType(CONSUMER);
        return agent;
    }

    @Override
    public Agent createAgent() {
        return createConsumerAgent();
    }
}