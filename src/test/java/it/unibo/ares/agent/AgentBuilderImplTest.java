package it.unibo.ares.agent;

import static org.junit.jupiter.api.Assertions.assertThrows;


import java.util.function.BiFunction;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.unibo.ares.utils.parameters.ParameterImpl;
import it.unibo.ares.utils.pos.Pos;
import it.unibo.ares.utils.state.State;

final public class AgentBuilderImplTest {
    private AgentBuilderImpl agentBuilder;

    @BeforeEach
    public void setUp() {
        agentBuilder = new AgentBuilderImpl();
    }

    @Test
    void testBuildWithNullStrategy() {
        assertThrows(IllegalStateException.class, () -> {
            agentBuilder.build();
        });
    }

    @Test
    void testBuild() {
        BiFunction<State, Pos, State> strategy = (state, pos) -> state;
        agentBuilder.addStrategy(strategy)
                    .addParameter(new ParameterImpl<>("testKey", 10))
                    .addParameter(new ParameterImpl<>("testKey2", "testValue"))
                    .addParameter(new ParameterImpl<>("testKey3", true));

    }
}
