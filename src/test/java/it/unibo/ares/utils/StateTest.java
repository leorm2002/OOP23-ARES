package it.unibo.ares.utils;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import it.unibo.ares.agent.Agent;
import it.unibo.ares.agent.AgentBuilderImpl;
import it.unibo.ares.utils.pos.PosImpl;
import it.unibo.ares.utils.state.State;
import it.unibo.ares.utils.state.StateImpl;

public class StateTest {
    @Test
    public void testState() {
        State state = new StateImpl(5, 5);
        assert state.getDimensions().getFirst() == 5;
        assert state.getDimensions().getSecond() == 5;
    }

    @Test
    public void testRadius(){
        State state = new StateImpl(5, 5);
        assert state.getPosByPosAndRadius(new PosImpl(0, 0), 1).size() == 3;
        assert state.getPosByPosAndRadius(new PosImpl(0, 0), 2).size() == 8;
        assert state.getPosByPosAndRadius(new PosImpl(0, 0), 3).size() == 15;
        assert state.getPosByPosAndRadius(new PosImpl(0, 0), 4).size() == 24;
        assert state.getPosByPosAndRadius(new PosImpl(1, 1), 1).size() == 8;
    }

    private Agent getSimpleTestAgent() {
        var agentBuilder = new AgentBuilderImpl();

        agentBuilder.addStrategy((state, pos) -> state);
        return agentBuilder.build();
    }

    @Test
    public void testAddAgentOutOfBounds() {
        State state = new StateImpl(5, 5);
        assertThrows(IllegalArgumentException.class, () -> {
            state.addAgent(new PosImpl(-1,-1), getSimpleTestAgent());
        });
        assertThrows(IllegalArgumentException.class, () -> {
            state.addAgent(new PosImpl(6,6), getSimpleTestAgent());
        });
    }
}