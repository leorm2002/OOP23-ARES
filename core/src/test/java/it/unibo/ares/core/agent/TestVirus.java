package it.unibo.ares.core.agent;

import it.unibo.ares.core.utils.Pair;
import it.unibo.ares.core.utils.directionvector.DirectionVector;
import it.unibo.ares.core.utils.directionvector.DirectionVectorImpl;
import it.unibo.ares.core.utils.pos.Pos;
import it.unibo.ares.core.utils.pos.PosImpl;
import it.unibo.ares.core.utils.state.State;
import it.unibo.ares.core.utils.state.StateImpl;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class TestVirus {
    private VirusAgentFactory factory;
    private Pos initialPos;
    private DirectionVector dir;
    private State state;
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
    void testInfection() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException,
     NoSuchMethodException, SecurityException, ClassNotFoundException {
        factory = new VirusAgentFactory();
        initialPos = new PosImpl(0, 0);
        dir = new DirectionVectorImpl(1, 1);
        stepSize = 1;
        Method method = Class.forName("it.unibo.ares.core.agent.VirusAgentFactory")
                .getDeclaredMethod("tickFunction", State.class, Pos.class);

        method.setAccessible(true);
        Agent agentP = factory.createAgent(), agentI = factory.createAgent();
        agentP.setType("P");
        agentI.setType("I");
        agentI.setParameter("stepSize", 1);
        agentI.setParameter("direction", new DirectionVectorImpl(1, 1));
        agentP.setParameter("stepSize", 1);
        agentP.setParameter("direction", new DirectionVectorImpl(1, 1));
        state = new StateImpl(10, 10);
        state.addAgent(new PosImpl(0, 0), agentP);
        state.addAgent(new PosImpl(1, 1), agentI);
        state = (State) method.invoke(factory, state, new PosImpl(0, 0));
        assertTrue(state.getAgentAt(initialPos).get().getType().equals("I"));
    }
}
