package it.unibo.ares.core.agent;

import it.unibo.ares.core.utils.pos.Pos;
import it.unibo.ares.core.utils.pos.PosImpl;
import it.unibo.ares.core.utils.state.State;
import it.unibo.ares.core.utils.state.StateImpl;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit test for a fire spread model agent.
 */
class TestFireSpread {
    private static final String FUEL = "fuel";
    private static final String CONS = "consumption";
    private static final String SPREAD = "spread";
    private static final String FLAMM = "flammability";

    // CHECKSTYLE: MissingJavadocMethod OFF
    private Agent getFireAgent(final Double fuel, final Integer spread, final Double cons) {

        final FireAgentFactory fireFactory = new FireAgentFactory();
        final Agent agent = fireFactory
                .createAgent();
        agent.setParameter(FUEL, fuel);
        agent.setParameter(SPREAD, spread);
        agent.setParameter(CONS, cons);
        return agent;
    }

    private Agent getTreeAgent(final Double fuel, final Double flamm) {
        final TreeAgentFactory treeFactory = new TreeAgentFactory();
        final Agent agent = treeFactory.createAgent();
        agent.setParameter(FUEL, fuel);
        agent.setParameter(FLAMM, flamm);
        return agent;
    }
    // CHECKSTYLE: MissingJavadocMethod ON

    /**
     * Test a state with only one Fire agent, the agent should not move
     * and die after few ticks.
     */
    @Test
    void testFireSpreadModelAgent1() {
        // CHECKSTYLE: MagicNumber OFF just the dimension of the state, not important
        final State state = new StateImpl(5, 5);

        // CHECKSTYLE: MagicNumber ON
        // Creates a Fire-type Agent with type 1, vision radius 1, direction (1,0),
        // spread 1, fuel 1.0 and consumption 0.3
        final Pos pos = new PosImpl(3, 3);
        final Agent fireAgent1 = getFireAgent(1.0, 1, 0.5);
        state.addAgent(pos, fireAgent1);

        // The agent should not move
        fireAgent1.tick(state, pos);
        fireAgent1.tick(state, pos);

        assert "E".equals(state.getAgentAt(pos).get().getType());
    }

    /**
     * Test a state with two agents of different type (Fire and Tree type), outside
     * the radius, the Fire agent should not spread to the Tree.
     */
    @Test
    void testFireSpreadModelAgent2() {
        // CHECKSTYLE: MagicNumber OFF just the dimension of the state, not important
        final State state = new StateImpl(5, 5);
        // CHECKSTYLE: MagicNumber ON

        // CHECKSTYLE: MagicNumber OFF
        // Create a Fire-type Agent with type 1, vision radius 1, direction (1,0),
        // spread 1, fuel 1.0 and consumption 0.3
        final Pos pos = new PosImpl(0, 0);
        final Agent fireAgent1 = getFireAgent(1.0, 1, 0.3);
        state.addAgent(pos, fireAgent1);
        // CHECKSTYLE: MagicNumber ON

        // CHECKSTYLE: MagicNumber OFF
        // Create a Tree-type Agent, fuel 0.5 and flammability 0.3
        final Pos pos2 = new PosImpl(2, 2);
        final Agent treeAgent1 = getTreeAgent(0.5, 0.3);
        state.addAgent(pos2, treeAgent1);
        // CHECKSTYLE: MagicNumber ON

        // The fire should spread to the tree
        fireAgent1.tick(state, pos);

        assertEquals("T", state.getAgentAt(pos2).get().getType());
    }

    /**
     * Test a state with one Fire agent and some Tree agent inside
     * the radius and one outside, the Fire agent should spread to one of the
     * Tree inside (one tick).
     */
    @Test
    void testFireSpreadModelAgent3() {
        // CHECKSTYLE: MagicNumber OFF just the dimension of the state, not important
        final State state = new StateImpl(5, 5);
        // CHECKSTYLE: MagicNumber ON

        // CHECKSTYLE: MagicNumber OFF
        // Create a Fire-type Agent with type 1, vision radius 1, direction (1,0),
        // spread 1, fuel 1.0 and consumption 0.3
        final Pos pos = new PosImpl(0, 0);
        final Agent fireAgent = getFireAgent(1.0, 1, 0.3);
        state.addAgent(pos, fireAgent);
        // CHECKSTYLE: MagicNumber ON

        // CHECKSTYLE: MagicNumber OFF
        final PosImpl pos1 = new PosImpl(1, 0);
        final PosImpl pos2 = new PosImpl(0, 1);
        final PosImpl pos3 = new PosImpl(1, 1);
        final PosImpl pos4 = new PosImpl(2, 2);

        // Creates some Tree-type Agent with fuel 0.5 and flammability 0.3
        final Agent treeAgent = getTreeAgent(0.5, 0.3);
        state.addAgent(pos1, treeAgent);
        state.addAgent(pos2, treeAgent);
        state.addAgent(pos3, treeAgent);
        state.addAgent(pos4, treeAgent);
        // CHECKSTYLE: MagicNumber ON

        // The fire should spread to the tree
        fireAgent.tick(state, pos);

        final Boolean fire1 = "F".equals(state.getAgentAt(pos1).get().getType());
        final Boolean fire2 = "F".equals(state.getAgentAt(pos2).get().getType());
        final Boolean fire3 = "F".equals(state.getAgentAt(pos3).get().getType());
        final Boolean fire4 = "F".equals(state.getAgentAt(pos4).get().getType());

        assertTrue(fire1 || fire2 || fire3 || fire4);
    }

    /**
     * Test a state with one Fire agent and some Tree agent inside
     * the radius and one outside, the Fire agent should spread to the
     * Trees inside (more ticks).
     */
    @Test
    void testFireSpreadModelAgent4() {
        // CHECKSTYLE: MagicNumber OFF just the dimension of the state, not important
        final State state = new StateImpl(5, 5);
        // CHECKSTYLE: MagicNumber ON

        // CHECKSTYLE: MagicNumber OFF
        // Create a Fire-type Agent with type 1, vision radius 1,
        // spread 1, fuel 1.0 and consumption 0.3
        final Pos pos = new PosImpl(0, 0);
        final Agent fireAgent = getFireAgent(1.0, 1, 0.3);
        state.addAgent(pos, fireAgent);
        // CHECKSTYLE: MagicNumber ON

        // CHECKSTYLE: MagicNumber OFF
        final PosImpl pos1 = new PosImpl(1, 0);
        final PosImpl pos2 = new PosImpl(0, 1);
        final PosImpl pos3 = new PosImpl(1, 1);
        final PosImpl pos4 = new PosImpl(3, 3);

        // Creates some Tree-type Agent with fuel 0.5 and flammability 0.1
        final Agent treeAgent = getTreeAgent(0.5, 0.1);
        state.addAgent(pos1, treeAgent);
        state.addAgent(pos2, treeAgent);
        state.addAgent(pos3, treeAgent);
        state.addAgent(pos4, treeAgent);
        // CHECKSTYLE: MagicNumber ON

        fireAgent.tick(state, pos);
        fireAgent.tick(state, pos);
        fireAgent.tick(state, pos);
        fireAgent.tick(state, pos);

        final Agent agent1 = state.getAgentAt(pos1).get();
        agent1.tick(state, pos1);

        final Agent agent2 = state.getAgentAt(pos2).get();
        agent1.tick(state, pos1);
        agent2.tick(state, pos2);

        final Agent agent3 = state.getAgentAt(pos3).get();
        agent1.tick(state, pos1);
        agent2.tick(state, pos2);
        agent3.tick(state, pos3);

        final Boolean fire1 = "F".equals(state.getAgentAt(pos1).get().getType());
        final Boolean fire2 = "F".equals(state.getAgentAt(pos2).get().getType());
        final Boolean tree1 = "T".equals(state.getAgentAt(pos4).get().getType());
        assertTrue(fire1 && fire2 && tree1);
    }
}
