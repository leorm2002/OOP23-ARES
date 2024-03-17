package it.unibo.ares.core.agent;

import it.unibo.ares.core.utils.parameters.ParameterDomainImpl;
import it.unibo.ares.core.utils.parameters.ParameterImpl;

/**
 * A factory class for creating Tree agents.
 */
public class TreeAgentFactory implements AgentFactory {

        /**
         * Verify if a Tree agent can be burnt.
         * 
         * @param a Tree agent to test flammability
         * @return True if flammable, false either way.
         */
        public static Boolean isFlammable(final Agent a) {
                Double flammable = a.getParameters()
                                .getParameter("flammability", Double.class)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Agent " + a + " has no flammable parameter"))
                                .getValue();

                return flammable > 0;
        };

        /**
         * Builds the Tree Agent.
         * 
         * @return An instance of the Tree Agent (with default parameters).
         */
        @Override
        public Agent createAgent() {
                AgentBuilder b = new AgentBuilderImpl();

                b
                                .addParameter(new ParameterImpl<>("fuel", Double.class,
                                                new ParameterDomainImpl<>("Capacità di combustibile",
                                                                (Double d) -> d >= 0.0),
                                                true))
                                .addParameter(new ParameterImpl<>("flammability", Double.class,
                                                new ParameterDomainImpl<>("Velocità di combustione (0.0-1.0)",
                                                                (Double d) -> d >= 0.0 && d <= 1.0),
                                                true))
                                .addStrategy((state, pos) -> state)
                                .build();

                Agent a = b.build();
                a.setType("T");
                return a;
        }
}