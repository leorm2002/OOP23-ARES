package it.unibo.ares.core.agent;

import it.unibo.ares.core.utils.Pair;
import it.unibo.ares.core.utils.directionvector.DirectionVector;
import it.unibo.ares.core.utils.directionvector.DirectionVectorImpl;
import it.unibo.ares.core.utils.pos.Pos;
import it.unibo.ares.core.utils.pos.PosImpl;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestVirus {
    private VirusAgentFactory factory;
    private Pos initialPos;
    private DirectionVector dir;
    private int stepSize;

    /*
     * This test checks if the move method works as expected.
     * It creates a new agent and then it tries to move it.
     * The agent should move to the new position.
     */
    @Test
    void testMove() throws NoSuchMethodException, SecurityException,
            ClassNotFoundException, IllegalAccessException, IllegalArgumentException,
            InvocationTargetException {
        factory = new VirusAgentFactory();
        initialPos = new PosImpl(0, 0);
        dir = new DirectionVectorImpl(1, 1);
        stepSize = 1;
        Method method = Class.forName("it.unibo.ares.core.agent.VirusAgentFactory")
                                .getDeclaredMethod("move", Pos.class, DirectionVector.class,
                                                Integer.class);

        method.setAccessible(true);
        Pos newPos = (Pos) method.invoke(factory, initialPos, dir, stepSize);
        assertEquals(new PosImpl(1, 1), newPos);
    }

    /*
     * This test checks if the limit method works as expected.
     * If the new position is out of the grid, the limit method should limit the position to the grid
     * and return the new position (maxPosX - 1, maxPosY - 1).
     */
    @Test
    void testPosLimit() throws NoSuchMethodException, SecurityException, ClassNotFoundException,
     IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        factory = new VirusAgentFactory();
        final int outOfBoundPos = 6, maxPos = 5, espectedPos = 4;
        Method method = Class.forName("it.unibo.ares.core.agent.VirusAgentFactory")
                .getDeclaredMethod("limit", Pos.class, Pair.class);

        method.setAccessible(true);
        Pos limitedPos = (Pos) method.invoke(factory, new PosImpl(outOfBoundPos, outOfBoundPos), new Pair<>(maxPos, maxPos));
        assertEquals(new PosImpl(espectedPos, espectedPos), limitedPos);
    }

    /*
     * This test checks if the infection method works as expected.
     * It creates a healthy person and an infected person, and then it tries to infect the healthy person.
     * The healthy person should become infected.
     * It creates also an infected person to allow the factory to have setted the parameters for the infected person
     * when the change of agent occurs.
     */
    @Test
    public void testInfection() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException,
            NoSuchMethodException, SecurityException, ClassNotFoundException {
        factory = new VirusAgentFactory();
        initialPos = new PosImpl(0, 0);
        Method method = Class.forName("it.unibo.ares.core.agent.VirusAgentFactory")
                .getDeclaredMethod("infectPerson", Agent.class, Pos.class);

        method.setAccessible(true);
        factory.setTypeOfAgent('P');
        Agent agentP = factory.createAgent();
        factory.setTypeOfAgent('I');
        Agent agentI = factory.createAgent();
        agentP.setType("P");
        agentP.setParameter("infectionRate", 100);
        agentI.setParameter("direction", new DirectionVectorImpl(0, 0));
        agentI.setType("I");
        agentI.setParameter("stepSize", 1);
        agentI.setParameter("recoveryRate", 0);
        agentP.setParameter("stepSize", 1);
        agentP.setParameter("direction", new DirectionVectorImpl(0, 0));
        @SuppressWarnings("unchecked")
        Optional<Agent> a = (Optional<Agent>) method.invoke(factory, agentP, initialPos);
        assertTrue(a.get().getType().equals("I"));
    }


    /*
     * This test checks if the recovery method works as expected.
     * It creates a healthy person and an infected person, and then it tries to recover the infected person.
     * The infected person should recover and become healthy.
     * It creates also a healthy person to allow the factory to have setted the parameters for the healthy person
     * when the change of agent occurs.
     */
    @Test
    public void testRecovery() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException,
            NoSuchMethodException, SecurityException, ClassNotFoundException {
        factory = new VirusAgentFactory();
        initialPos = new PosImpl(0, 0);
        Method method = Class.forName("it.unibo.ares.core.agent.VirusAgentFactory")
                .getDeclaredMethod("recoveryInfected", Agent.class, Pos.class);

        method.setAccessible(true);
        factory.setTypeOfAgent('P');
        Agent agentP = factory.createAgent();
        factory.setTypeOfAgent('I');
        Agent agentI = factory.createAgent();
        agentP.setType("P");
        agentP.setParameter("infectionRate", 0);
        agentI.setParameter("direction", new DirectionVectorImpl(0, 0));
        agentI.setType("I");
        agentI.setParameter("stepSize", 1);
        agentI.setParameter("recoveryRate", 100);
        agentP.setParameter("stepSize", 1);
        agentP.setParameter("direction", new DirectionVectorImpl(0, 0));
        @SuppressWarnings("unchecked")
        Optional<Agent> a = (Optional<Agent>) method.invoke(factory, agentI, initialPos);
        assertTrue(a.get().getType().equals("P"));
    }
}
