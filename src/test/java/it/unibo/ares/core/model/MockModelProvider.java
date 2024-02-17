package it.unibo.ares.core.model;

import it.unibo.ares.core.agent.MockAgentProvider;
import it.unibo.ares.core.utils.pos.PosImpl;
import it.unibo.ares.core.utils.state.State;
import it.unibo.ares.core.utils.state.StateImpl;

public class MockModelProvider {
    public static Model getMockModel() {
        ModelBuilder builder = new ModelBuilderImpl();
        builder.addExitFunction((state, state2) -> false);
        builder.addInitFunction(params -> {
            State state = new StateImpl(10, 10);
            state.addAgent(new PosImpl(0, 0), MockAgentProvider.getMockAgent());
            return state;
        });
        return builder.build();
    }
}
