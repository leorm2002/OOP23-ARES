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

    @Test
    void testPosLimit() throws NoSuchMethodException, SecurityException, ClassNotFoundException,
     IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        factory = new VirusAgentFactory();
        Method method = Class.forName("it.unibo.ares.core.agent.VirusAgentFactory")
                .getDeclaredMethod("limit", Pos.class, Pair.class);

        method.setAccessible(true);
        Pos limitedPos = (Pos) method.invoke(factory, new PosImpl(10, 10), new Pair<>(5, 5));
        assertEquals(new PosImpl(4, 4), limitedPos);
    }

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
