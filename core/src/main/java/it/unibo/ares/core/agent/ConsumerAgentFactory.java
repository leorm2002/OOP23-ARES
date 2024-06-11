package it.unibo.ares.core.agent;

import java.util.Comparator;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

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

    private Agent createConsumerAgent() {
        final AgentBuilder builder = new AgentBuilderImpl();
        builder.addParameter(new ParameterImpl<>(
                "visionRadius", Integer.class,
                new ParameterDomainImpl<>("raggio di visione", (Integer i) -> i > 0),
                true));
        builder.addParameter(new ParameterImpl<>(
                "metabolismRate", Integer.class,
                new ParameterDomainImpl<>("velocita metaboilismo", (Integer i) -> i > 0),
                true));
        builder.addParameter(new ParameterImpl<>(
                "sugar", Integer.class,
                new ParameterDomainImpl<>("zucchero iniziale", (Integer i) -> i >= 0),
                true));
        builder.addParameter(new ParameterImpl<>(
                "maxSugar", Integer.class,
                new ParameterDomainImpl<>("max zucchero", (Integer i) -> i > 0),
                true));

        builder.addStrategy((state, pos) -> {
            final int visionRadius = state.getAgentAt(pos)
                    .orElseThrow(() -> new IllegalStateException("No agents at that pos"))
                    .getParameters()
                    .getParameter("visionRadius", Integer.class)
                    .orElseThrow(() -> new IllegalArgumentException("Agent has no visionRadius parameter"))
                    .getValue();

            final int metabolismRate = state.getAgentAt(pos)
                    .orElseThrow(() -> new IllegalStateException("No agents at that pos"))
                    .getParameters()
                    .getParameter("metabolismRate", Integer.class)
                    .orElseThrow(() -> new IllegalArgumentException("Agent has no metabolismRate parameter"))
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

            // Metabolize
            if (sugar - metabolismRate < 0) {
                state.removeAgent(pos, state.getAgentAt(pos).get());
                return state;
            }
            state.getAgentAt(pos)
                    .ifPresent(agent -> agent.getParameters().setParameter("sugar", sugar - metabolismRate));

            // Find sugar within vision radius
            Optional<Pos> sugarPos = getNeighboringPositions(state, pos, visionRadius).stream()
                    .filter(p -> state.getAgentAt(p).isPresent())
                    .filter(p -> SugarAgentFactory.SUGAR.equals(state.getAgentAt(p).get().getType()))
                    .sorted(Comparator.comparingDouble(pos1 -> Math
                            .sqrt(Math.pow(pos1.diff(pos).getX(), 2) + Math.pow(pos1.diff(pos).getY(), 2))))
                    .findFirst();

            if (sugarPos.isPresent()) {
                // Consume sugar
                final int sugarAmount = state.getAgentAt(sugarPos.get())
                        .orElseThrow(() -> new IllegalStateException("No agents at that pos"))
                        .getParameters()
                        .getParameter("sugarAmount", Integer.class)
                        .orElseThrow(() -> new IllegalArgumentException("Agent has no sugarAmount parameter"))
                        .getValue();

                final int maxSugarIntake = Math.min(maxSugar - sugar, sugarAmount);
                state.getAgentAt(pos)
                        .ifPresent(agent -> agent.getParameters().setParameter("sugar", sugar + maxSugarIntake));
                state.getAgentAt(sugarPos.get())
                        .ifPresent(agent -> agent.getParameters().setParameter("sugarAmount",
                                sugarAmount - maxSugarIntake));

                // Move towards sugar
                state.moveAgent(pos, sugarPos.get());
            } else {
                // Move randomly if no sugar is found
                final Pos newPos = getNeighboringPositions(state, pos, 1).stream()
                        .filter(state::isFree)
                        .findFirst()
                        .orElse(pos);
                state.moveAgent(pos, newPos);
            }

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