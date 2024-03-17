package it.unibo.ares.core.agent;

import it.unibo.ares.core.utils.pos.Pos;
import it.unibo.ares.core.utils.pos.PosImpl;
import it.unibo.ares.core.utils.state.State;
import it.unibo.ares.core.utils.state.StateImpl;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit test for a fire spread model agent.
 */
public class TestFireSpread {
        /**
         * Test a state with only one Fire agent, the agent should not move
         * and die after few ticks.
         */
        @Test
        public void testFireSpreadModelAgent1() {
                FireAgentFactory fireFactory = new FireAgentFactory();
                // CHECKSTYLE: MagicNumber OFF just the dimension of the state, not important
                State state = new StateImpl(5, 5);

                // CHECKSTYLE: MagicNumber ON
                // Creates a Fire-type Agent with type 1, vision radius 1, direction (1,0),
                // spread 1, fuel 1.0 and consumption 0.3
                Pos pos = new PosImpl(3, 3);
                Agent fireAgent1 = fireFactory.createAgent();
                fireAgent1.setParameter("spread", 1);
                fireAgent1.setParameter("fuel", 1.0);
                fireAgent1.setParameter("consumption", 0.5);
                state.addAgent(pos, fireAgent1);

                // The agent should not move
                fireAgent1.tick(state, pos);
                fireAgent1.tick(state, pos);

                assert state.getAgentAt(pos).isEmpty();
        }

        /**
         * Test a state with two agents of different type (Fire and Tree type), outside
         * the radius, the Fire agent should not spread to the Tree.
         */
        @Test
        public void testFireSpreadModelAgent3() {
                FireAgentFactory fireFactory = new FireAgentFactory();
                TreeAgentFactory treeFactory = new TreeAgentFactory();

                // CHECKSTYLE: MagicNumber OFF just the dimension of the state, not important
                State state = new StateImpl(5, 5);
                // CHECKSTYLE: MagicNumber ON

                // CHECKSTYLE: MagicNumber OFF
                // Create a Fire-type Agent with type 1, vision radius 1, direction (1,0),
                // spread 1, fuel 1.0 and consumption 0.3
                Pos pos = new PosImpl(0, 0);
                Agent fireAgent1 = fireFactory.createAgent();
                fireAgent1.setParameter("spread", 1);
                fireAgent1.setParameter("fuel", 1.0);
                fireAgent1.setParameter("consumption", 0.3);
                state.addAgent(pos, fireAgent1);
                // CHECKSTYLE: MagicNumber ON

                // CHECKSTYLE: MagicNumber OFF
                // Create a Tree-type Agent, fuel 0.5 and flammability 0.3
                Pos pos2 = new PosImpl(2, 2);
                Agent treeAgent1 = treeFactory.createAgent();
                treeAgent1.setParameter("fuel", 0.5);
                treeAgent1.setParameter("flammability", 0.3);
                state.addAgent(pos2, treeAgent1);
                // CHECKSTYLE: MagicNumber ON

                // The fire should spread to the tree
                fireAgent1.tick(state, pos);

                assertTrue(state.getAgentAt(pos2).get().getType() == "T");
        }

        /**
         * Test a state with one Fire agent and some Tree agent inside
         * the radius and one outside, the Fire agent should spread to one of the
         * Tree inside (one tick).
         */
        @Test
        public void testFireSpreadModelAgent4() {
                FireAgentFactory fireFactory = new FireAgentFactory();
                TreeAgentFactory treeFactory = new TreeAgentFactory();

                // CHECKSTYLE: MagicNumber OFF just the dimension of the state, not important
                State state = new StateImpl(5, 5);
                // CHECKSTYLE: MagicNumber ON

                // CHECKSTYLE: MagicNumber OFF
                // Create a Fire-type Agent with type 1, vision radius 1, direction (1,0),
                // spread 1, fuel 1.0 and consumption 0.3
                Pos pos = new PosImpl(0, 0);
                Agent fireAgent1 = fireFactory.createAgent();
                fireAgent1.setParameter("spread", 1);
                fireAgent1.setParameter("fuel", 1.0);
                fireAgent1.setParameter("consumption", 0.3);
                state.addAgent(pos, fireAgent1);
                // CHECKSTYLE: MagicNumber ON

                // CHECKSTYLE: MagicNumber OFF
                PosImpl pos1 = new PosImpl(1, 0);
                PosImpl pos2 = new PosImpl(0, 1);
                PosImpl pos3 = new PosImpl(1, 1);
                PosImpl pos4 = new PosImpl(2, 2);

                // Creates some Tree-type Agent with fuel 0.5 and flammability 0.3
                Agent treeAgent = treeFactory.createAgent();
                treeAgent.setParameter("fuel", 0.5);
                treeAgent.setParameter("flammability", 0.1);
                state.addAgent(pos1, treeAgent);
                state.addAgent(pos2, treeAgent);
                state.addAgent(pos3, treeAgent);
                state.addAgent(pos4, treeAgent);
                // CHECKSTYLE: MagicNumber ON

                // The fire should spread to the tree
                fireAgent1.tick(state, pos);

                Boolean fire1 = state.getAgentAt(pos1).get().getType() == "F";
                Boolean fire2 = state.getAgentAt(pos2).get().getType() == "F";
                Boolean fire3 = state.getAgentAt(pos3).get().getType() == "F";
                Boolean fire4 = state.getAgentAt(pos4).get().getType() == "F";

                assertTrue(fire1 || fire2 || fire3 || fire4);
        }

        /**
         * Test a state with one Fire agent and some Tree agent inside
         * the radius and one outside, the Fire agent should spread to the
         * Trees inside (more ticks).
         */
        @Test
        public void testFireSpreadModelAgent5() {
                FireAgentFactory fireFactory = new FireAgentFactory();
                TreeAgentFactory treeFactory = new TreeAgentFactory();

                // CHECKSTYLE: MagicNumber OFF just the dimension of the state, not important
                State state = new StateImpl(5, 5);
                // CHECKSTYLE: MagicNumber ON

                // CHECKSTYLE: MagicNumber OFF
                // Create a Fire-type Agent with type 1, vision radius 1, direction (1,0),
                // spread 1, fuel 1.0 and consumption 0.3
                Pos pos = new PosImpl(0, 0);
                Agent fireAgent = fireFactory.createAgent();
                fireAgent.setParameter("spread", 1);
                fireAgent.setParameter("fuel", 1.0);
                fireAgent.setParameter("consumption", 0.3);
                state.addAgent(pos, fireAgent);
                // CHECKSTYLE: MagicNumber ON

                // CHECKSTYLE: MagicNumber OFF
                PosImpl pos1 = new PosImpl(1, 0);
                PosImpl pos2 = new PosImpl(2, 0);
                PosImpl pos3 = new PosImpl(3, 0);
                PosImpl pos4 = new PosImpl(2, 2);

                // Creates some Tree-type Agent with fuel 0.5 and flammability 0.1
                Agent treeAgent = treeFactory.createAgent();
                treeAgent.setParameter("fuel", 0.5);
                treeAgent.setParameter("flammability", 0.1);
                state.addAgent(pos1, treeAgent);
                state.addAgent(pos2, treeAgent);
                state.addAgent(pos3, treeAgent);
                state.addAgent(pos4, treeAgent);
                // CHECKSTYLE: MagicNumber ON

                fireAgent.tick(state, pos);

                Agent fireAgent1 = state.getAgentAt(pos1).get();

                fireAgent.tick(state, pos);
                fireAgent1.tick(state, pos1);

                Agent fireAgent2 = state.getAgentAt(pos2).get();
                fireAgent.tick(state, pos);
                fireAgent1.tick(state, pos1);
                fireAgent2.tick(state, pos2);

                Agent fireAgent3 = state.getAgentAt(pos3).get();
                fireAgent.tick(state, pos);
                fireAgent1.tick(state, pos1);
                fireAgent2.tick(state, pos2);
                fireAgent3.tick(state, pos3);

                Boolean fire1 = state.getAgentAt(pos1).get().getType() == "F";
                Boolean fire2 = state.getAgentAt(pos2).get().getType() == "F";
                Boolean fire3 = state.getAgentAt(pos3).get().getType() == "F";
                Boolean fire4 = state.getAgentAt(pos4).get().getType() == "T";

                assertTrue(fire1 && fire2 && fire3 && fire4);
        }
}
