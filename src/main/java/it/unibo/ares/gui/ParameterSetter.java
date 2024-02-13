package it.unibo.ares.gui;

import java.util.HashMap;

import it.unibo.ares.core.controller.SimulationInitializer;

/**
 * ParameterSetter is a class that provides methods to set parameters for a
 * simulation.
 */
public class ParameterSetter {

    /**
     * The setModelParameters method sets the parameters for the model in the
     * simulation initializer.
     *
     * @param initializer the SimulationInitializer instance to set the parameters
     *                    on
     * @param parameters  a HashMap containing the parameters to set on the
     *                    initializer
     */
    public void setModelParameters(final SimulationInitializer initializer, final HashMap<String, Object> parameters) {
        parameters.forEach((key, value) -> initializer.setModelParameter(key, value));
    }

    /**
     * The setAgentParameters method sets the parameters for a specific agent in the
     * simulation initializer.
     *
     * @param initializer the SimulationInitializer instance to set the parameters
     *                    on
     * @param agentId     the ID of the agent to set the parameters for
     * @param parameters  a HashMap containing the parameters to set on the
     *                    initializer
     */
    public void setAgentParameters(final SimulationInitializer initializer, final String agentId,
            final HashMap<String, Object> parameters) {
        parameters.forEach((key, value) -> initializer.setAgentParameterSimplified(agentId, key, value));
    }
}
