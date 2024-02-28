package it.unibo.ares.core.agent;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import it.unibo.ares.core.utils.Pair;
import it.unibo.ares.core.utils.directionvector.DirectionVector;
import it.unibo.ares.core.utils.directionvector.DirectionVectorImpl;
import it.unibo.ares.core.utils.pos.Pos;
import it.unibo.ares.core.utils.pos.PosImpl;
import it.unibo.ares.core.utils.state.State;
import it.unibo.ares.core.utils.state.StateImpl;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class TestVirus {
    private VirusAgentFactory factory;
    private Pos initialPos;
    private DirectionVector dir;
    private State state;
    private int stepSize;

    @Test
    public void testMove() throws NoSuchMethodException, SecurityException,
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
    public void testPosLimit() throws NoSuchMethodException, SecurityException, ClassNotFoundException, IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        factory = new VirusAgentFactory();
        Method method = Class.forName("it.unibo.ares.core.agent.VirusAgentFactory")
                .getDeclaredMethod("limit", Pos.class, Pair.class);

        method.setAccessible(true);
        Pos limitedPos = (Pos) method.invoke(factory, new PosImpl(10, 10), new Pair<>(5, 5));
        assertEquals(new PosImpl(4, 4), limitedPos);
    }
/*
    @Test
    public void testInfection() {
        factory = new VirusAgentFactory();
        initialPos = new PosImpl(0, 0);
        dir = new DirectionVectorImpl(1, 1);
        stepSize = 1;
        Agent agentP = factory.createAgent(), agentI = factory.createAgent();
        agentP.setType("P");
        agentI.setType("I");
        State state = new StateImpl(10, 10);
        state.addAgent(new PosImpl(0, 0), agentP);
        state.addAgent(new PosImpl(1, 1), agentI);
        state = factory.tickFunction(state, new PosImpl(0, 0));
        Pos p = factory.move(initialPos, dir, stepSize);
        assertTrue(state.getAgentAt(p).get().getType().equals("Infected"));
    }*/

}