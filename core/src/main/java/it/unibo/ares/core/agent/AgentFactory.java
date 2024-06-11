package it.unibo.ares.core.agent;

import java.io.Serializable;

/**
 * Represents a factory for creating agents.
 */
public interface AgentFactory extends Serializable {
    /**
     * Creates an agent.
     * 
     * @return the agent created.
     */
    Agent createAgent();
}
