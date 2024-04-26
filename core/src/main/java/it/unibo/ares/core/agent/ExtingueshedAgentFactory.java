package it.unibo.ares.core.agent;

import it.unibo.ares.core.utils.parameters.ParameterDomainImpl;
import it.unibo.ares.core.utils.parameters.ParameterImpl;

/**
 * A factory class for creating Extingueshed agents.
 */
public final class ExtingueshedAgentFactory implements AgentFactory {

    /**
     * Builds the Extingueshed Agent.
     * 
     * @return An instance of the Extingueshed Agent (with default parameters).
     */
    @Override
    public Agent createAgent() {
        final AgentBuilder b = new AgentBuilderImpl();

        b
                .addParameter(new ParameterImpl<>("flammability", Double.class,
                        new ParameterDomainImpl<>("Velocità di combustione (0.0-1.0)",
                                (Double d) -> d >= 0.0 && d <= 1.0),
                        true))
                .addStrategy((state, pos) -> state)
                .build();

        final Agent a = b.build();
        a.setType("E");
        a.setParameter("flammability", 0.0);
        return a;
    }
}
