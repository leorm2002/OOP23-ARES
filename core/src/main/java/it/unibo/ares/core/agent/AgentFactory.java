package it.unibo.ares.core.agent;

/**
 * Represents a factory for creating agents.
 */
public interface AgentFactory {
    /**
     * Creates an agent.
     * 
     * @return the agent created.
     */
    Agent createAgent();
}
