package it.unibo.ares.core.agent;

import it.unibo.ares.core.utils.parameters.ParameterDomainImpl;
import it.unibo.ares.core.utils.parameters.ParameterImpl;

/**
 * A factory class for creating Tree agents.
 */
public class TreeAgentFactory implements AgentFactory {
        private static final String FUEL = "fuel";
        private static final String FLAMM = "flammability";

        /**
         * Verify if a Tree agent can be burnt.
         * 
         * @param a Tree agent to test flammability
         * @return True if flammable, false either way.
         */
        public static Boolean isFlammable(final Agent a) {
                final Double flammable = a.getParameters()
                                .getParameter(FLAMM, Double.class)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Agent " + a + " has no flammable parameter"))
                                .getValue();

                return flammable > 0;
        }

        /**
         * Builds the Tree Agent.
         * 
         * @return An instance of the Tree Agent (with default parameters).
         */
        @Override
        public Agent createAgent() {
                final AgentBuilder b = new AgentBuilderImpl();

                b
                                .addParameter(new ParameterImpl<>(FUEL, Double.class,
                                                new ParameterDomainImpl<>("Capacità di combustibile",
                                                                (Double d) -> d >= 0.0),
                                                true))
                                .addParameter(new ParameterImpl<>(FLAMM, Double.class,
                                                new ParameterDomainImpl<>("Velocità di combustione (0.0-1.0)",
                                                                (Double d) -> d >= 0.0 && d <= 1.0),
                                                true))
                                .addStrategy((state, pos) -> state)
                                .build();

                final Agent a = b.build();
                a.setType("T");
                return a;
        }
}
