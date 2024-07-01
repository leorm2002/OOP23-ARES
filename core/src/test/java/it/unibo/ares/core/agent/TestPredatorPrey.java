package it.unibo.ares.core.agent;

import it.unibo.ares.core.utils.pos.Pos;
import it.unibo.ares.core.utils.pos.PosImpl;
import it.unibo.ares.core.utils.state.State;
import it.unibo.ares.core.utils.state.StateImpl;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit test for the Predator-Prey model agents.
 */
class TestPredatorPrey {

    private static final String VISION_RADIUS_PREDATOR = "visionRadiusPredator";
    private static final String VISION_RADIUS_PREY = "visionRadiusPrey";

    private Agent getPredatorAgent(final Integer visionRadius) {
        final PredatorAgentFactory predatorFactory = new PredatorAgentFactory();
        final Agent agent = predatorFactory.createAgent();
        agent.setParameter(VISION_RADIUS_PREDATOR, visionRadius);
        return agent;
    }

    private Agent getPreyAgent(final Integer visionRadius) {
        final PreyAgentFactory preyFactory = new PreyAgentFactory();
        final Agent agent = preyFactory.createAgent();
        agent.setParameter(VISION_RADIUS_PREY, visionRadius);
        return agent;
    }

    /**
     * Test a state with one predator and one prey within the predator's vision
     * radius.
     */
    @Test
    void testPredatorHuntsPrey() {
        // CHECKSTYLE: MagicNumber OFF
        State state = new StateImpl(5, 5);

        final Pos predatorPos = new PosImpl(1, 1);
        final Pos preyPos = new PosImpl(2, 2);
        final Agent predator = getPredatorAgent(2);
        final Agent prey = getPreyAgent(2);

        state.addAgent(predatorPos, predator);
        state.addAgent(preyPos, prey);

        state = predator.tick(state, predatorPos);

        // Check if the predator has moved to the prey's position
        assertEquals(PredatorAgentFactory.PREDATOR, state.getAgentAt(preyPos).get().getType());
    }

    /**
     * Test a state with one predator and multiple prey within the predator's vision
     * radius.
     */
    @Test
    void testPredatorHuntsNearestPrey() {
        // CHECKSTYLE: MagicNumber OFF
        State state = new StateImpl(5, 5);

        final Pos predatorPos = new PosImpl(1, 1);
        final Pos preyPos1 = new PosImpl(1, 3);
        final Pos preyPos2 = new PosImpl(4, 1);
        final Agent predator = getPredatorAgent(5);
        final Agent prey1 = getPreyAgent(2);
        final Agent prey2 = getPreyAgent(2);

        state.addAgent(predatorPos, predator);
        state.addAgent(preyPos1, prey1);
        state.addAgent(preyPos2, prey2);

        state = predator.tick(state, predatorPos);

        final Pos expectedPosition = new PosImpl(1, 2);

        // Check if the predator has moved towards the nearest prey's position
        assertEquals(PredatorAgentFactory.PREDATOR, state.getAgentAt(expectedPosition).get().getType());
    }

    /**
     * Test a state with one prey and multiple predators within the prey's vision
     * radius.
     */
    @Test
    void testPreyEscapes() {
        // CHECKSTYLE: MagicNumber OFF
        State state = new StateImpl(5, 5);

        final Pos preyPos = new PosImpl(2, 2);
        final Pos predatorPos1 = new PosImpl(1, 1);
        final Pos predatorPos2 = new PosImpl(3, 3);
        final Agent prey = getPreyAgent(3);
        final Agent predator1 = getPredatorAgent(2);
        final Agent predator2 = getPredatorAgent(2);

        state.addAgent(preyPos, prey);
        state.addAgent(predatorPos1, predator1);
        state.addAgent(predatorPos2, predator2);

        // Prey should move to escape the predators
        state = prey.tick(state, preyPos);

        // Check if the prey has moved
        assertTrue(state.getAgentAt(preyPos).isEmpty());
    }

}
