package it.unibo.ares.agent;

import org.junit.jupiter.api.Test;

import it.unibo.ares.utils.pos.Pos;
import it.unibo.ares.utils.pos.PosImpl;
import it.unibo.ares.utils.state.State;
import it.unibo.ares.utils.state.StateImpl;

public class TestSchelling {
    @Test
    public void testSchellingSegregationModelAgent1() {
        ConcreteAgentFactory factory = new ConcreteAgentFactory();

        //Let's create a 5x5 state
        State state = new StateImpl(5, 5);
        
        //Let's create a Schelling agent with type 1, threshold 0.5 and vision radius 1
        Pos pos = new PosImpl(1, 1);
        Agent type1Agent = factory.getSchellingSegregationModelAgent(1, 0.5, 1);
        state.addAgent(pos, type1Agent);

        //The agent should not move

        var agents = state.getAgents();

        type1Agent.tick(state, pos);

        assert state.getAgentAt(pos).isPresent();
    }
    @Test
    public void testSchellingSegregationModelAgent2() {
        ConcreteAgentFactory factory = new ConcreteAgentFactory();

        //Let's create a 5x5 state
        State state = new StateImpl(5, 5);
        
        //Let's create a Schelling agent with type 1, threshold 0.5 and vision radius 1
        Pos pos = new PosImpl(1, 1);
        Agent type1Agent = factory.getSchellingSegregationModelAgent(1, 0.5, 1);
        state.addAgent(pos, type1Agent);
        //Let's create a Schelling agent with type 2, threshold 0.5 and vision radius 1
        Pos pos2 = new PosImpl(1, 0);
        Agent type2Agent = factory.getSchellingSegregationModelAgent(2, 0.5, 1);
        state.addAgent(pos2, type2Agent);

        //The agent should move
        type1Agent.tick(state, pos);

        assert state.getAgentAt(pos).isPresent() == false;

    }
    @Test
    public void testSchellingSegregationModelAgent3() {
        ConcreteAgentFactory factory = new ConcreteAgentFactory();

        //Let's create a 5x5 state
        State state = new StateImpl(5, 5);
        
        //Let's create a Schelling agent with type 1, threshold 0.5 and vision radius 1
        Pos pos = new PosImpl(1, 1);
        Agent type1Agent = factory.getSchellingSegregationModelAgent(1, 0.5, 1);
        state.addAgent(pos, type1Agent);

        //Let's create a Schelling agent with type 1, threshold 0.5 and vision radius 1
        Pos pos2 = new PosImpl(0, 1);
        Agent type1AgentB = factory.getSchellingSegregationModelAgent(1, 0.5, 1);
        state.addAgent(pos2, type1AgentB);

        //Let's create a Schelling agent with type 2, threshold 0.5 and vision radius 1
        Pos pos3 = new PosImpl(1, 0);
        Agent type2Agent = factory.getSchellingSegregationModelAgent(2, 0.5, 1);
        state.addAgent(pos3, type2Agent);

        //The agent should not move
        type1Agent.tick(state, pos);
        assert state.getAgentAt(pos).isPresent() == true;

        //The agent should not move
        type2Agent.tick(state, pos2);
        assert state.getAgentAt(pos2).isPresent() == true;

        //The agent should move
        type1AgentB.tick(state, pos3);
        assert state.getAgentAt(pos3).isPresent() == false;

    }
}
