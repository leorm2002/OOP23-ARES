package it.unibo.ares.core.agent;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.function.BiFunction;

import it.unibo.ares.core.utils.parameters.ParameterImpl;
import it.unibo.ares.core.utils.pos.Pos;
import it.unibo.ares.core.utils.state.State;

/**
 * Unit test for {@link AgentBuilderImpl}.
 */
final class AgentBuilderImplTest {

    /**
     * Should throw an IllegalStateException if we try to build an agent without a
     * strategy.
     */
    @Test
    void testBuildWithNullStrategy() {
        final AgentBuilderImpl agentBuilder = new AgentBuilderImpl();

        assertThrows(IllegalStateException.class, () -> {
            agentBuilder.build();
        });
    }

    /**
     * Should create an agent with a simple strategy.
     */
    @Test
    void testBuild() {
        final AgentBuilderImpl agentBuilder = new AgentBuilderImpl();

        final BiFunction<State, Pos, State> strategy = (state, pos) -> state;
        assertDoesNotThrow(() -> {
            agentBuilder.addStrategy(strategy)
                    .addParameter(new ParameterImpl<>("testKey", 10, true))
                    .addParameter(new ParameterImpl<>("testKey2", "testValue", true))
                    .addParameter(new ParameterImpl<>("testKey3", true, true));
        });
    }
}
