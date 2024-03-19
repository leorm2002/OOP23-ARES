package it.unibo.ares.core.agent;

import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.function.BiPredicate;

import it.unibo.ares.core.utils.parameters.ParameterImpl;
import it.unibo.ares.core.utils.ComputationUtils;
import it.unibo.ares.core.utils.Pair;
import it.unibo.ares.core.utils.directionvector.DirectionVector;
import it.unibo.ares.core.utils.directionvector.DirectionVectorImpl;
import it.unibo.ares.core.utils.parameters.ParameterDomainImpl;
import it.unibo.ares.core.utils.pos.Pos;
import it.unibo.ares.core.utils.state.State;

/**
 * A factory class for creating P agents for the virus model.
 */
public final class PVirusAgentFactory implements AgentFactory {

    private Random r;
    // PARAMETRI DA SETTARE, SETTATI A VALORI DI DEFAULT
    // Utilizzati per settare i parametri dell'agente infetto
    private int stepSizeI = 1;
    private int infectionRate = 0;
    private int recoveryRate = 0;
    private boolean paramSected = false;

    /**
     * Constructor for the PVirusAgentFactory.
     */
    public PVirusAgentFactory() {
        r = new Random();
    }

    /*
     * A predicate to check if two agents are of the same type.
     */
    private static BiPredicate<Agent, Agent> isAgentOfSameType = (a, b) -> {
        String typeA = a.getType();
        String typeB = b.getType();
        return typeA.equals(typeB);
    };

    /**
     * Initializes the parameters for the agents.
     * Useful for the first tick, so the factory can set the parameters for the
     * agents
     * when they are created on runtime.
     *
     * @param agents The set of agents.
     */
    private void initializeParameters(final Set<Pair<Pos, Agent>> agents) {
        boolean infSected = false;
        boolean persSected = false;
        // cerco i parametri per l'agente infetto e per l'agente sano
        // una volta settati i parametri per ciascun tipo di agente, break
        for (Pair<Pos, Agent> pair : agents) {
            switch (pair.getSecond().getType()) {
                case "P":
                    infectionRate = pair.getSecond().getParameters()
                            .getParameter("infectionRate", Integer.class)
                            .get().getValue();
                    persSected = true;
                    break;
                case "I":
                    stepSizeI = pair.getSecond().getParameters()
                            .getParameter("stepSize", Integer.class)
                            .get().getValue();
                    recoveryRate = pair.getSecond().getParameters()
                            .getParameter("recoveryRate", Integer.class)
                            .get().getValue();
                    infSected = true;
                    break;
                default:
                    break;
            }
            if (persSected && infSected) {
                paramSected = true;
                break;
            }
        }
    }

    /**
     * Updates the state of the agent based on its current state and position.
     * The agent can move in the environment and get infected by other agents.
     * At every tick the agents can move or get infected.
     * Not two actions at the same tick.
     *
     * @param currentState  The current state of the agent.
     * @param agentPosition The position of the agent.
     * @return The updated state of the agent.
     */
    private State tickFunction(final State currentState, final Pos agentPosition) {
        // se i parametri della classe non sono stati settati, li setto
        if (!paramSected) {
            initializeParameters(currentState.getAgents());
        }
        if (!currentState.getAgentAt(agentPosition).isPresent()) {
            return currentState;
        }
        Agent agent = currentState.getAgentAt(agentPosition).get();
        if (!agent.getParameters().getParametersToset().isEmpty()) {
            throw new RuntimeException("Parameters not set");
        }

        int stepSize = agent.getParameters().getParameter("stepSize", Integer.class)
                .get().getValue();
        // assegno una nuova direzione casuale ad ogni step
        DirectionVector dir = ComputationUtils.getRandomDirection(r);
        currentState.getAgentAt(agentPosition).get().setParameter("direction", dir);
        Pos newPos = ComputationUtils.move(agentPosition, dir, stepSize);
        if (!currentState.isInside(newPos)) {
            // se la nuova posizione dell'agente sarebbe fuori dallo spazio, cambio
            // direzione
            dir = new DirectionVectorImpl(-dir.getX(), -dir.getY());
            newPos = ComputationUtils.limit(
                    ComputationUtils.move(agentPosition, dir, stepSize), currentState.getDimensions());
        }
        if (!currentState.isFree(newPos)) {
            // se la nuova posizione è occupata da due agenti di tipo diverso, controllo se
            // avviene un'infezione
            if (!isAgentOfSameType.test(currentState.getAgentAt(newPos).get(),
                    currentState.getAgentAt(agentPosition).get())) {
                // l'agente in agentPos è sano, l'agente in newPos è infetto
                // probabile infezione dell'agente in agentPos
                Optional<Agent> newAgent = infectPerson(agent, agentPosition);
                if (newAgent.isPresent()) {
                    currentState.removeAgent(agentPosition, agent);
                    currentState.addAgent(agentPosition, newAgent.get());
                    return currentState;
                }
            }
            // se la nuova posizione è occupata da due agenti dello stesso tipo, cambio
            // direzione
            dir = ComputationUtils.getRandomDirection(r);
            currentState.getAgentAt(agentPosition).get().setParameter("direction", dir);
            newPos = ComputationUtils.limit(
                    ComputationUtils.move(agentPosition, dir, stepSize), currentState.getDimensions());
        }
        if (currentState.isFree(newPos)) {
            // se la nuova posizione è libera, l'agente si sposta
            currentState.moveAgent(agentPosition, newPos);
        }
        return currentState;
    }

    /**
     * This method is used to infect a person. It uses the infection rate parameter
     * from the agent's parameters. If a random number is less than the infection
     * rate, the agent
     * is infected and substituted with a I agent. The agent parameters are also
     * updated.
     *
     * @param agent         The agent to be infected
     * @param agentPosition The position of the agent
     * @return An Optional containing the infected agent if infection was
     *         successful, otherwise an empty Optional
     */
    private Optional<Agent> infectPerson(final Agent agent, final Pos agentPosition) {
        if (r.nextInt(100) < infectionRate) {
            // create a new agent with the parameters of the infected agents
            IVirusAgentFactory factory = new IVirusAgentFactory();
            Agent a = factory.createAgent();
            a.setParameter("stepSize", stepSizeI);
            a.setParameter("recoveryRate", recoveryRate);
            return Optional.of(a);
        }
        return Optional.empty();
    }

    /**
     * Creates a new agent with the given parameters.
     *
     * @return The new agent.
     */
    @Override
    public Agent createAgent() {
        AgentBuilder b = new AgentBuilderImpl();
        b.addParameter(new ParameterImpl<>("stepSize", Integer.class,
                new ParameterDomainImpl<>("la dimensione del passo (1-10)",
                        (Integer d) -> d > 0 && d <= 10),
                true));
        b.addParameter(new ParameterImpl<>("direction", ComputationUtils.getRandomDirection(r), false));
        b.addParameter(new ParameterImpl<>("infectionRate", Integer.class,
                new ParameterDomainImpl<Integer>(
                        "Probabilità di infenzione da contatto (0-100)",
                        i -> i >= 0 && i <= 100),
                true));
        b.addStrategy(this::tickFunction);
        Agent a = b.build();
        a.setType("P");
        return a;
    }
}
