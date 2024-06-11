package it.unibo.ares.core.utils;

import it.unibo.ares.core.agent.Agent;
import it.unibo.ares.core.utils.parameters.Parameters;
import it.unibo.ares.core.utils.pos.Pos;
import it.unibo.ares.core.utils.pos.PosImpl;
import it.unibo.ares.core.utils.state.State;
import it.unibo.ares.core.utils.state.StateImpl;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.Serializable;

/**
 * Unit test for {@link State}.
 */
class StateTest {
    // Disable magic number chekstyle, they're random values to
    // CHECKSTYLE: MagicNumber OFF

    /**
     * Test the creation of a state and its dimensions.
     */
    @Test
    void testState() {
        final State state = new StateImpl(5, 5);
        assertEquals(5, state.getDimensions().getFirst());
        assertEquals(5, state.getDimensions().getSecond());
    }

    /**
     * Test the radius functionality.
     */
    @Test
    void testRadius() {
        final State state = new StateImpl(5, 5);

        assertEquals(3, state.getPosByPosAndRadius(new PosImpl(0, 0), 1).size());
        assertEquals(8, state.getPosByPosAndRadius(new PosImpl(0, 0), 2).size());
        assertEquals(15, state.getPosByPosAndRadius(new PosImpl(0, 0), 3).size());
        assertEquals(24, state.getPosByPosAndRadius(new PosImpl(0, 0), 4).size());
        assertEquals(8, state.getPosByPosAndRadius(new PosImpl(1, 1), 1).size());
    }

    private Agent getSimpleTestAgent() {
        return new Agent() {

            @Override
            public State tick(final State state, final Pos pos) {
                return state;
            }

            @Override
            public Parameters getParameters() {
                throw new UnsupportedOperationException("Unimplemented method 'getParameters'");
            }

            @Override
            public <T extends Serializable> void setParameter(final String key, final T value) {
                throw new UnsupportedOperationException("Unimplemented method 'setParameter'");
            }

            @Override
            public String getId() {
                throw new UnsupportedOperationException("Unimplemented method 'getId'");
            }

            @Override
            public String getType() {
                throw new UnsupportedOperationException("Unimplemented method 'getType'");
            }

            @Override
            public void setType(final String type) {
                throw new UnsupportedOperationException("Unimplemented method 'setType'");
            }
        };
    }

    /**
     * Test adding an agent to the state which is out of bounds.
     */
    @Test
    void testAddAgentOutOfBounds() {
        final State state = new StateImpl(5, 5);
        assertThrows(IllegalArgumentException.class, () -> {
            state.addAgent(new PosImpl(-1, -1), getSimpleTestAgent());
        });
        assertThrows(IllegalArgumentException.class, () -> {
            state.addAgent(new PosImpl(6, 6), getSimpleTestAgent());
        });
    }
}
