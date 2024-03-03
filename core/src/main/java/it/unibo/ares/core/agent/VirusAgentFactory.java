package it.unibo.ares.core.agent;

import java.util.Optional;
import java.util.Random;
import java.util.function.BiPredicate;

import it.unibo.ares.core.utils.parameters.ParameterImpl;
import it.unibo.ares.core.utils.Pair;
import it.unibo.ares.core.utils.directionvector.DirectionVector;
import it.unibo.ares.core.utils.directionvector.DirectionVectorImpl;
import it.unibo.ares.core.utils.parameters.ParameterDomainImpl;
import it.unibo.ares.core.utils.pos.Pos;
import it.unibo.ares.core.utils.pos.PosImpl;
import it.unibo.ares.core.utils.state.State;


/**
 * A factory class for creating agents for the Schelling Segregation Model.
 */
public final class VirusAgentFactory implements AgentFactory {

        private Random r;
        private char type;
        // PARAMETRI DA SETTARE, INIZIALIZZATI A VALORI DI DEFAULT PER SVOLGERE TEST
        private static int stepSizeP = 1;
        private static int stepSizeI = 1;
        private static int infectionRate = 100;
        private static int recoveryRate = 100;


        public VirusAgentFactory() {
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
         * Moves the agent in the given direction by the given step size.
         *
         * @param initialPos The initial position of the agent.
         * @param dir        The direction in which the agent should move.
         * @param stepSize   The number of steps the agent should take.
         * @return The new position of the agent.
         */
        private Pos move(final Pos initialPos, final DirectionVector dir, final Integer stepSize) {
                return new PosImpl(initialPos.getX() + dir.getNormalizedX() * stepSize,
                                initialPos.getY() + dir.getNormalizedY() * stepSize);
        }

        /**
         * Limits the given value to the range [0, max - 1].
         *
         * @param curr The current value.
         * @param max  The maximum value.
         * @return The limited value.
         */
        private int limit(final int curr, final int max) {
                return curr < 0 ? 0 : curr > (max - 1) ? (max - 1) : curr;
        }

        /**
         * Limits the given position to the size of the environment.
         *
         * @param pos  The current position.
         * @param size The size of the environment.
         * @return The limited position.
         */
        private Pos limit(final Pos pos, final Pair<Integer, Integer> size) {
                return new PosImpl(limit(pos.getX(), size.getFirst()), limit(pos.getY(), size.getSecond()));
        }

        /**
         * Generates a random direction for the agent to move in.
         *
         * @return The random direction.
         */
        private DirectionVectorImpl getRandomDirection() {
                int x = r.nextInt(-5, 5), y = r.nextInt(-5, 5);
                if(x == 0 && y == 0)
                        return getRandomDirection();
                return new DirectionVectorImpl(x, y);
        }

        /**
         * Updates the state of the agent based on its current state and position.
         *
         * @param currentState  The current state of the agent.
         * @param agentPosition The position of the agent.
         * @return The updated state of the agent.
         */
        private State tickFunction(final State currentState, final Pos agentPosition) {
                if (!currentState.getAgentAt(agentPosition).isPresent()) {
                        return currentState;
                }
                Agent agent = currentState.getAgentAt(agentPosition).get();
                if (!agent.getParameters().getParametersToset().isEmpty()) {
                        throw new RuntimeException("Parameters not set");
                }
                switch (agent.getType()) {
                        case "P":
                                stepSizeP = agent.getParameters().getParameter("stepSize", Integer.class)
                                                .get().getValue();
                                infectionRate = agent.getParameters().getParameter("infectionRate", Integer.class)
                                                .get().getValue();
                                break;

                        case "I":
                                stepSizeI = agent.getParameters().getParameter("stepSize", Integer.class)
                                                .get().getValue();
                                recoveryRate = agent.getParameters().getParameter("recoveryRate", Integer.class)
                                                .get().getValue();
                                break;
                }

                DirectionVector dir = agent.getParameters()
                                .getParameter("direction", DirectionVectorImpl.class).get().getValue();
                int stepSize = agent.getParameters().getParameter("stepSize", Integer.class)
                                .get().getValue();
                currentState.getAgentAt(agentPosition).get().setParameter("direction", getRandomDirection());

                if (agent.getType().equals("I")) {
                        Optional<Agent> newAgent = recoveryInfected(agent, agentPosition);
                        if (newAgent.isPresent()) {
                                currentState.removeAgent(agentPosition, agent);
                                currentState.addAgent(agentPosition, newAgent.get());
                                return currentState;
                        }
                }
                dir = agent.getParameters().getParameter("direction", DirectionVectorImpl.class).get().getValue();
                Pos newPos = move(agentPosition, dir, stepSize);
                if (!currentState.isInside(newPos)) {
                        dir = new DirectionVectorImpl(-dir.getX(), -dir.getY());
                        newPos = limit(move(agentPosition, dir, stepSize), currentState.getDimensions());
                }
                if (!currentState.isFree(newPos)) {
                        if (!isAgentOfSameType.test(currentState.getAgentAt(newPos).get(),
                                        currentState.getAgentAt(agentPosition).get())) {
                                if (currentState.getAgentAt(newPos).get().getType().equals("I")) {
                                        infectionRate = agent.getParameters()
                                                        .getParameter("infectionRate", Integer.class)
                                                        .get().getValue();
                                        //new pos è infetto quindi agent pos è p
                                        Optional<Agent> newAgent = infectPerson(agent, agentPosition);
                                        if (newAgent.isPresent()) {
                                                currentState.removeAgent(agentPosition, agent);
                                                currentState.addAgent(agentPosition, newAgent.get());
                                                return currentState;
                                        }
                                } else {
                                        //agent pos è infetto, new pos è p
                                        infectionRate = currentState.getAgentAt(newPos).get().getParameters()
                                                        .getParameter("infectionRate", Integer.class)
                                                        .get().getValue();
                                        Optional<Agent> newAgent = infectPerson(currentState.getAgentAt(newPos).get(),
                                                        newPos);
                                        if (newAgent.isPresent()) {
                                                currentState.removeAgent(newPos, currentState.getAgentAt(newPos).get());
                                                currentState.addAgent(newPos, newAgent.get());
                                                return currentState;
                                        }
                                }
                        }
                        currentState.getAgentAt(agentPosition).get().setParameter("direction", getRandomDirection());
                        dir = agent.getParameters().getParameter("direction", DirectionVectorImpl.class).get()
                                        .getValue();
                        newPos = limit(move(agentPosition, dir, stepSize), currentState.getDimensions());
                }
                if (currentState.isFree(newPos)) {
                        currentState.moveAgent(agentPosition, newPos);
                }
                return currentState;
        }
        
        private Optional<Agent> infectPerson(final Agent agent, final Pos agentPosition) {
                if (r.nextInt(100) < infectionRate) {
                        setTypeOfAgent('I');
                        Agent a = createAgent();
                        a.setType("I");
                        a.setParameter("stepSize", stepSizeI);
                        a.setParameter("direction", getRandomDirection());
                        a.setParameter("recoveryRate", recoveryRate);
                        return Optional.of(a);
                }
                return Optional.empty();
        }
        
        private Optional<Agent> recoveryInfected(final Agent agent, final Pos agentPosition) {
                recoveryRate = agent.getParameters().getParameter("recoveryRate", Integer.class)
                                .get().getValue();
                if (r.nextInt(100) < recoveryRate) {
                        setTypeOfAgent('P');
                        Agent a = createAgent();
                        a.setType("P");
                        a.setParameter("stepSize", stepSizeP);
                        a.setParameter("direction", getRandomDirection());
                        a.setParameter("infectionRate", infectionRate);
                        return Optional.of(a);
                }
                return Optional.empty();
        }

        @Override
        public Agent createAgent() {
                AgentBuilder b = new AgentBuilderImpl();
                b.addParameter(new ParameterImpl<>("stepSize", Integer.class,
                                new ParameterDomainImpl<>("la dimensione del passo (1-10)",
                                                (Integer d) -> d > 0 && d <= 10), true));
                b.addParameter(new ParameterImpl<>("direction", getRandomDirection(), false));
                if (type == 'P') {
                        b.addParameter(new ParameterImpl<>("infectionRate", Integer.class,
                        new ParameterDomainImpl<Integer>(
                                "Probabilità di infenzione da contatto (0-100)",
                                                        i -> i >= 0 && i <= 100),true));
                }
                else if (type == 'I') {
                        b.addParameter(new ParameterImpl<>("recoveryRate", Integer.class,
                        new ParameterDomainImpl<Integer>(
                                "Probabilità di guarigione a ogni step (0-100)",
                                                        i -> i >= 0 && i <= 100), true));
                }
                b.addStrategy(this::tickFunction);
                return b.build();
        }

        public void setTypeOfAgent(final char c) {
                type = c;
        }
}
