package it.unibo.ares.core.agent;

import it.unibo.ares.core.utils.parameters.ParameterDomainImpl;
import it.unibo.ares.core.utils.parameters.ParameterImpl;

/**
 * Represents a factory for creating Sugar agents.
 * 
 */
public final class SugarAgentFactory implements AgentFactory {

    private static final long serialVersionUID = 1L;
    /**
     * This class represents a SugarAgentFactory, which is responsible for creating
     * SugarAgents.
     */
    public static final String SUGAR = "S";

    private Agent createSugarAgent() {
        final AgentBuilder builder = new AgentBuilderImpl();
        builder.addParameter(new ParameterImpl<>(
                "maxSugar", Integer.class,
                new ParameterDomainImpl<>("zucchero massimo Producer", (Integer i) -> i > 0),
                true));

        builder.addParameter(new ParameterImpl<>(
                "sugarAmount", Integer.class,
                new ParameterDomainImpl<>("quanto zucchero iniziale Prioducer", (Integer i) -> i >= 0),
                true));
        builder.addParameter(new ParameterImpl<>(
                "growthRate", Integer.class,
                new ParameterDomainImpl<>("velocita crescita zuccero producer", (Integer i) -> i > 0),
                true));

        builder.addStrategy((state, pos) -> {
            final int sugarAmount = state.getAgentAt(pos)
                    .orElseThrow(() -> new IllegalStateException("No agents at that pos"))
                    .getParameters()
                    .getParameter("sugarAmount", Integer.class)
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Agent has no sugarAmount parameter"))
                    .getValue();

            final int growthRate = state.getAgentAt(pos)
                    .orElseThrow(() -> new IllegalStateException("No agents at that pos"))
                    .getParameters()
                    .getParameter("growthRate", Integer.class)
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Agent has no growthRate parameter"))
                    .getValue();

            final int maxSuger = state.getAgentAt(pos)
                    .orElseThrow(() -> new IllegalStateException("No agents at that pos"))
                    .getParameters()
                    .getParameter("maxSugar", Integer.class)
                    .orElseThrow(() -> new IllegalArgumentException(
                            "Agent has no growthRate parameter"))
                    .getValue();

            // Grow sugar
            state.getAgentAt(pos)
                    .ifPresent(agent -> agent.getParameters().setParameter("sugarAmount",
                            Math.min(sugarAmount + growthRate, maxSuger)));

            return state;
        });

        final var agent = builder.build();
        agent.setType(SUGAR);
        return agent;
    }

    @Override
    public Agent createAgent() {
        return createSugarAgent(); // Initialize with 0 sugar and growth rate, they will be set later
    }
}
