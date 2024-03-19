package it.unibo.ares.core.agent;

import it.unibo.ares.core.utils.directionvector.DirectionVectorImpl;
import it.unibo.ares.core.utils.pos.Pos;
import it.unibo.ares.core.utils.pos.PosImpl;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * This class tests the VirusAgentFactory class.
 */
public class TestVirusModel {
    private IVirusAgentFactory factoryI;
    private Pos initialPos;
    private PVirusAgentFactory factoryP;

    // CHECKSTYLE: MissingJavadocMethod OFF

    private Agent getPAgent(final int infectionRate) {
        factoryI = new IVirusAgentFactory();
        factoryP = new PVirusAgentFactory();
        initialPos = new PosImpl(0, 0);
        Agent agent = factoryP.createAgent();
        agent.setParameter("infectionRate", infectionRate);
        agent.setParameter("stepSize", 1);
        agent.setParameter("direction", new DirectionVectorImpl(0, 0));

        return agent;
    }

    private Agent getIAgent(final int recoveryRate) {
        factoryI = new IVirusAgentFactory();
        Agent agent = factoryI.createAgent();
        initialPos = new PosImpl(0, 0);
        agent.setParameter("direction", new DirectionVectorImpl(0, 0));
        agent.setParameter("stepSize", 1);
        agent.setParameter("recoveryRate", recoveryRate);
        return agent;

    }

    /*
     * This test checks if the infection method works as expected.
     * It creates a healthy person and an infected person, and then it tries to
     * infect the healthy person.
     * The healthy person should become infected because of the 100% infection rate.
     */
    @Test
    void testInfection() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException,
            NoSuchMethodException, SecurityException, ClassNotFoundException {
        // create two agents, one healthy and one infected
        // then try to infect the healthy person whose infection rate is 100%, so it
        // should become infected
        Agent agent = getPAgent(100);
        Method method = Class.forName("it.unibo.ares.core.agent.PVirusAgentFactory")
                .getDeclaredMethod("infectPerson", Agent.class, Pos.class);
        method.setAccessible(true);

        @SuppressWarnings("unchecked")
        Optional<Agent> a = (Optional<Agent>) method.invoke(factoryP, agent, initialPos);
        assertTrue(a.get().getType().equals("I"));
    }

    /*
     * This test checks if the recovery method works as expected.
     * It creates a healthy person and an infected person, and then it tries to
     * recover the infected person.
     * The infected person should recover and become healthy because of the 100%
     * recovery rate.
     */
    @Test
    public void testRecovery() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException,
            NoSuchMethodException, SecurityException, ClassNotFoundException {
        // create two agents, one healthy and one infected
        // then try to recover the infected person whose recovery rate is 100%, so it
        // should become healthy
        Agent agent = getIAgent(100);

        Method method = Class.forName("it.unibo.ares.core.agent.IVirusAgentFactory")
                .getDeclaredMethod("recoveryInfected", Agent.class, Pos.class);
        method.setAccessible(true);

        @SuppressWarnings("unchecked")
        Optional<Agent> a = (Optional<Agent>) method.invoke(factoryI, agent, initialPos);
        assertTrue(a.get().getType().equals("P"));
    }

    /*
     * This test checks if the recovery method works as expected.
     * It creates a healthy person and an infected person, and then it tries to
     * recover the infected person.
     * The infected person should not recover and become healthy because of the 0%
     * recovery rate.
     */
    @Test
    public void testRecovery2() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException,
            NoSuchMethodException, SecurityException, ClassNotFoundException {
        // create two agents, one healthy and one infected
        // then try to recover the infected person whose recovery rate is 0%, so it
        // should not become healthy
        Agent agent = getIAgent(0);

        Method method = Class.forName("it.unibo.ares.core.agent.IVirusAgentFactory")
                .getDeclaredMethod("recoveryInfected", Agent.class, Pos.class);
        method.setAccessible(true);

        @SuppressWarnings("unchecked")
        Optional<Agent> a = (Optional<Agent>) method.invoke(factoryI, agent, initialPos);
        assertTrue(a.isEmpty());
    }

    /*
     * This test checks if the infection method works as expected.
     * It creates a healthy person and an infected person, and then it tries to
     * infect the healthy person.
     * The healthy person should not become infected because of the 0% infection
     * rate.
     */
    @Test
    public void testInfection2() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException,
            NoSuchMethodException, SecurityException, ClassNotFoundException {
        // create two agents, one healthy and one infected
        // then try to infect the healthy person whose infection rate is 0%, so it
        // should not become infected
        Agent agent = getPAgent(0);

        Method method = Class.forName("it.unibo.ares.core.agent.PVirusAgentFactory")
                .getDeclaredMethod("infectPerson", Agent.class, Pos.class);
        method.setAccessible(true);

        @SuppressWarnings("unchecked")
        Optional<Agent> a = (Optional<Agent>) method.invoke(factoryP, agent, initialPos);
        assertTrue(a.isEmpty());
    }
}
