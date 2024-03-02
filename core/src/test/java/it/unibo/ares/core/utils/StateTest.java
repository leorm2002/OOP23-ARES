package it.unibo.ares.core.utils;

import it.unibo.ares.core.agent.Agent;
import it.unibo.ares.core.utils.pos.PosImpl;
import it.unibo.ares.core.utils.state.State;
import it.unibo.ares.core.utils.state.StateImpl;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

/**
 * Unit test for {@link State}.
 */
public class StateTest {
    // Disable magic number chekstyle, they're random values to
    // CHECKSTYLE: MagicNumber OFF

    /**
     * Test the creation of a state and its dimensions.
     */
    @Test
    public void testState() {
        State state = new StateImpl(5, 5);
        assertEquals(state.getDimensions().getFirst(), 5);
        assertEquals(state.getDimensions().getSecond(), 5);
    }

    /**
     * Test the radius functionality.
     */
    @Test
    public void testRadius() {
        State state = new StateImpl(5, 5);

        assertEquals(3, state.getPosByPosAndRadius(new PosImpl(0, 0), 1).size());
        assertEquals(8, state.getPosByPosAndRadius(new PosImpl(0, 0), 2).size());
        assertEquals(15, state.getPosByPosAndRadius(new PosImpl(0, 0), 3).size());
        assertEquals(24, state.getPosByPosAndRadius(new PosImpl(0, 0), 4).size());
        assertEquals(8, state.getPosByPosAndRadius(new PosImpl(1, 1), 1).size());
    }

    private Agent getSimpleTestAgent() {
        /*
         * 
         * var agentBuilder = new AgentBuilderImpl();
         * TODO ACCESS VIA REFLECTION
         * agentBuilder.addStrategy((state, pos) -> state);
         * return agentBuilder.build();
         */
        return null;
    }

    /**
     * Test adding an agent to the state which is out of bounds.
     */
    @Test
    public void testAddAgentOutOfBounds() {
        State state = new StateImpl(5, 5);
        assertThrows(IllegalArgumentException.class, () -> {
            state.addAgent(new PosImpl(-1, -1), getSimpleTestAgent());
        });
        assertThrows(IllegalArgumentException.class, () -> {
            state.addAgent(new PosImpl(6, 6), getSimpleTestAgent());
        });
    }
}
