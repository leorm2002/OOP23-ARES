package it.unibo.ares.core.agent;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import it.unibo.ares.core.agent.Agent;
import it.unibo.ares.core.agent.SchellingsAgentFactory;
import it.unibo.ares.core.utils.pos.Pos;
import it.unibo.ares.core.utils.pos.PosImpl;
import it.unibo.ares.core.utils.state.State;
import it.unibo.ares.core.utils.state.StateImpl;

/**
 * Unit test for a schelling segregation model agent.
 */
public class TesttSchelling {
    /**
     * Test a sate with only one agent the agent should not move.
     */
    @Test
    public void testSchellingSegregationModelAgent1() {
        SchellingsAgentFactory factory = new SchellingsAgentFactory();
        // CHECKSTYLE: MagicNumber OFF just the dimension of the state, not important
        State state = new StateImpl(5, 5);
        // CHECKSTYLE: MagicNumber ON
        // Let's create a Schelling agent with type 1, threshold 0.5 and vision radius 1
        Pos pos = new PosImpl(1, 1);
        Agent type1Agent = factory.getSchellingSegregationModelAgent(1, 0.5, 1);
        state.addAgent(pos, type1Agent);

        // The agent should not move

        var agents = state.getAgents();

        type1Agent.tick(state, pos);

        assert state.getAgentAt(pos).isPresent();
    }

    /**
     * Test a sate with two agents of different type inside the radius, the first
     * agent should move the second souldn't.
     */
    @Test
    public void testSchellingSegregationModelAgent2() {
        SchellingsAgentFactory factory = new SchellingsAgentFactory();
        // CHECKSTYLE: MagicNumber OFF just the dimension of the state, not important
        State state = new StateImpl(5, 5);
        // CHECKSTYLE: MagicNumber ON

        // Let's create a Schelling agent with type 1, threshold 0.5 and vision radius 1
        Pos pos = new PosImpl(1, 1);
        Agent type1Agent = factory.getSchellingSegregationModelAgent(1, 0.5, 1);
        state.addAgent(pos, type1Agent);
        // Let's create a Schelling agent with type 2, threshold 0.5 and vision radius 1
        Pos pos2 = new PosImpl(1, 0);
        Agent type2Agent = factory.getSchellingSegregationModelAgent(2, 0.5, 1);
        state.addAgent(pos2, type2Agent);

        // The agent should move
        type1Agent.tick(state, pos);
        assertFalse(state.getAgentAt(pos).isPresent());

        // The agent should not move
        type2Agent.tick(state, pos2);
        assertTrue(state.getAgentAt(pos2).isPresent());

    }

    @Test
    public void testSchellingSegregationModelAgent3() {
        SchellingsAgentFactory factory = new SchellingsAgentFactory();
        // CHECKSTYLE: MagicNumber OFF just the dimension of the state, not important
        State state = new StateImpl(5, 5);
        // CHECKSTYLE: MagicNumber ON
        // Let's create a Schelling agent with type 1, threshold 0.5 and vision radius 1
        Pos pos = new PosImpl(1, 1);
        Agent type1Agent = factory.getSchellingSegregationModelAgent(1, 0.5, 1);
        state.addAgent(pos, type1Agent);
        // Let's create a Schelling agent with type 1, threshold 0.5 and vision radius 1
        Pos pos2 = new PosImpl(0, 1);
        Agent type1AgentB = factory.getSchellingSegregationModelAgent(1, 0.5, 1);
        state.addAgent(pos2, type1AgentB);
        // Let's create a Schelling agent with type 2, threshold 0.5 and vision radius 1
        Pos pos3 = new PosImpl(1, 0);
        Agent type2Agent = factory.getSchellingSegregationModelAgent(2, 0.5, 1);
        state.addAgent(pos3, type2Agent);
        // The agent should not move
        type1Agent.tick(state, pos);
        assertTrue(state.getAgentAt(pos).isPresent());
        // The agent should not move
        type2Agent.tick(state, pos2);
        assertTrue(state.getAgentAt(pos2).isPresent());
        // The agent should move
        type1AgentB.tick(state, pos3);
        assertFalse(state.getAgentAt(pos3).isPresent());
    }
}
