package it.unibo.ares.core.agent;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

import it.unibo.ares.core.utils.directionvector.DirectionVector;
import it.unibo.ares.core.utils.directionvector.DirectionVectorImpl;
import it.unibo.ares.core.utils.pos.Pos;
import it.unibo.ares.core.utils.pos.PosImpl;
import it.unibo.ares.core.utils.state.State;
import it.unibo.ares.core.utils.state.StateImpl;

/**
 * Unit test for a fire spread model agent.
 */
public class TestFireSpread {
        /**
         * Test a state with only one Fire-type Agent, the agent should not move.
         */
        @Test
        public void testFireSpreadModelAgent1() {
                FireSpreadAgentFactory factory = new FireSpreadAgentFactory();
                State state = new StateImpl(5, 5);

                // Creates a Fire-type Agent with type 1, vision radius 1, direction
                // (1,0), spread 1 and fuel 1.0
                Pos pos = new PosImpl(1, 1);
                DirectionVector dir = new DirectionVectorImpl(1.0, 0.0);
                Agent fireAgent1 = factory.getFireModelAgent(1, dir, 1, 1.0);
                state.addAgent(pos, fireAgent1);

                // The agent should not move

                // var agents = state.getAgents();

                fireAgent1.tick(state, pos);
                fireAgent1.tick(state, pos);
                fireAgent1.tick(state, pos);

                assert state.getAgentAt(pos).isEmpty();
        }

        /**
         * Test a state with two agents of different type (Fire and Tree type),
         * Tree-type inside the radius, the Fire-type Agent should spread to the
         * Tree-type.
         */
        @Test
        public void testFireSpreadModelAgent2() {
                FireSpreadAgentFactory factory = new FireSpreadAgentFactory();
                State state = new StateImpl(5, 5);

                // Create a Fire-type Agent with type 1, vision radius 1, direction (1,0),
                // spread 1 and fuel 1.0
                Pos pos = new PosImpl(0, 0);
                DirectionVector dir1 = new DirectionVectorImpl(1.0, 0.0);
                Agent fireAgent1 = factory.getFireModelAgent(1, dir1, 1, 1.0);
                state.addAgent(pos, fireAgent1);

                // Create a Tree-type Agent with type 2, threshold 0.5 and vision radius 1
                Pos pos2 = new PosImpl(1, 0);
                Agent treeAgent1 = factory.getTreeModelAgent(0.5, 0.3);
                state.addAgent(pos2, treeAgent1);

                // The fire should spread to the tree
                fireAgent1.tick(state, pos);

                assertTrue(state.getAgentAt(pos2).get()
                                .getParameters().getParameter("type", Integer.class)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Agent " + state.getAgentAt(pos2).get() + " has no type parameter"))
                                .getValue() == 1);
        }

        /**
         * Test a state with two agents of different type (Fire and Tree type), outside
         * the radius, the Fire-type Agent should not spread to the Tree-type.
         */
        @Test
        public void testFireSpreadModelAgent3() {
                FireSpreadAgentFactory factory = new FireSpreadAgentFactory();
                State state = new StateImpl(5, 5);

                // Create a Fire-type Agent with type 1, vision radius 1, direction (1,0),
                // spread 1 and fuel 1.0
                Pos pos = new PosImpl(0, 0);
                DirectionVector dir1 = new DirectionVectorImpl(1.0, 0.0);
                Agent fireAgent1 = factory.getFireModelAgent(1, dir1, 1, 1.0);
                state.addAgent(pos, fireAgent1);

                // Create a Tree-type Agent with type 2, threshold 0.5 and vision radius 1
                Pos pos2 = new PosImpl(2, 2);
                Agent treeAgent1 = factory.getTreeModelAgent(0.5, 0.3);
                state.addAgent(pos2, treeAgent1);

                // The fire should spread to the tree
                fireAgent1.tick(state, pos);

                assertTrue(state.getAgentAt(pos2).get()
                                .getParameters().getParameter("type", Integer.class)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Agent " + state.getAgentAt(pos2).get() + " has no type parameter"))
                                .getValue() == 2);
        }

        /**
         * Test a state with one Fire-type Agent and some Tree-type Agent inside
         * the radius and one outside, the Fire-type Agent should spread to the
         * Tree-types inside (one tick).
         */
        @Test
        public void testFireSpreadModelAgent4() {
                FireSpreadAgentFactory factory = new FireSpreadAgentFactory();
                State state = new StateImpl(5, 5);

                // Create a Fire-type Agent with type 1, vision radius 1, direction (1,0),
                // spread 1 and fuel 1.0
                Pos pos = new PosImpl(0, 0);
                DirectionVector dir1 = new DirectionVectorImpl(1.0, 0.0);
                Agent fireAgent1 = factory.getFireModelAgent(1, dir1, 1, 1.0);
                state.addAgent(pos, fireAgent1);

                PosImpl pos1 = new PosImpl(1, 0);
                PosImpl pos2 = new PosImpl(0, 1);
                PosImpl pos3 = new PosImpl(1, 1);
                PosImpl pos4 = new PosImpl(2, 2);

                // Creates some Tree-type Agent with type 2, threshold 0.5 and vision radius 1
                state.addAgent(pos1, factory.getTreeModelAgent(0.5, 0.1));
                state.addAgent(pos2, factory.getTreeModelAgent(0.5, 0.1));
                state.addAgent(pos3, factory.getTreeModelAgent(0.5, 0.1));
                state.addAgent(pos4, factory.getTreeModelAgent(0.5, 0.1));

                // The fire should spread to the tree
                fireAgent1.tick(state, pos);

                Boolean fire1 = state.getAgentAt(pos1).get()
                                .getParameters().getParameter("type", Integer.class)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Agent " + state.getAgentAt(
                                                                pos1).get() + " has no type parameter"))
                                .getValue() == 1;

                Boolean fire2 = state.getAgentAt(pos2).get()
                                .getParameters().getParameter("type", Integer.class)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Agent " + state.getAgentAt(pos2).get() + " has no type parameter"))
                                .getValue() == 1;

                Boolean fire3 = state.getAgentAt(pos3).get()
                                .getParameters().getParameter("type", Integer.class)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Agent " + state.getAgentAt(pos3).get() + " has no type parameter"))
                                .getValue() == 1;

                Boolean fire4 = state.getAgentAt(pos4).get()
                                .getParameters().getParameter("type", Integer.class)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Agent " + state.getAgentAt(pos4).get() + " has no type parameter"))
                                .getValue() == 2;

                assertTrue(fire1 && fire2 && fire3 && fire4);
        }

        /**
         * Test a state with one Fire-type Agent and some Tree-type Agent inside
         * the radius and one outside, the Fire-type Agent should spread to the
         * Tree-types inside (more ticks).
         */
        @Test
        public void testFireSpreadModelAgent5() {
                FireSpreadAgentFactory factory = new FireSpreadAgentFactory();
                State state = new StateImpl(5, 5);

                // Create a Fire-type Agent with type 1, vision radius 1, direction (1,0),
                // spread 1 and fuel 1.0
                Pos pos = new PosImpl(0, 0);
                DirectionVector dir1 = new DirectionVectorImpl(1.0, 0.0);
                Agent fireAgent = factory.getFireModelAgent(1, dir1, 1, 1.0);
                state.addAgent(pos, fireAgent);

                PosImpl pos1 = new PosImpl(1, 0);
                PosImpl pos2 = new PosImpl(0, 1);
                PosImpl pos3 = new PosImpl(1, 1);
                PosImpl pos4 = new PosImpl(2, 2);

                // Creates some Tree-type Agent with type 2, threshold 0.5 and vision radius 1
                state.addAgent(pos1, factory.getTreeModelAgent(0.5, 0.1));
                state.addAgent(pos2, factory.getTreeModelAgent(0.5, 0.1));
                state.addAgent(pos3, factory.getTreeModelAgent(0.5, 0.1));
                state.addAgent(pos4, factory.getTreeModelAgent(0.5, 0.1));

                fireAgent.tick(state, pos);

                Agent fireAgent1 = state.getAgentAt(pos1).get();
                Agent fireAgent2 = state.getAgentAt(pos2).get();
                Agent fireAgent3 = state.getAgentAt(pos3).get();

                fireAgent.tick(state, pos);
                fireAgent1.tick(state, pos1);
                fireAgent2.tick(state, pos2);
                fireAgent3.tick(state, pos3);

                Boolean fire1 = state.getAgentAt(pos1).get()
                                .getParameters().getParameter("type", Integer.class)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Agent " + state.getAgentAt(
                                                                pos1).get() + " has no type parameter"))
                                .getValue() == 1;

                Boolean fire2 = state.getAgentAt(pos2).get()
                                .getParameters().getParameter("type", Integer.class)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Agent " + state.getAgentAt(pos2).get() + " has no type parameter"))
                                .getValue() == 1;

                Boolean fire3 = state.getAgentAt(pos3).get()
                                .getParameters().getParameter("type", Integer.class)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Agent " + state.getAgentAt(pos3).get() + " has no type parameter"))
                                .getValue() == 1;

                Boolean fire4 = state.getAgentAt(pos4).get()
                                .getParameters().getParameter("type", Integer.class)
                                .orElseThrow(() -> new IllegalArgumentException(
                                                "Agent " + state.getAgentAt(pos4).get() + " has no type parameter"))
                                .getValue() == 1;

                assertTrue(fire1 && fire2 && fire3 && fire4);
        }
}
