package it.unibo.ares.core.agent;

import java.util.Optional;
import java.util.Random;
import java.util.Set;

import it.unibo.ares.core.utils.parameters.ParameterImpl;
import it.unibo.ares.core.utils.ComputationUtils;
import it.unibo.ares.core.utils.Pair;
import it.unibo.ares.core.utils.directionvector.DirectionVector;
import it.unibo.ares.core.utils.directionvector.DirectionVectorImpl;
import it.unibo.ares.core.utils.parameters.ParameterDomainImpl;
import it.unibo.ares.core.utils.pos.Pos;
import it.unibo.ares.core.utils.state.State;

/**
 * A factory class for creating I agents for the virus model.
 */
public final class IVirusAgentFactory implements AgentFactory {

    private Random r;
    // PARAMETRI DA SETTARE, SETTATI A VALORI DI DEFAULT
    // Utilizzati per settare i parametri dell'agente p
    private int stepSizeP = 1;
    private int recoveryRate = 0, infectionRate = 0;
    private boolean paramSected = false;

    /**
     * Constructor for the IVirusAgentFactory.
     */
    public IVirusAgentFactory() {
        r = new Random();
    }

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
                    stepSizeP = pair.getSecond().getParameters()
                            .getParameter("stepSize", Integer.class)
                            .get().getValue();
                    infectionRate = pair.getSecond().getParameters()
                            .getParameter("infectionRate", Integer.class)
                            .get().getValue();
                    persSected = true;
                    break;
                case "I":
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
     * The agent can move in the environment and recover from the infection.
     * At every tick the agents can move or recover.
     * Not two actions at the same tick.
     *
     * @param currentState  The current state of the agent.
     * @param agentPosition The position of the agent.
     * @return The updated state of the agent.
     */
    private State tickFunction(final State currentState, final Pos agentPosition) {
        // se i parametri della classe non sono stati settati, li setto
        if (!paramSected) {
            // AL PRIMO TICK DEVO SETTARE TUTTO ANCHE QUELLI DELLA P
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

        // se l'agente è infetto, controllo se guarisce, in caso negativo continuo con
        // lo spostamento
        if (agent.getType().equals("I")) {
            Optional<Agent> newAgent = recoveryInfected(agent, agentPosition);
            if (newAgent.isPresent()) {
                // se guarisce, rimuovo l'agente infetto e aggiungo un agente sano
                // e ritorno lo stato aggiornato
                currentState.removeAgent(agentPosition, agent);
                currentState.addAgent(agentPosition, newAgent.get());
                return currentState;
            }
        }
        Pos newPos = ComputationUtils.move(agentPosition, dir, stepSize);
        if (!currentState.isInside(newPos)) {
            // se la nuova posizione dell'agente sarebbe fuori dallo spazio, cambio
            // direzione
            dir = new DirectionVectorImpl(-dir.getX(), -dir.getY());
            newPos = ComputationUtils.limit(ComputationUtils.move(agentPosition, dir, stepSize),
                    currentState.getDimensions());
        }
        if (!currentState.isFree(newPos)) {
            // se la nuova posizione è occupata, cambio
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
     * This method is used to recover an infected agent. It uses the recovery rate
     * parameter
     * from the agent's parameters. If a random number is less than the recovery
     * rate, the agent
     * is recovered and substituted with a P agent. The agent parameters are also
     * updated.
     *
     * @param agent         The agent to be recovered
     * @param agentPosition The position of the agent
     * @return An Optional containing the recovered agent if recovery was
     *         successful, otherwise an empty Optional
     */
    private Optional<Agent> recoveryInfected(final Agent agent, final Pos agentPosition) {
        if (r.nextInt(100) < recoveryRate) {
            // create a new agent with the parameters of the person agents
            PVirusAgentFactory factory = new PVirusAgentFactory();
            Agent a = factory.createAgent();
            a.setParameter("stepSize", stepSizeP);
            a.setParameter("infectionRate", infectionRate);
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
        b.addParameter(new ParameterImpl<>("recoveryRate", Integer.class,
                new ParameterDomainImpl<Integer>(
                        "Probabilità di guarigione a ogni step (0-100)",
                        i -> i >= 0 && i <= 100),
                true));
        b.addStrategy(this::tickFunction);
        Agent a = b.build();
        a.setType("I");
        return a;
    }

}
