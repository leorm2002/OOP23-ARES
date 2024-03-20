package it.unibo.ares.core.agent;

import it.unibo.ares.core.utils.directionvector.DirectionVectorImpl;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * This class tests the VirusAgentFactory class.
 */
class TestVirusModel {
    private IVirusAgentFactory factoryI;
    private PVirusAgentFactory factoryP;
    private static final String UNCHECKED = "unchecked";

    // CHECKSTYLE: MissingJavadocMethod OFF

    private Agent getPAgent(final int infectionRate) {
        factoryI = new IVirusAgentFactory();
        factoryP = new PVirusAgentFactory();
        final Agent agent = factoryP.createAgent();
        agent.setParameter("infectionRate", infectionRate);
        agent.setParameter("stepSize", 1);
        agent.setParameter("direction", new DirectionVectorImpl(0, 0));

        return agent;
    }

    private Agent getIAgent(final int recoveryRate) {
        factoryI = new IVirusAgentFactory();
        final Agent agent = factoryI.createAgent();
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
        final Agent agent = getPAgent(100);
        final Method method = Class.forName("it.unibo.ares.core.agent.PVirusAgentFactory")
                .getDeclaredMethod("infectPerson", Agent.class);
        method.setAccessible(true);

        @SuppressWarnings(UNCHECKED)
        final Optional<Agent> a = (Optional<Agent>) method.invoke(factoryP, agent);
        assertEquals("I", a.get().getType());
    }

    /*
     * This test checks if the recovery method works as expected.
     * It creates a healthy person and an infected person, and then it tries to
     * recover the infected person.
     * The infected person should recover and become healthy because of the 100%
     * recovery rate.
     */
    @Test
    void testRecovery() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException,
            NoSuchMethodException, SecurityException, ClassNotFoundException {
        // create two agents, one healthy and one infected
        // then try to recover the infected person whose recovery rate is 100%, so it
        // should become healthy
        final Agent agent = getIAgent(100);

        final Method method = Class.forName("it.unibo.ares.core.agent.IVirusAgentFactory")
                .getDeclaredMethod("recoveryInfected", Agent.class);
        method.setAccessible(true);

        @SuppressWarnings(UNCHECKED)
        final Optional<Agent> a = (Optional<Agent>) method.invoke(factoryI, agent);
        assertEquals("P", a.get().getType());
    }

    /*
     * This test checks if the recovery method works as expected.
     * It creates a healthy person and an infected person, and then it tries to
     * recover the infected person.
     * The infected person should not recover and become healthy because of the 0%
     * recovery rate.
     */
    @Test
    void testRecovery2() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException,
            NoSuchMethodException, SecurityException, ClassNotFoundException {
        // create two agents, one healthy and one infected
        // then try to recover the infected person whose recovery rate is 0%, so it
        // should not become healthy
        final Agent agent = getIAgent(0);

        final Method method = Class.forName("it.unibo.ares.core.agent.IVirusAgentFactory")
                .getDeclaredMethod("recoveryInfected", Agent.class);
        method.setAccessible(true);

        @SuppressWarnings(UNCHECKED)
        final Optional<Agent> a = (Optional<Agent>) method.invoke(factoryI, agent);
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
    void testInfection2() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException,
            NoSuchMethodException, SecurityException, ClassNotFoundException {
        // create two agents, one healthy and one infected
        // then try to infect the healthy person whose infection rate is 0%, so it
        // should not become infected
        final Agent agent = getPAgent(0);

        final Method method = Class.forName("it.unibo.ares.core.agent.PVirusAgentFactory")
                .getDeclaredMethod("infectPerson", Agent.class);
        method.setAccessible(true);

        @SuppressWarnings(UNCHECKED)
        final Optional<Agent> a = (Optional<Agent>) method.invoke(factoryP, agent);
        assertTrue(a.isEmpty());
    }
}
