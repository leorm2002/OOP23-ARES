package it.unibo.ares.core.agent;

import it.unibo.ares.core.utils.pos.Pos;
import it.unibo.ares.core.utils.pos.PosImpl;

/**
 * A factory class for creating a simple agent for the Simple Model.
 */
public final class SimpleAgentFactory implements AgentFactory {
    /**
     * Returns a mock agent that moves the agent in the top-right direction.
     * 
     * @return a mock agent that moves the agent in the top-right direction.
     */
    @Override
    public Agent createAgent() {
        final AgentBuilder builder = new AgentBuilderImpl();
        builder.addStrategy((state, pos) -> {
            final Pos newPos = new PosImpl((pos.getX() + 1)
                    % state.getDimensions().getFirst(), (pos.getY() + 1) % state.getDimensions().getSecond());
            state.moveAgent(pos, newPos);
            return state;
        });
        final Agent agent = builder.build();
        agent.setType("A");
        return agent;
    }
}
