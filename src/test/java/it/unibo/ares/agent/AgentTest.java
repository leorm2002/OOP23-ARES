package it.unibo.ares.agent;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.unibo.ares.core.agent.Agent;
import it.unibo.ares.core.agent.AgentBuilder;
import it.unibo.ares.core.agent.AgentBuilderImpl;
import it.unibo.ares.core.utils.Pair;
import it.unibo.ares.core.utils.parameters.ParameterImpl;
import it.unibo.ares.core.utils.pos.Pos;
import it.unibo.ares.core.utils.pos.PosImpl;
import it.unibo.ares.core.utils.state.State;
import it.unibo.ares.core.utils.state.StateImpl;
/**
 * Unit test for {@link Agent}.
 */
public class AgentTest {
    //Disable magic number chekstyle, they're random values to
    // CHECKSTYLE: MagicNumber OFF

    private AgentBuilderImpl agentBuilder;

    /**
     * Istantiate a new AgentBuilder before each test.
     */
    @BeforeEach
    public void setUp() {
        agentBuilder = new AgentBuilderImpl();
    }

    private State getTestState() {
        State state = new StateImpl(5, 5);

        return state;
    }

    private Agent getSimpleTestAgent() {
        agentBuilder.addStrategy((state, pos) -> state);
        return agentBuilder.build();
    }

    /**
     *  Test an agent with a strategy that return the State unchanged.
     */
    @Test
    public void simpleAgentTest() {

        State simpleTestState = getTestState();
        Pos testPos = new PosImpl(3, 3);
        Agent agent = getSimpleTestAgent();

        assertEquals(agent.tick(simpleTestState, testPos), simpleTestState);
    }

    private boolean isAgentOfSameType(final Agent a, final Agent b) {
        Integer type1 = a.getParameters().getParameter("type", Integer.class).get().getValue();
        Integer type2 = b.getParameters().getParameter("type", Integer.class).get().getValue();
        return type1.equals(type2);
    }

    private Agent getAgentWithStrategyAndWithParameter(final ParameterImpl<Integer> parameter) {
        AgentBuilder b  =  new AgentBuilderImpl();

        //Removes all the agents of different types
        b.addStrategy((state, pos) -> {
            state.getAgents().stream()
            .filter(pair -> !isAgentOfSameType(pair.getSecond(), state.getAgentAt(pos).get()))
            .forEach(pair -> state.removeAgent(pair.getFirst(), pair.getSecond()));

            return state;
        });
        b.addParameter(parameter);
        return b.build();
    }

    /**
     * Test an agent with a strategy that removes all the agents of different types.
     */
    @Test
    public void agentWithStrategyAndParametersTest() {
        Agent agent1a = getAgentWithStrategyAndWithParameter(new ParameterImpl<Integer>("type", 1));
        Agent agent1b = getAgentWithStrategyAndWithParameter(new ParameterImpl<Integer>("type", 1));
        Agent agent2 = getAgentWithStrategyAndWithParameter(new ParameterImpl<Integer>("type", 2));
        State state = getTestState();
        Pos agent1aPos = new PosImpl(1, 1);
        state.addAgent(agent1aPos, agent1a);
        state.addAgent(new PosImpl(1, 2), agent1b);
        state.addAgent(new PosImpl(1, 3), agent2);
        assertEquals(state.getAgents().size(), 3);
        state = agent1a.tick(state, agent1aPos);
        assertEquals(2, state.getAgents().size());
        assertTrue(state.getAgents().stream().map(Pair::getSecond).collect(Collectors.toList()).contains(agent1a));
        assertTrue(state.getAgents().stream().map(Pair::getSecond).collect(Collectors.toList()).contains(agent1b));


    }
    /**
     * Test an agent with a strategy that removes all the agents of different types.
     */
    @Test
    public void agentWithStrategyAndParametersTest2() {
        Agent agent1a = getAgentWithStrategyAndWithParameter(new ParameterImpl<Integer>("type", 1));
        Agent agent1b = getAgentWithStrategyAndWithParameter(new ParameterImpl<Integer>("type", 1));
        Agent agent2 = getAgentWithStrategyAndWithParameter(new ParameterImpl<Integer>("type", 2));

        State state = getTestState();
        state.addAgent(new PosImpl(1, 1), agent1a);
        state.addAgent(new PosImpl(1, 2), agent1b);
        state.addAgent(new PosImpl(1, 3), agent2);
        assertEquals(state.getAgents().size(), 3);

        state = agent2.tick(state, new PosImpl(1, 3));
        assertEquals(1, state.getAgents().size());

        assertTrue(!state.getAgents().stream().map(Pair::getSecond).collect(Collectors.toList()).contains(agent1a));
        assertTrue(!state.getAgents().stream().map(Pair::getSecond).collect(Collectors.toList()).contains(agent1b));
    }
    // CHECKSTYLE: MagicNumber ON
}
