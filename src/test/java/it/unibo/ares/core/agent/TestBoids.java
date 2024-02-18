package it.unibo.ares.core.agent;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import it.unibo.ares.core.utils.directionvector.DirectionVector;
import it.unibo.ares.core.utils.directionvector.DirectionVectorImpl;
import it.unibo.ares.core.utils.pos.Pos;
import it.unibo.ares.core.utils.pos.PosImpl;
import it.unibo.ares.core.utils.state.State;
import it.unibo.ares.core.utils.state.StateImpl;

class TestBoids {
        @Test
        void testInsideCone() throws NoSuchMethodException, SecurityException, ClassNotFoundException,
                        IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                BoidsAgentFactory b = new BoidsAgentFactory();
                Pos pos = new PosImpl(0, 0);
                DirectionVector dir = new DirectionVectorImpl(1.0, 0.0);
                Integer radius = 3;
                Integer angle = 90;
                Set<PosImpl> inside = Stream.concat(
                                Stream.concat(
                                                IntStream.rangeClosed(-3, 3).mapToObj(y -> new PosImpl(0, y)),
                                                IntStream.rangeClosed(-2, 2).boxed()
                                                                .flatMap(y -> IntStream.rangeClosed(1, 2)
                                                                                .mapToObj(x -> new PosImpl(x, y)))),
                                Stream.of(new PosImpl(3, 0)))
                                .filter(p -> !(p.getX() == 0 && p.getY() == 0))
                                .collect(Collectors.toSet());

                Set<PosImpl> outside = IntStream.range(-10, 10).boxed()
                                .flatMap(x -> IntStream.range(-10, 10).mapToObj(y -> new PosImpl(x, y)))
                                .filter(p -> !inside.contains(p))
                                .collect(Collectors.toSet());
                Method method = Class.forName("it.unibo.ares.core.agent.BoidsAgentFactory")
                                .getDeclaredMethod("insideCone", Pos.class, Pos.class, DirectionVector.class,
                                                Integer.class,
                                                Integer.class);

                method.setAccessible(true);
                for (Pos p : inside) {
                        assertTrue((boolean) method.invoke(b, p, pos, dir, radius, angle));
                }
                for (Pos p : outside) {
                        assertFalse((boolean) method.invoke(b, p, pos, dir, radius, angle));
                }
        }

        @Test
        void testCollisionAvoidanceNotOutOfScope()
                        throws NoSuchMethodException, SecurityException, ClassNotFoundException,
                        IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                BoidsAgentFactory b = new BoidsAgentFactory();
                Agent movingAgent = b.createAgent();
                Pos movingAgentPos = new PosImpl(0, 0);
                DirectionVector movingAgentDir = new DirectionVectorImpl(1.0, 0.0);
                Integer radius = 3;
                Integer angle = 90;

                Agent obstacleAgent = b.createAgent();
                Pos obstaclePos = new PosImpl(4, 0);
                // CREATE STATE
                State state = new StateImpl(10, 10);
                state.addAgent(movingAgentPos, movingAgent);
                state.addAgent(obstaclePos, obstacleAgent);
                // TEST NOT VIEWED

                Method method = Class.forName("it.unibo.ares.core.agent.BoidsAgentFactory")
                                .getDeclaredMethod("collisionAvoindance", State.class, Pos.class, DirectionVector.class,
                                                Integer.class,
                                                Integer.class);

                method.setAccessible(true);

                DirectionVector newDir = (DirectionVectorImpl) method.invoke(b, state, movingAgentPos, movingAgentDir,
                                radius,
                                angle);
                assertEquals(newDir, movingAgentDir);
        }

        @Test
        void testCollisionAvoidance()
                        throws NoSuchMethodException, SecurityException, ClassNotFoundException,
                        IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                BoidsAgentFactory b = new BoidsAgentFactory();
                Agent movingAgent = b.createAgent();
                Pos movingAgentPos = new PosImpl(0, 0);
                DirectionVector movingAgentDir = new DirectionVectorImpl(1.0, 0.0);
                Integer radius = 3;
                Integer angle = 90;

                Agent obstacleAgent = b.createAgent();
                Pos obstaclePos = new PosImpl(1, 0);
                // CREATE STATE
                State state = new StateImpl(10, 10);
                state.addAgent(movingAgentPos, movingAgent);
                state.addAgent(obstaclePos, obstacleAgent);
                // TEST NOT VIEWED

                Method method = Class.forName("it.unibo.ares.core.agent.BoidsAgentFactory")
                                .getDeclaredMethod("collisionAvoindance", State.class, Pos.class, DirectionVector.class,
                                                Integer.class,
                                                Integer.class);

                method.setAccessible(true);

                DirectionVector newDir = (DirectionVectorImpl) method.invoke(b, state, movingAgentPos, movingAgentDir,
                                radius,
                                angle);
                DirectionVector expectedDir = new DirectionVectorImpl(-1.0, 0.0);
                assertEquals(newDir, expectedDir);
        }

        @Test
        void testCollisionAvoidanceWithTwoObstacles()
                        throws NoSuchMethodException, SecurityException, ClassNotFoundException,
                        IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                BoidsAgentFactory b = new BoidsAgentFactory();
                Agent movingAgent = b.createAgent();
                Pos movingAgentPos = new PosImpl(0, 0);
                DirectionVector movingAgentDir = new DirectionVectorImpl(1.0, 0.0);
                Integer radius = 3;
                Integer angle = 90;

                Agent obstacleAgent = b.createAgent();
                Pos obstaclePos = new PosImpl(1, 0);
                Agent obstacleAgent2 = b.createAgent();
                Pos obstaclePos2 = new PosImpl(1, 1);

                // CREATE STATE
                State state = new StateImpl(10, 10);
                state.addAgent(movingAgentPos, movingAgent);
                state.addAgent(obstaclePos, obstacleAgent);
                state.addAgent(obstaclePos2, obstacleAgent2);
                // TEST NOT VIEWED

                Method method = Class.forName("it.unibo.ares.core.agent.BoidsAgentFactory")
                                .getDeclaredMethod("collisionAvoindance", State.class, Pos.class, DirectionVector.class,
                                                Integer.class,
                                                Integer.class);
                method.setAccessible(true);
                DirectionVector newDir = (DirectionVectorImpl) method.invoke(
                                b, state, movingAgentPos, movingAgentDir,
                                radius,
                                angle);
                DirectionVector expectedDir = new DirectionVectorImpl(-2.0, -1.0);
                assertEquals(newDir, expectedDir.getNormalized());
        }

        @Test
        void testDirectionCenterCohesion()
                        throws NoSuchMethodException, SecurityException, ClassNotFoundException,
                        IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                BoidsAgentFactory b = new BoidsAgentFactory();
                Agent movingAgent = b.createAgent();
                Pos movingAgentPos = new PosImpl(0, 0);
                DirectionVector movingAgentDir = new DirectionVectorImpl(1.0, 0.0);
                Integer radius = 3;
                Integer angle = 90;

                Agent obstacleAgent = b.createAgent();
                Pos obstaclePos = new PosImpl(0, 1);
                Agent obstacleAgent2 = b.createAgent();
                Pos obstaclePos2 = new PosImpl(2, 1);

                // CREATE STATE
                State state = new StateImpl(10, 10);
                state.addAgent(movingAgentPos, movingAgent);
                state.addAgent(obstaclePos, obstacleAgent);
                state.addAgent(obstaclePos2, obstacleAgent2);
                // TEST NOT VIEWED

                Method method = Class.forName("it.unibo.ares.core.agent.BoidsAgentFactory")
                                .getDeclaredMethod("centerCohesion", State.class, Pos.class, DirectionVector.class,
                                                Integer.class,
                                                Integer.class);

                method.setAccessible(true);
                DirectionVector newDir = (DirectionVectorImpl) method.invoke(
                                b, state, movingAgentPos, movingAgentDir,
                                radius, angle);
                DirectionVector expectedDir = new DirectionVectorImpl(1, 1);
                assertEquals(newDir, expectedDir.getNormalized());
        }

        @Test
        void testDirectionCenterCohesionRemainingStill()
                        throws NoSuchMethodException, SecurityException, ClassNotFoundException,
                        IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                BoidsAgentFactory b = new BoidsAgentFactory();
                Agent movingAgent = b.createAgent();
                Pos movingAgentPos = new PosImpl(1, 1);
                DirectionVector movingAgentDir = new DirectionVectorImpl(1.0, 0.0);
                Integer radius = 3;
                Integer angle = 180;

                Agent obstacleAgent = b.createAgent();
                Pos obstaclePos = new PosImpl(0, 0);
                Agent obstacleAgent2 = b.createAgent();
                Pos obstaclePos2 = new PosImpl(2, 0);
                Agent obstacleAgent3 = b.createAgent();
                Pos obstaclePos3 = new PosImpl(0, 2);
                Agent obstacleAgent4 = b.createAgent();
                Pos obstaclePos4 = new PosImpl(2, 2);

                // CREATE STATE
                State state = new StateImpl(10, 10);
                state.addAgent(movingAgentPos, movingAgent);
                state.addAgent(obstaclePos, obstacleAgent);
                state.addAgent(obstaclePos2, obstacleAgent2);
                state.addAgent(obstaclePos3, obstacleAgent3);
                state.addAgent(obstaclePos4, obstacleAgent4);
                // TEST NOT VIEWED

                Method method = Class.forName("it.unibo.ares.core.agent.BoidsAgentFactory")
                                .getDeclaredMethod("collisionAvoindance", State.class, Pos.class, DirectionVector.class,
                                                Integer.class,
                                                Integer.class);

                method.setAccessible(true);
                DirectionVector newDir = (DirectionVectorImpl) method.invoke(
                                b, state, movingAgentPos, movingAgentDir,
                                radius, angle);
                DirectionVector expectedDir = new DirectionVectorImpl(0, 0);
                assertEquals(newDir, expectedDir.getNormalized());
        }

        @Test
        void testDirectionDirectionAligment()
                        throws NoSuchMethodException, SecurityException, ClassNotFoundException,
                        IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                BoidsAgentFactory b = new BoidsAgentFactory();
                Agent movingAgent = b.createAgent();
                Pos movingAgentPos = new PosImpl(0, 0);
                Integer radius = 3;
                Integer angle = 30;

                DirectionVector movinAgentDir = new DirectionVectorImpl(-1.0, 0.0);
                DirectionVector obstacleAgentsDir = new DirectionVectorImpl(1.0, 0.0);
                Agent obstacleAgent = b.createAgent();
                obstacleAgent.setParameter("direction", obstacleAgentsDir);
                Pos obstaclePos = new PosImpl(0, 1);
                Agent obstacleAgent2 = b.createAgent();
                Pos obstaclePos2 = new PosImpl(2, 1);
                obstacleAgent2.setParameter("direction", obstacleAgentsDir);

                // CREATE STATE
                State state = new StateImpl(10, 10);
                state.addAgent(movingAgentPos, movingAgent);
                state.addAgent(obstaclePos, obstacleAgent);
                state.addAgent(obstaclePos2, obstacleAgent2);
                // TEST NOT VIEWED

                Method method = Class.forName("it.unibo.ares.core.agent.BoidsAgentFactory")
                                .getDeclaredMethod("collisionAvoindance", State.class, Pos.class, DirectionVector.class,
                                                Integer.class,
                                                Integer.class);

                method.setAccessible(true);
                DirectionVector newDir = (DirectionVectorImpl) method.invoke(
                                b, state, movingAgentPos, movinAgentDir,
                                radius, angle);
                assertEquals(newDir, movinAgentDir.getNormalized());
        }

        @Test
        void testDirectionDirectionAligment2()
                        throws NoSuchMethodException, SecurityException, ClassNotFoundException,
                        IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                BoidsAgentFactory b = new BoidsAgentFactory();
                Agent movingAgent = b.createAgent();
                Pos movingAgentPos = new PosImpl(0, 0);
                Integer radius = 3;
                Integer angle = 180;

                DirectionVector movinAgentDir = new DirectionVectorImpl(-1.0, 0.0);
                DirectionVector obstacleAgentsDir = new DirectionVectorImpl(1.0, 0.0);
                Agent obstacleAgent = b.createAgent();
                obstacleAgent.setParameter("direction", obstacleAgentsDir);
                Pos obstaclePos = new PosImpl(0, 1);
                Agent obstacleAgent2 = b.createAgent();
                Pos obstaclePos2 = new PosImpl(2, 1);
                obstacleAgent2.setParameter("direction", obstacleAgentsDir);

                // CREATE STATE
                State state = new StateImpl(10, 10);
                state.addAgent(movingAgentPos, movingAgent);
                state.addAgent(obstaclePos, obstacleAgent);
                state.addAgent(obstaclePos2, obstacleAgent2);
                // TEST NOT VIEWED

                Method method = Class.forName("it.unibo.ares.core.agent.BoidsAgentFactory")
                                .getDeclaredMethod("directionAlignment", State.class, Pos.class, DirectionVector.class,
                                                Integer.class,
                                                Integer.class);
                method.setAccessible(true);
                DirectionVector newDir = (DirectionVectorImpl) method.invoke(
                                b, state, movingAgentPos, movinAgentDir,
                                radius, angle);
                assertEquals(newDir, obstacleAgentsDir.getNormalized());
        }

}
