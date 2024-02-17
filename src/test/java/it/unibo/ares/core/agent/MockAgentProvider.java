package it.unibo.ares.core.agent;

import it.unibo.ares.core.utils.pos.Pos;
import it.unibo.ares.core.utils.pos.PosImpl;

public class MockAgentProvider {
    public static Agent getMockAgent() {
        AgentBuilder builder = new AgentBuilderImpl();
        builder.addStrategy((state, pos) -> {
            Pos newPos = new PosImpl((pos.getX() + 1)
                    % state.getDimensions().getFirst(), (pos.getY() + 1) % state.getDimensions().getSecond());
            state.moveAgent(pos, newPos);
            return state;
        });
        return builder.build();
    }
}
