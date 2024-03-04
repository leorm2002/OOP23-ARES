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
import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * This class tests the VirusAgentFactory class.
 */
public class TestVirus {
    private VirusAgentFactory factory;
    private Pos initialPos;
    private DirectionVector dir;
    private int stepSize;

    // CHECKSTYLE: MissingJavadocMethod OFF


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

        // The new position should be (1, 1) because the direction vector is (1, 1) and the step size is 1,
        // starting from (0, 0)
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

        // The new position should be (4, 4) because the new position is (6, 6) 
        // and it's out of bound because the grid is 5x5, (maxPosX - 1, maxPosY - 1)
        Pos limitedPos = (Pos) method.invoke(factory, new PosImpl(outOfBoundPos, outOfBoundPos), new Pair<>(maxPos, maxPos));
        assertEquals(new PosImpl(espectedPos, espectedPos), limitedPos);
    }

    /*
     * This test checks if the infection method works as expected.
     * It creates a healthy person and an infected person, and then it tries to infect the healthy person.
     * The healthy person should become infected because of the 100% infection rate.
     * It creates also an infected person to allow the factory to have setted the parameters for the infect persons
     * when the change of agent occurs.
     */
    @Test
    public void testInfection() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException,
            NoSuchMethodException, SecurityException, ClassNotFoundException {
        // create two agents, one healthy and one infected
        // then try to infect the healthy person whose infection rate is 100%, so it should become infected
        factory = new VirusAgentFactory();
        initialPos = new PosImpl(0, 0);
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

        Method method = Class.forName("it.unibo.ares.core.agent.VirusAgentFactory")
                .getDeclaredMethod("initializeParameters", Set.class);
        method.setAccessible(true);
        method.invoke(factory, Set.of(new Pair<Pos, Agent>(initialPos, agentP),
                new Pair<Pos, Agent>(new PosImpl(1, 1), agentI)));
        method = Class.forName("it.unibo.ares.core.agent.VirusAgentFactory")
                .getDeclaredMethod("infectPerson", Agent.class, Pos.class);
        method.setAccessible(true);

        @SuppressWarnings("unchecked")
        Optional<Agent> a = (Optional<Agent>) method.invoke(factory, agentP, initialPos);
        assertTrue(a.get().getType().equals("I"));
    }


    /*
     * This test checks if the recovery method works as expected.
     * It creates a healthy person and an infected person, and then it tries to recover the infected person.
     * The infected person should recover and become healthy because of the 100% recovery rate.
     * It creates also a healthy person to allow the factory to have setted the parameters for the healthy person
     * when the change of agent occurs.
     */
    @Test
    public void testRecovery() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException,
            NoSuchMethodException, SecurityException, ClassNotFoundException {
        // create two agents, one healthy and one infected
        // then try to recover the infected person whose recovery rate is 100%, so it should become healthy
        factory = new VirusAgentFactory();
        initialPos = new PosImpl(0, 0);
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

        Method method = Class.forName("it.unibo.ares.core.agent.VirusAgentFactory")
                .getDeclaredMethod("initializeParameters", Set.class);
        method.setAccessible(true);
        method.invoke(factory, Set.of(new Pair<Pos, Agent>(initialPos, agentI),
                new Pair<Pos, Agent>(new PosImpl(1, 1), agentP)));
        method = Class.forName("it.unibo.ares.core.agent.VirusAgentFactory")
                .getDeclaredMethod("recoveryInfected", Agent.class, Pos.class);
        method.setAccessible(true);

        @SuppressWarnings("unchecked")
        Optional<Agent> a = (Optional<Agent>) method.invoke(factory, agentI, initialPos);
        assertTrue(a.get().getType().equals("P"));
    }

    /*
     * This test checks if the recovery method works as expected.
     * It creates a healthy person and an infected person, and then it tries to
     * recover the infected person.
     * The infected person should not recover and become healthy because of the 0%
     * recovery rate.
     * It creates also a healthy person to allow the factory to have setted the
     * parameters for the healthy persons
     * when the change of agent occurs.
     */
    @Test
    public void testRecovery2() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException,
            NoSuchMethodException, SecurityException, ClassNotFoundException {
        // create two agents, one healthy and one infected
        // then try to recover the infected person whose recovery rate is 0%, so it should not become healthy
        factory = new VirusAgentFactory();
        initialPos = new PosImpl(0, 0);
        factory.setTypeOfAgent('P');
        Agent agentP = factory.createAgent();
        factory.setTypeOfAgent('I');
        Agent agentI = factory.createAgent();
        agentP.setType("P");
        agentP.setParameter("infectionRate", 0);
        agentI.setParameter("direction", new DirectionVectorImpl(0, 0));
        agentI.setType("I");
        agentI.setParameter("stepSize", 1);
        agentI.setParameter("recoveryRate", 0);
        agentP.setParameter("stepSize", 1);
        agentP.setParameter("direction", new DirectionVectorImpl(0, 0));

        Method method = Class.forName("it.unibo.ares.core.agent.VirusAgentFactory")
                .getDeclaredMethod("initializeParameters", Set.class);
        method.setAccessible(true);
        method.invoke(factory, Set.of(new Pair<Pos, Agent>(initialPos, agentI),
                new Pair<Pos, Agent>(new PosImpl(1, 1), agentP)));
        method = Class.forName("it.unibo.ares.core.agent.VirusAgentFactory")
                .getDeclaredMethod("recoveryInfected", Agent.class, Pos.class);
        method.setAccessible(true);

        @SuppressWarnings("unchecked")
        Optional<Agent> a = (Optional<Agent>) method.invoke(factory, agentI, initialPos);
        assertTrue(a.isEmpty());
    }

    /*
     * This test checks if the infection method works as expected.
     * It creates a healthy person and an infected person, and then it tries to
     * infect the healthy person.
     * The healthy person should not become infected because of the 0% infection
     * rate.
     * It creates also an infected person to allow the factory to have setted the
     * parameters for the infect persons
     * when the change of agent occurs.
     */
    @Test
    public void testInfection2() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException,
            NoSuchMethodException, SecurityException, ClassNotFoundException {
        // create two agents, one healthy and one infected
        // then try to infect the healthy person whose infection rate is 0%, so it should not become infected
        factory = new VirusAgentFactory();
        initialPos = new PosImpl(0, 0);
        factory.setTypeOfAgent('P');
        Agent agentP = factory.createAgent();
        factory.setTypeOfAgent('I');
        Agent agentI = factory.createAgent();
        agentP.setType("P");
        agentP.setParameter("infectionRate", 0);
        agentI.setParameter("direction", new DirectionVectorImpl(0, 0));
        agentI.setType("I");
        agentI.setParameter("stepSize", 1);
        agentI.setParameter("recoveryRate", 0);
        agentP.setParameter("stepSize", 1);
        agentP.setParameter("direction", new DirectionVectorImpl(0, 0));

        Method method = Class.forName("it.unibo.ares.core.agent.VirusAgentFactory")
                .getDeclaredMethod("initializeParameters", Set.class);
        method.setAccessible(true);
        method.invoke(factory, Set.of(new Pair<Pos, Agent>(initialPos, agentP),
                new Pair<Pos, Agent>(new PosImpl(1, 1), agentI)));
        method = Class.forName("it.unibo.ares.core.agent.VirusAgentFactory")
                .getDeclaredMethod("infectPerson", Agent.class, Pos.class);
        method.setAccessible(true);

        @SuppressWarnings("unchecked")
        Optional<Agent> a = (Optional<Agent>) method.invoke(factory, agentP, initialPos);
        assertTrue(a.isEmpty());
    }
}
