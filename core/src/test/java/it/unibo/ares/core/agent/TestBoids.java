package it.unibo.ares.core.agent;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import org.junit.jupiter.api.Test;

import it.unibo.ares.core.utils.directionvector.DirectionVector;
import it.unibo.ares.core.utils.directionvector.DirectionVectorImpl;
import it.unibo.ares.core.utils.pos.Pos;
import it.unibo.ares.core.utils.pos.PosImpl;
import it.unibo.ares.core.utils.state.State;
import it.unibo.ares.core.utils.state.StateImpl;

class TestBoids {

    @Test
    void testCollisionAvoidanceNotOutOfScope()
            throws NoSuchMethodException, SecurityException, ClassNotFoundException,
            IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        final BoidsAgentFactory b = new BoidsAgentFactory();
        final Agent movingAgent = b.createAgent();
        final Pos movingAgentPos = new PosImpl(0, 0);
        final DirectionVector movingAgentDir = new DirectionVectorImpl(1.0, 0.0);
        final Integer radius = 3;
        final Integer angle = 90;

        final Agent obstacleAgent = b.createAgent();
        final Pos obstaclePos = new PosImpl(4, 0);
        // CREATE STATE
        final State state = new StateImpl(10, 10);
        state.addAgent(movingAgentPos, movingAgent);
        state.addAgent(obstaclePos, obstacleAgent);
        // TEST NOT VIEWED

        final Method method = Class.forName("it.unibo.ares.core.agent.BoidsAgentFactory")
                .getDeclaredMethod("collisionAvoindance", State.class, Pos.class, DirectionVector.class,
                        Integer.class,
                        Integer.class);

        method.setAccessible(true);

        final DirectionVector newDir = (DirectionVectorImpl) method.invoke(b, state, movingAgentPos, movingAgentDir,
                radius,
                angle);
        assertEquals(newDir, movingAgentDir);
    }

    @Test
    void testCollisionAvoidance()
            throws NoSuchMethodException, SecurityException, ClassNotFoundException,
            IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        final BoidsAgentFactory b = new BoidsAgentFactory();
        final Agent movingAgent = b.createAgent();
        final Pos movingAgentPos = new PosImpl(0, 0);
        final DirectionVector movingAgentDir = new DirectionVectorImpl(1.0, 0.0);
        final Integer radius = 3;
        final Integer angle = 90;

        final Agent obstacleAgent = b.createAgent();
        final Pos obstaclePos = new PosImpl(1, 0);
        // CREATE STATE
        final State state = new StateImpl(10, 10);
        state.addAgent(movingAgentPos, movingAgent);
        state.addAgent(obstaclePos, obstacleAgent);
        // TEST NOT VIEWED

        final Method method = Class.forName("it.unibo.ares.core.agent.BoidsAgentFactory")
                .getDeclaredMethod("collisionAvoindance", State.class, Pos.class, DirectionVector.class,
                        Integer.class,
                        Integer.class);

        method.setAccessible(true);

        final DirectionVector newDir = (DirectionVectorImpl) method.invoke(b, state, movingAgentPos, movingAgentDir,
                radius,
                angle);
        final DirectionVector expectedDir = new DirectionVectorImpl(-1.0, 0.0);
        assertEquals(newDir, expectedDir);
    }

    @Test
    void testCollisionAvoidanceWithTwoObstacles()
            throws NoSuchMethodException, SecurityException, ClassNotFoundException,
            IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        final BoidsAgentFactory b = new BoidsAgentFactory();
        final Agent movingAgent = b.createAgent();
        final Pos movingAgentPos = new PosImpl(0, 0);
        final DirectionVector movingAgentDir = new DirectionVectorImpl(1.0, 0.0);
        final Integer radius = 3;
        final Integer angle = 90;

        final Agent obstacleAgent = b.createAgent();
        final Pos obstaclePos = new PosImpl(1, 0);
        final Agent obstacleAgent2 = b.createAgent();
        final Pos obstaclePos2 = new PosImpl(1, 1);

        // CREATE STATE
        final State state = new StateImpl(10, 10);
        state.addAgent(movingAgentPos, movingAgent);
        state.addAgent(obstaclePos, obstacleAgent);
        state.addAgent(obstaclePos2, obstacleAgent2);
        // TEST NOT VIEWED

        final Method method = Class.forName("it.unibo.ares.core.agent.BoidsAgentFactory")
                .getDeclaredMethod("collisionAvoindance", State.class, Pos.class, DirectionVector.class,
                        Integer.class,
                        Integer.class);
        method.setAccessible(true);
        final DirectionVector newDir = (DirectionVectorImpl) method.invoke(
                b, state, movingAgentPos, movingAgentDir,
                radius,
                angle);
        // CHECKSTYLE: MagicNumber OFF sono i risultati attesi
        final DirectionVector expectedDir = new DirectionVectorImpl(-2.0, -1.0);
        // CHECKSTYLE: MagicNumber ON
        assertEquals(newDir, expectedDir.getNormalized());
    }

    @Test
    void testDirectionCenterCohesion()
            throws NoSuchMethodException, SecurityException, ClassNotFoundException,
            IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        final BoidsAgentFactory b = new BoidsAgentFactory();
        final Agent movingAgent = b.createAgent();
        final Pos movingAgentPos = new PosImpl(0, 0);
        final DirectionVector movingAgentDir = new DirectionVectorImpl(1.0, 0.0);
        final Integer radius = 3;
        final Integer angle = 90;

        final Agent obstacleAgent = b.createAgent();
        final Pos obstaclePos = new PosImpl(0, 1);
        final Agent obstacleAgent2 = b.createAgent();
        final Pos obstaclePos2 = new PosImpl(2, 1);

        // CREATE STATE
        final State state = new StateImpl(10, 10);
        state.addAgent(movingAgentPos, movingAgent);
        state.addAgent(obstaclePos, obstacleAgent);
        state.addAgent(obstaclePos2, obstacleAgent2);
        // TEST NOT VIEWED

        final Method method = Class.forName("it.unibo.ares.core.agent.BoidsAgentFactory")
                .getDeclaredMethod("centerCohesion", State.class, Pos.class, DirectionVector.class,
                        Integer.class,
                        Integer.class);

        method.setAccessible(true);
        final DirectionVector newDir = (DirectionVectorImpl) method.invoke(
                b, state, movingAgentPos, movingAgentDir,
                radius, angle);
        final DirectionVector expectedDir = new DirectionVectorImpl(1, 1);
        assertEquals(newDir, expectedDir.getNormalized());
    }

    @Test
    void testDirectionCenterCohesionRemainingStill()
            throws NoSuchMethodException, SecurityException, ClassNotFoundException,
            IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        final BoidsAgentFactory b = new BoidsAgentFactory();
        final Agent movingAgent = b.createAgent();
        final Pos movingAgentPos = new PosImpl(1, 1);
        final DirectionVector movingAgentDir = new DirectionVectorImpl(1.0, 0.0);
        final Integer radius = 3;
        final Integer angle = 180;

        final Agent obstacleAgent = b.createAgent();
        final Pos obstaclePos = new PosImpl(0, 0);
        final Agent obstacleAgent2 = b.createAgent();
        final Pos obstaclePos2 = new PosImpl(2, 0);
        final Agent obstacleAgent3 = b.createAgent();
        final Pos obstaclePos3 = new PosImpl(0, 2);
        final Agent obstacleAgent4 = b.createAgent();
        final Pos obstaclePos4 = new PosImpl(2, 2);

        // CREATE STATE
        final State state = new StateImpl(10, 10);
        state.addAgent(movingAgentPos, movingAgent);
        state.addAgent(obstaclePos, obstacleAgent);
        state.addAgent(obstaclePos2, obstacleAgent2);
        state.addAgent(obstaclePos3, obstacleAgent3);
        state.addAgent(obstaclePos4, obstacleAgent4);
        // TEST NOT VIEWED

        final Method method = Class.forName("it.unibo.ares.core.agent.BoidsAgentFactory")
                .getDeclaredMethod("collisionAvoindance", State.class, Pos.class, DirectionVector.class,
                        Integer.class,
                        Integer.class);

        method.setAccessible(true);
        final DirectionVector newDir = (DirectionVectorImpl) method.invoke(
                b, state, movingAgentPos, movingAgentDir,
                radius, angle);
        final DirectionVector expectedDir = new DirectionVectorImpl(0, 0);
        assertEquals(newDir, expectedDir.getNormalized());
    }

    @Test
    void testDirectionDirectionAligment()
            throws NoSuchMethodException, SecurityException, ClassNotFoundException,
            IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        final BoidsAgentFactory b = new BoidsAgentFactory();
        final Agent movingAgent = b.createAgent();
        final Pos movingAgentPos = new PosImpl(0, 0);
        final Integer radius = 3;
        // CHECKSTYLE: MagicNumber OFF angolo per testare, in base a questo valore si
        // calcola la direzione
        final Integer angle = 30;
        // CHECKSTYLE: MagicNumber OFF just the dimension of the state, not important

        final DirectionVector movinAgentDir = new DirectionVectorImpl(-1.0, 0.0);
        final DirectionVector obstacleAgentsDir = new DirectionVectorImpl(1.0, 0.0);
        final Agent obstacleAgent = b.createAgent();
        obstacleAgent.setParameter("direction", obstacleAgentsDir);
        final Pos obstaclePos = new PosImpl(0, 1);
        final Agent obstacleAgent2 = b.createAgent();
        final Pos obstaclePos2 = new PosImpl(2, 1);
        obstacleAgent2.setParameter("direction", obstacleAgentsDir);

        // CREATE STATE
        State state = new StateImpl(10, 10);
        state.addAgent(movingAgentPos, movingAgent);
        state.addAgent(obstaclePos, obstacleAgent);
        state.addAgent(obstaclePos2, obstacleAgent2);
        // TEST NOT VIEWED

        final Method method = Class.forName("it.unibo.ares.core.agent.BoidsAgentFactory")
                .getDeclaredMethod("collisionAvoindance", State.class, Pos.class, DirectionVector.class,
                        Integer.class,
                        Integer.class);

        method.setAccessible(true);
        DirectionVector newDir = (DirectionVectorImpl) method.invoke(
                b, state, movingAgentPos, movinAgentDir,
                radius, angle);
        assertEquals(newDir, movinAgentDir.getNormalized());

        // CREATE STATE
        state = new StateImpl(10, 10);
        state.addAgent(movingAgentPos, movingAgent);
        state.addAgent(obstaclePos, obstacleAgent);
        state.addAgent(obstaclePos2, obstacleAgent2);
        // TEST NOT VIEWED

        newDir = (DirectionVectorImpl) method.invoke(
                b, state, movingAgentPos, movinAgentDir,
                radius, angle);
        assertEquals(newDir, obstacleAgentsDir.getNormalized());
    }

}
