package it.unibo.ares.core.agent;

import java.util.Optional;
import java.util.Random;

import it.unibo.ares.core.utils.parameters.ParameterImpl;
import it.unibo.ares.core.model.VirusModelFactory;
import it.unibo.ares.core.utils.ComputationUtils;
import it.unibo.ares.core.utils.directionvector.DirectionVector;
import it.unibo.ares.core.utils.directionvector.DirectionVectorImpl;
import it.unibo.ares.core.utils.parameters.ParameterDomainImpl;
import it.unibo.ares.core.utils.pos.Pos;
import it.unibo.ares.core.utils.state.State;

/**
 * A factory class for creating I agents for the virus model.
 */
public final class IVirusAgentFactory implements AgentFactory {

    private final Random r;

    /**
     * Constructor for the IVirusAgentFactory.
     */
    public IVirusAgentFactory() {
        r = new Random();
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
        if (!currentState.getAgentAt(agentPosition).isPresent()) {
            return currentState;
        }
        final Agent agent = currentState.getAgentAt(agentPosition).get();

        // assegno una nuova direzione casuale ad ogni step
        DirectionVector dir = ComputationUtils.getRandomDirection(r);

        currentState.getAgentAt(agentPosition).get().setParameter("direction", dir);

        // se l'agente è infetto, controllo se guarisce, in caso negativo continuo con
        // lo spostamento
        if ("I".equals(agent.getType())) {
            final Optional<Agent> newAgent = recoveryInfected(agent);
            if (newAgent.isPresent()) {
                // se guarisce, rimuovo l'agente infetto e aggiungo un agente sano
                // e ritorno lo stato aggiornato
                currentState.removeAgent(agentPosition, agent);
                currentState.addAgent(agentPosition, newAgent.get());
                return currentState;
            }
        }
        final int stepSize = agent.getParameters().getParameter("stepSize", Integer.class)
                .get().getValue();
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
     * updated with the default parameters of a healthy person.
     *
     * @param agent         The agent to be recovered
     * @return An Optional containing the recovered agent if recovery was
     *         successful, otherwise an empty Optional
     */
    private Optional<Agent> recoveryInfected(final Agent agent) {
        final int recoveryRate = agent.getParameters().getParameter("recoveryRate", Integer.class)
                .get().getValue();
        if (r.nextInt(100) < recoveryRate) {
            final int stepSizeP = VirusModelFactory.STEP_SIZEP;
            final int infectionRate = VirusModelFactory.INFECTION_RATE;
            // create a new agent with the parameters of the person agents
            final PVirusAgentFactory factory = new PVirusAgentFactory();
            final Agent a = factory.createAgent();
            a.setParameter("stepSize", stepSizeP);
            a.setParameter("infectionRate", infectionRate);
            return Optional.of(a);
        }
        return Optional.empty();
    }

    /**
     * Creates a new infected agent with the following parameters:
     * - stepSize: the size of the step (1-10)
     * - direction: the direction of the agent
     * - recoveryRate: the probability of recovery at every step (0-100).
     *
     * @return The new infected agent.
     */
    @Override
    public Agent createAgent() {
        final Agent a = new AgentBuilderImpl()
                .addParameter(new ParameterImpl<>("stepSize", Integer.class,
                        new ParameterDomainImpl<>("la dimensione del passo (1-10)",
                                (Integer d) -> d > 0 && d <= 10),
                        true))
                .addParameter(new ParameterImpl<>("direction", ComputationUtils.getRandomDirection(r), false))
                .addParameter(new ParameterImpl<>("recoveryRate", Integer.class,
                        new ParameterDomainImpl<Integer>(
                                "Probabilità di guarigione a ogni step (0-100)",
                                i -> i >= 0 && i <= 100),
                        true))
                .addStrategy(this::tickFunction).build();
        a.setType("I");
        return a;
    }

}
