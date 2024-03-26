package it.unibo.ares.core.agent;

import it.unibo.ares.core.utils.Pair;
import it.unibo.ares.core.utils.parameters.ParameterImpl;
import it.unibo.ares.core.utils.pos.Pos;
import it.unibo.ares.core.utils.pos.PosImpl;
import it.unibo.ares.core.utils.state.State;
import it.unibo.ares.core.utils.state.StateImpl;
import org.junit.jupiter.api.Test;

import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit test for {\@link Agent}.
 */
class AgentTest {
    // Disable magic number chekstyle, they're random values to
    // CHECKSTYLE: MagicNumber OFF
    private static final String TYPE = "type";

    private State getTestState() {
        return new StateImpl(5, 5);
    }

    private Agent getSimpleTestAgent() {
        final AgentBuilderImpl agentBuilder = new AgentBuilderImpl();
        agentBuilder.addStrategy((state, pos) -> state);
        return agentBuilder.build();
    }

    /**
     * Test an agent with a strategy that return the State unchanged.
     */
    @Test
    void simpleAgentTest() {
        final State simpleTestState = getTestState();
        final Pos testPos = new PosImpl(3, 3);
        final Agent agent = getSimpleTestAgent();

        assertEquals(agent.tick(simpleTestState, testPos), simpleTestState);
    }

    private boolean isAgentOfSameType(final Agent a, final Agent b) {
        final Integer type1 = a.getParameters().getParameter(TYPE, Integer.class).get().getValue();
        final Integer type2 = b.getParameters().getParameter(TYPE, Integer.class).get().getValue();
        return type1.equals(type2);
    }

    private Agent getAgentWithStrategyAndWithParameter(final ParameterImpl<Integer> parameter) {
        return new AgentBuilderImpl()
                .addStrategy((state, pos) -> {
                    state.getAgents().stream()
                            .filter(pair -> !isAgentOfSameType(pair.getSecond(), state.getAgentAt(pos).get()))
                            .forEach(pair -> state.removeAgent(pair.getFirst(), pair.getSecond()));

                    return state;
                })
                .addParameter(parameter)
                .build();
    }

    /**
     * Test an agent with a strategy that removes all the agents of different types.
     */
    @Test
    void agentWithStrategyAndParametersTest() {
        final Agent agent1a = getAgentWithStrategyAndWithParameter(new ParameterImpl<Integer>(TYPE, 1, true));
        final Agent agent1b = getAgentWithStrategyAndWithParameter(new ParameterImpl<Integer>(TYPE, 1, true));
        final Agent agent2 = getAgentWithStrategyAndWithParameter(new ParameterImpl<Integer>(TYPE, 2, true));
        State state = getTestState();
        final Pos agent1aPos = new PosImpl(1, 1);
        state.addAgent(agent1aPos, agent1a);
        state.addAgent(new PosImpl(1, 2), agent1b);
        state.addAgent(new PosImpl(1, 3), agent2);
        assertEquals(3, state.getAgents().size());
        state = agent1a.tick(state, agent1aPos);
        assertEquals(2, state.getAgents().size());
        assertTrue(state.getAgents().stream().map(Pair::getSecond).collect(Collectors.toList()).contains(agent1a));
        assertTrue(state.getAgents().stream().map(Pair::getSecond).collect(Collectors.toList()).contains(agent1b));

    }

    /**
     * Test an agent with a strategy that removes all the agents of different types.
     */
    @Test
    void agentWithStrategyAndParametersTest2() {
        final Agent agent1a = getAgentWithStrategyAndWithParameter(new ParameterImpl<Integer>(TYPE, 1, true));
        final Agent agent1b = getAgentWithStrategyAndWithParameter(new ParameterImpl<Integer>(TYPE, 1, true));
        final Agent agent2 = getAgentWithStrategyAndWithParameter(new ParameterImpl<Integer>(TYPE, 2, true));

        State state = getTestState();
        state.addAgent(new PosImpl(1, 1), agent1a);
        state.addAgent(new PosImpl(1, 2), agent1b);
        state.addAgent(new PosImpl(1, 3), agent2);
        assertEquals(3, state.getAgents().size());

        state = agent2.tick(state, new PosImpl(1, 3));
        assertEquals(1, state.getAgents().size());

        assertFalse(state.getAgents().stream().map(Pair::getSecond).collect(Collectors.toList()).contains(agent1a));
        assertFalse(state.getAgents().stream().map(Pair::getSecond).collect(Collectors.toList()).contains(agent1b));
    }
    // CHECKSTYLE: MagicNumber ON
}
