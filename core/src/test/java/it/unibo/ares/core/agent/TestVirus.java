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
    private IVirusAgentFactory factoryI;
    private Pos initialPos;
    private DirectionVector dir;
    private int stepSize;
    private PVirusAgentFactory factoryP;

    // CHECKSTYLE: MissingJavadocMethod OFF

    private Pair<Agent, Agent> getTwoAgents(int infectionRate) {
        factoryI = new IVirusAgentFactory();
        factoryP = new PVirusAgentFactory();
        initialPos = new PosImpl(0, 0);
        Agent agentP = factoryP.createAgent();
        Agent agentI = factoryI.createAgent();
        agentP.setParameter("infectionRate", infectionRate);
        agentI.setParameter("direction", new DirectionVectorImpl(0, 0));
        agentI.setParameter("stepSize", 1);
        agentI.setParameter("recoveryRate", 0);
        agentP.setParameter("stepSize", 1);
        agentP.setParameter("direction", new DirectionVectorImpl(0, 0));

        return new Pair<Agent, Agent>(agentP, agentI);
    }

    /*
     * This test checks if the infection method works as expected.
     * It creates a healthy person and an infected person, and then it tries to
     * infect the healthy person.
     * The healthy person should become infected because of the 100% infection rate.
     * It creates also an infected person to allow the factory to have setted the
     * parameters for the infect persons
     * when the change of agent occurs.
     */
    @Test
    void testInfection() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException,
            NoSuchMethodException, SecurityException, ClassNotFoundException {
        // create two agents, one healthy and one infected
        // then try to infect the healthy person whose infection rate is 100%, so it
        // should become infected
        Pair<Agent, Agent> agents = getTwoAgents(100);
        Method method = Class.forName("it.unibo.ares.core.agent.PVirusAgentFactory")
                .getDeclaredMethod("initializeParameters", Set.class);
        method.setAccessible(true);
        method.invoke(factoryP, Set.of(new Pair<Pos, Agent>(initialPos, agents.getFirst()),
                new Pair<Pos, Agent>(new PosImpl(1, 1), agents.getSecond())));
        method = Class.forName("it.unibo.ares.core.agent.PVirusAgentFactory")
                .getDeclaredMethod("infectPerson", Agent.class, Pos.class);
        method.setAccessible(true);

        @SuppressWarnings("unchecked")
        Optional<Agent> a = (Optional<Agent>) method.invoke(factoryP, agents.getFirst(), initialPos);
        assertTrue(a.get().getType().equals("I"));
    }

    /*
     * This test checks if the recovery method works as expected.
     * It creates a healthy person and an infected person, and then it tries to
     * recover the infected person.
     * The infected person should recover and become healthy because of the 100%
     * recovery rate.
     * It creates also a healthy person to allow the factory to have setted the
     * parameters for the healthy person
     * when the change of agent occurs.
     */
    @Test
    public void testRecovery() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException,
            NoSuchMethodException, SecurityException, ClassNotFoundException {
        // create two agents, one healthy and one infected
        // then try to recover the infected person whose recovery rate is 100%, so it
        // should become healthy
        factoryP = new PVirusAgentFactory();
        factoryI = new IVirusAgentFactory();
        initialPos = new PosImpl(0, 0);
        Agent agentP = factoryP.createAgent();
        Agent agentI = factoryI.createAgent();
        agentP.setParameter("infectionRate", 0);
        agentI.setParameter("direction", new DirectionVectorImpl(0, 0));
        agentI.setParameter("stepSize", 1);
        agentI.setParameter("recoveryRate", 100);
        agentP.setParameter("stepSize", 1);
        agentP.setParameter("direction", new DirectionVectorImpl(0, 0));

        Method method = Class.forName("it.unibo.ares.core.agent.IVirusAgentFactory")
                .getDeclaredMethod("initializeParameters", Set.class);
        method.setAccessible(true);
        method.invoke(factoryI, Set.of(new Pair<Pos, Agent>(initialPos, agentP),
                new Pair<Pos, Agent>(new PosImpl(1, 1), agentI)));
        method = Class.forName("it.unibo.ares.core.agent.IVirusAgentFactory")
                .getDeclaredMethod("recoveryInfected", Agent.class, Pos.class);
        method.setAccessible(true);

        @SuppressWarnings("unchecked")
        Optional<Agent> a = (Optional<Agent>) method.invoke(factoryI, agentI, initialPos);
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
        // then try to recover the infected person whose recovery rate is 0%, so it
        // should not become healthy
        Pair<Agent, Agent> agents = getTwoAgents(0);

        Method method = Class.forName("it.unibo.ares.core.agent.IVirusAgentFactory")
                .getDeclaredMethod("initializeParameters", Set.class);
        method.setAccessible(true);
        method.invoke(factoryI, Set.of(new Pair<Pos, Agent>(initialPos, agents.getFirst()),
                new Pair<Pos, Agent>(new PosImpl(1, 1), agents.getSecond())));
        method = Class.forName("it.unibo.ares.core.agent.IVirusAgentFactory")
                .getDeclaredMethod("recoveryInfected", Agent.class, Pos.class);
        method.setAccessible(true);

        @SuppressWarnings("unchecked")
        Optional<Agent> a = (Optional<Agent>) method.invoke(factoryI, agents.getSecond(), initialPos);
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
        // then try to infect the healthy person whose infection rate is 0%, so it
        // should not become infected
        Pair<Agent, Agent> agents = getTwoAgents(0);

        Method method = Class.forName("it.unibo.ares.core.agent.PVirusAgentFactory")
                .getDeclaredMethod("initializeParameters", Set.class);
        method.setAccessible(true);
        method.invoke(factoryP, Set.of(new Pair<Pos, Agent>(initialPos, agents.getFirst()),
                new Pair<Pos, Agent>(new PosImpl(1, 1), agents.getSecond())));
        method = Class.forName("it.unibo.ares.core.agent.PVirusAgentFactory")
                .getDeclaredMethod("infectPerson", Agent.class, Pos.class);
        method.setAccessible(true);

        @SuppressWarnings("unchecked")
        Optional<Agent> a = (Optional<Agent>) method.invoke(factoryP, agents.getFirst(), initialPos);
        assertTrue(a.isEmpty());
    }
}
