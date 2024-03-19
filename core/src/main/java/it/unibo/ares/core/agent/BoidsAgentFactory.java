package it.unibo.ares.core.agent;

import it.unibo.ares.core.utils.ComputationUtils;
import it.unibo.ares.core.utils.directionvector.DirectionVector;
import it.unibo.ares.core.utils.directionvector.DirectionVectorImpl;
import it.unibo.ares.core.utils.parameters.ParameterDomainImpl;
import it.unibo.ares.core.utils.parameters.ParameterImpl;
import it.unibo.ares.core.utils.pos.Pos;
import it.unibo.ares.core.utils.pos.PosImpl;
import it.unibo.ares.core.utils.state.State;

import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Represents a factory for creating Boids agents.
 * 
 */
public final class BoidsAgentFactory implements AgentFactory {
    // PARAMETRI FINE TUNTING
    private static final Double USERCORRECTIONWEIGHT = 0.4;
    private static final Double WALLAVOIDANCEWEIGHT = 0.2;
    private static final String DIRECTION = "direction";
    private final Random r;

    /**
     * Creates a new Boids agent factory.
     */
    public BoidsAgentFactory() {
        this.r = new Random();
    }

    private Set<Pos> getAgentsCells(final State state, final Set<Pos> cells) {
        return cells.stream()
                .filter(p -> state.getAgentAt(p).isPresent())
                .collect(Collectors.toSet());
    }

    private Set<Pos> getObstacles(final State state, final Set<Pos> cells) {
        return cells.stream()
                .filter(p -> state.getAgentAt(p).isPresent() || state.getEntityAt(p).isPresent())
                .collect(Collectors.toSet());
    }

    private DirectionVector collisionAvoindance(
            final State s, final Pos pos, final DirectionVector dir,
            final Integer distance, final Integer angle) {
        final Set<Pos> obstacles = getObstacles(s, ComputationUtils.computeCloseCells(pos, dir, distance, angle));
        if (obstacles.isEmpty()) {
            return dir;
        }
        int newX = 0;
        int newY = 0;
        for (final Pos p : obstacles) {
            newX += pos.getX() - p.getX();
            newY += pos.getY() - p.getY();
        }
        return new DirectionVectorImpl(newX, newY).getNormalized();
    }

    private DirectionVector directionAlignment(
            final State s, final Pos pos, final DirectionVector dir, final Integer distance,
            final Integer angle) {
        final var closeCells = ComputationUtils.computeCloseCells(pos, dir, distance, angle);
        final var agents = getAgentsCells(s, closeCells);
        return agents.stream()
                .map(p -> s.getAgentAt(p).get().getParameters()
                        .getParameter(
                                DIRECTION, DirectionVectorImpl.class)
                        .get()
                        .getValue())
                .map(DirectionVector.class::cast)
                .reduce((a, b) -> a.mean(b))
                .map(d -> new DirectionVectorImpl(d.getX() / agents.size(), d.getY() / agents.size()))
                .map(d -> d.getNormalized())
                .orElse(dir);
    }

    private DirectionVector centerCohesion(final State s, final Pos pos, final DirectionVector dir,
            final Integer distance, final Integer angle) {
        // Compute a vector pointing to che center of the flock
        final var closeCells = ComputationUtils.computeCloseCells(pos, dir, distance, angle);
        final var agents = getAgentsCells(s, closeCells);
        final var center = closeCells.stream()
                .filter(p -> s.getAgentAt(p).isPresent())
                .reduce((a, b) -> new PosImpl(a.getX() + b.getX(), a.getY() + b.getY()))
                .map(p -> new PosImpl(p.getX() / agents.size(), p.getY() / agents.size()))
                .orElse(new PosImpl(pos.getX(), pos.getY()));
        return new DirectionVectorImpl(center.getX() - pos.getX(), center.getY() - pos.getY()).getNormalized();
    }

    private DirectionVector mixer(final DirectionVector original, final DirectionVector steerAway,
            final DirectionVector a, final DirectionVector b,
            final DirectionVector c,
            final double w1, final double w2, final double w3) {
        double i = 0;
        double j = 0;
        final List<DirectionVector> vectors = List.of(a, b, c);
        final List<Double> weights = List.of(w1, w2, w3);
        for (final DirectionVector v : vectors) {
            i += v.getNormalizedX() * weights.get(vectors.indexOf(v));
            j += v.getNormalizedY() * weights.get(vectors.indexOf(v));
        }
        i *= USERCORRECTIONWEIGHT;
        j *= USERCORRECTIONWEIGHT;
        i += original.getNormalizedX() * (1 - USERCORRECTIONWEIGHT);
        j += original.getNormalizedY() * (1 - USERCORRECTIONWEIGHT);
        i += steerAway.getNormalizedX() * WALLAVOIDANCEWEIGHT;
        j += steerAway.getNormalizedY() * WALLAVOIDANCEWEIGHT;
        return new DirectionVectorImpl(i, j).getNormalized();
    }

    private Pos getPosNear(final Pos pos, final State state, final int distance, final DirectionVector dir,
            final int angle) {
        if (distance > state.getDimensions().getFirst() && distance > state.getDimensions().getSecond()) {
            return pos;
        }
        final Optional<Pos> newPos = ComputationUtils.computeCloseCells(pos, dir, distance, 180).stream()
                .filter(state::isInside)
                .filter(state::isFree)
                .findAny()
                .map(Pos.class::cast);
        if (newPos.isEmpty()) {
            return getPosNear(pos, state, distance + 1, dir, angle);
        }

        return newPos.get();
    }

    DirectionVector steerAwayFromBorder(final Pos currentPos, final int width, final int height) {
        return new DirectionVectorImpl(
                width / 2 - currentPos.getX(),
                height / 2 - currentPos.getY());
    }

    private State tickFunction(final State currentState, final Pos agentPosition) {
        if (!currentState.getAgentAt(agentPosition).isPresent()) {
            return currentState;
        }
        final Agent agent = currentState.getAgentAt(agentPosition).get();
        if (!agent.getParameters().getParametersToset().isEmpty()) {
            throw new RuntimeException("Parameters not set");
        }

        final DirectionVector dir = agent.getParameters()
                .getParameter(DIRECTION, DirectionVectorImpl.class).get().getValue();
        final Integer angle = agent.getParameters()
                .getParameter("angle", Integer.class).get().getValue();
        final Integer distance = agent.getParameters()
                .getParameter("distance", Integer.class).get().getValue();

        DirectionVector newDir = mixer(
                dir,
                steerAwayFromBorder(agentPosition, currentState.getDimensions().getFirst(),
                        currentState.getDimensions().getSecond()),
                collisionAvoindance(currentState, agentPosition, dir, distance, angle),
                directionAlignment(currentState, agentPosition, dir, distance, angle),
                centerCohesion(currentState, agentPosition, dir, distance, angle),
                agent.getParameters().getParameter("collisionAvoidanceWeight", Double.class)
                        .get().getValue(),
                agent.getParameters().getParameter("alignmentWeight", Double.class)
                        .get().getValue(),
                agent.getParameters().getParameter("cohesionWeight", Double.class)
                        .get().getValue());

        agent.setParameter(DIRECTION, newDir);
        final int stepSize = agent.getParameters().getParameter("stepSize", Integer.class)
                .get().getValue();
        Pos newPos = ComputationUtils
                .move(agentPosition, newDir,
                        agent.getParameters().getParameter("stepSize", Integer.class)
                                .get().getValue());
        if (!currentState.isInside(newPos)) {
            newDir = new DirectionVectorImpl(-newDir.getX(), -newDir.getY());
            newPos = ComputationUtils.limit(ComputationUtils.move(agentPosition, newDir, stepSize),
                    currentState.getDimensions());
        }
        if (!currentState.isFree(newPos)) {
            newPos = getPosNear(newPos, currentState, stepSize * 2, newDir, angle);
        }
        if (currentState.isFree(newPos)) {
            currentState.moveAgent(agentPosition, newPos);
        }
        return currentState;
    }

    /**
     * Creates a new Boids agent.
     * A boids Agents requires the following parameters:
     * - distance: the distance within which to consider other agents (Integer).
     * - angle: the angle within which to consider other agents (Integer).
     * - collisionAvoidanceWeight: the weight of the collision avoidance (Double).
     * - alignmentWeight: the weight of the alignment (Double).
     * - cohesionWeight: the weight of the cohesion (Double).
     * - stepSize: the step size of the agent (Integer).
     * 
     * @return a new Boids agent.
     */
    @Override
    public Agent createAgent() {
        return new AgentBuilderImpl()
                .addParameter(new ParameterImpl<>("distance", Integer.class,
                        new ParameterDomainImpl<>("il raggio di visione in celle (1-10)",
                                (Integer d) -> d > 0 && d <= 10),
                        true))
                .addParameter(new ParameterImpl<>("angle", Integer.class,
                        new ParameterDomainImpl<>("il raggio di visione in gradi (0-180)",
                                (Integer d) -> d > 0 && d <= 180),
                        true))
                .addParameter(new ParameterImpl<>(DIRECTION, ComputationUtils.getRandomDirection(r), false))
                .addParameter(new ParameterImpl<>("collisionAvoidanceWeight", Double.class,
                        new ParameterDomainImpl<>(
                                "il peso dell'evitamento degli ostacoli (0.0-1.0)",
                                (Double d) -> d >= 0.0 && d <= 1.0),
                        true))
                .addParameter(new ParameterImpl<>("alignmentWeight", Double.class,
                        new ParameterDomainImpl<>("il peso dell'allineamento (0.0-1.0)",
                                (Double d) -> d >= 0.0 && d <= 1.0),
                        true))
                .addParameter(new ParameterImpl<>("cohesionWeight", Double.class,
                        new ParameterDomainImpl<>("il peso della coesione (0.0-1.0)",
                                (Double d) -> d >= 0.0 && d <= 1.0),
                        true))
                .addParameter(new ParameterImpl<>("stepSize", Integer.class,
                        new ParameterDomainImpl<>("la dimensione del passo (1-10)",
                                (Integer d) -> d > 0 && d <= 10),
                        true))
                .addStrategy(this::tickFunction)
                .build();
    }
}
