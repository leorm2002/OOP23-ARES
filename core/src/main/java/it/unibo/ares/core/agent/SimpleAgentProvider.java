package it.unibo.ares.core.agent;

import it.unibo.ares.core.utils.pos.Pos;
import it.unibo.ares.core.utils.pos.PosImpl;

/**
 * Provides a mock agent for testing purposes.
 */
public final class SimpleAgentProvider {
    /**
     * Returns a mock agent that moves the agent in the top-right direction.
     * 
     * @return a mock agent that moves the agent in the top-right direction.
     */
    public static Agent getMockAgent() {
        AgentBuilder builder = new AgentBuilderImpl();
        builder.addStrategy((state, pos) -> {
            Pos newPos = new PosImpl((pos.getX() + 1)
                    % state.getDimensions().getFirst(), (pos.getY() + 1) % state.getDimensions().getSecond());
            state.moveAgent(pos, newPos);
            return state;
        });
        Agent agent = builder.build();
        agent.setType("A");
        return agent;
    }

    private SimpleAgentProvider() {
        throw new IllegalStateException("Utility class");
    }
}
