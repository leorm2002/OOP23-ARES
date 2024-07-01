package it.unibo.ares.core.agent;

import java.util.Optional;
import java.util.Random;
import java.util.function.BiPredicate;

import it.unibo.ares.core.utils.parameters.ParameterImpl;
import it.unibo.ares.core.model.VirusModelFactory;
import it.unibo.ares.core.utils.ComputationUtils;
import it.unibo.ares.core.utils.directionvector.DirectionVector;
import it.unibo.ares.core.utils.directionvector.DirectionVectorImpl;
import it.unibo.ares.core.utils.parameters.ParameterDomainImpl;
import it.unibo.ares.core.utils.pos.Pos;
import it.unibo.ares.core.utils.state.State;

/**
 * A factory class for creating P agents for the virus model.
 */
public final class PVirusAgentFactory implements AgentFactory {

    private static final long serialVersionUID = 1L;
    private final Random r;
    /*
     * A predicate to check if two agents are of the same type.
     */
    private static BiPredicate<Agent, Agent> checkAgentSameType = (a, b) -> {
        final String typeA = a.getType();
        final String typeB = b.getType();
        return typeA.equals(typeB);
    };

    /**
     * Constructor for the PVirusAgentFactory.
     */
    public PVirusAgentFactory() {
        r = new Random();
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
        if (!currentState.getAgentAt(agentPosition).isPresent()) {
            return currentState;
        }
        final Agent agent = currentState.getAgentAt(agentPosition).get();

        final int stepSize = agent.getParameters().getParameter("stepSize", Integer.class)
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
            if (!checkAgentSameType.test(currentState.getAgentAt(newPos).get(),
                    currentState.getAgentAt(agentPosition).get())) {
                // l'agente in agentPos è sano, l'agente in newPos è infetto
                // probabile infezione dell'agente in agentPos
                final Optional<Agent> newAgent = infectPerson(agent);
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
     * updated with the default parameters of the infected agents.
     *
     * @param agent The agent to be infected
     * @return An Optional containing the infected agent if infection was
     *         successful, otherwise an empty Optional
     */
    private Optional<Agent> infectPerson(final Agent agent) {
        final int infectionRate = agent.getParameters().getParameter("infectionRate", Integer.class)
                .get().getValue();
        if (r.nextInt(100) < infectionRate) {
            // create a new agent with the defaul parameters of the infected agents
            final int stepSizeI = VirusModelFactory.STEP_SIZEI;
            final int recoveryRate = VirusModelFactory.RECOVERY_RATE;
            final IVirusAgentFactory factory = new IVirusAgentFactory();
            final Agent a = factory.createAgent();
            a.setParameter("stepSize", stepSizeI);
            a.setParameter("recoveryRate", recoveryRate);
            return Optional.of(a);
        }
        return Optional.empty();
    }

    /**
     * Creates a new healthy agent with the following parameters:
     * - stepSize: the size of the step (1-10)
     * - direction: a random direction
     * - infectionRate: the probability of getting infected by contact (0-100)
     * The agent is of type "P".
     *
     * @return The new healty agent.
     */
    @Override
    public Agent createAgent() {
        final AgentBuilder b = new AgentBuilderImpl();
        b.addParameter(new ParameterImpl<>("direction", ComputationUtils.getRandomDirection(r), false));
        b.addParameter(new ParameterImpl<>("infectionRate", Integer.class,
                new ParameterDomainImpl<>(
                        "Probabilità di infenzione da contatto (0-100)",
                        (Integer i) -> i >= 0 && i <= 100),
                true));
        b.addParameter(new ParameterImpl<>("stepSize", Integer.class,
                new ParameterDomainImpl<>("la dimensione del passo (1-10)",
                        (Integer d) -> d > 0 && d <= 10),
                true));
        b.addStrategy(this::tickFunction);
        final Agent a = b.build();
        a.setType("P");
        return a;
    }
}
