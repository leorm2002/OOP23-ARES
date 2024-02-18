package it.unibo.ares.core.agent;

import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import it.unibo.ares.core.utils.Pair;
import it.unibo.ares.core.utils.directionvector.DirectionVector;
import it.unibo.ares.core.utils.directionvector.DirectionVectorImpl;
import it.unibo.ares.core.utils.parameters.ParameterImpl;
import it.unibo.ares.core.utils.pos.Pos;
import it.unibo.ares.core.utils.pos.PosImpl;
import it.unibo.ares.core.utils.state.State;
import it.unibo.ares.core.utils.state.StateImpl;

public final class BoidsAgentFactory implements AgentFactory {

        private boolean insideCone(final Pos pos, final Pos center, final DirectionVector dir, final Integer distance,
                        final Integer angle) {
                double radAng = Math.toRadians(angle);

                DirectionVector vectorToNewPoint = new DirectionVectorImpl(pos.diff(center).getX(),
                                pos.diff(center).getY());
                double dotProduct = dir.getNormalized().pointProduct(vectorToNewPoint.getNormalized());
                double radAngleBetween = Math.acos(dotProduct);

                return radAngleBetween <= radAng && vectorToNewPoint.getMagnitude() <= distance;
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

        private Set<Pos> computeCloseCells(final Pos pos, final DirectionVector dir, final Integer distance,
                        final Integer angle) {
                int xSign = dir.getX() > 0 ? 1 : -1;
                int ySign = dir.getY() > 0 ? 1 : -1;

                State a = new StateImpl(
                                Math.abs(pos.getX() + xSign * (distance + 1)),
                                Math.abs(pos.getY() + ySign * (distance + 1)));

                return a.getPosByPosAndRadius(pos, distance)
                                .stream()
                                .filter(p -> insideCone(p, pos, dir, distance, angle))
                                .collect(Collectors.toSet());
        }

        private DirectionVector collisionAvoindance(
                        final State s, final Pos pos, final DirectionVector dir,
                        final Integer distance, final Integer angle) {
                Set<Pos> obstacles = getObstacles(s, computeCloseCells(pos, dir, distance, angle));
                if (obstacles.isEmpty()) {
                        return dir;
                }
                int newX = 0;
                int newY = 0;
                for (Pos p : obstacles) {
                        newX += pos.getX() - p.getX();
                        newY += pos.getY() - p.getY();
                }
                return new DirectionVectorImpl(newX, newY).getNormalized();
        }

        private DirectionVector directionAlignment(
                        final State s, final Pos pos, final DirectionVector dir, final Integer distance,
                        final Integer angle) {
                var closeCells = computeCloseCells(pos, dir, distance, angle);
                var agents = getAgentsCells(s, closeCells);
                return agents.stream()
                                .map(p -> s.getAgentAt(p).get().getParameters()
                                                .getParameter("direction", DirectionVectorImpl.class).get()
                                                .getValue())
                                .map(DirectionVector.class::cast)
                                .reduce((a, b) -> a.mean(b))
                                .map(d -> new DirectionVectorImpl(d.getX() / agents.size(), d.getY() / agents.size()))
                                .map(d -> d.getNormalized())
                                .orElse(dir);
        }

        private DirectionVector centerCohesion(State s, final Pos pos, final DirectionVector dir,
                        final Integer distance, final Integer angle) {
                // Compute a vector pointing to che center of the flock
                var closeCells = computeCloseCells(pos, dir, distance, angle);
                var agents = getAgentsCells(s, closeCells);
                var center = closeCells.stream()
                                .filter(p -> s.getAgentAt(p).isPresent())
                                .reduce((a, b) -> new PosImpl(a.getX() + b.getX(), a.getY() + b.getY()))
                                .map(p -> new PosImpl(p.getX() / agents.size(), p.getY() / agents.size()))
                                .orElse(new PosImpl(pos.getX(), pos.getY()));
                return new DirectionVectorImpl(center.getX() - pos.getX(), center.getY() - pos.getY()).getNormalized();
        }

        private DirectionVector mixer(final DirectionVector a, final DirectionVector b, final DirectionVector c,
                        final double w1, final double w2, final double w3) {
                return new DirectionVectorImpl(
                                a.getX() * w1 + b.getX() * w2 + c.getX() * w3,
                                a.getY() * w1 + b.getY() * w2 + c.getY() * w3).getNormalized();
        }

        private Pos move(final Pos initialPos, final DirectionVector dir, final Integer stepSize) {
                return new PosImpl(initialPos.getX() + dir.getX() * stepSize,
                                initialPos.getY() + dir.getY() * stepSize);
        }

        private int limit(final int curr, final int max) {
                return curr < 0 ? 0 : curr > (max - 1) ? (max - 1) : curr;
        }

        private Pos limit(final Pos pos, final Pair<Integer, Integer> size) {
                return new PosImpl(limit(pos.getX(), size.getFirst()), limit(pos.getY(), size.getSecond()));
        }

        private State tickFunction(final State currentState, final Pos agentPosition) {
                Random r = new Random();
                if (!currentState.getAgentAt(agentPosition).isPresent()) {
                        return currentState;
                }
                Agent agent = currentState.getAgentAt(agentPosition).get();
                if (!agent.getParameters().getParametersToset().isEmpty()) {
                        throw new RuntimeException("Parameters not set");
                }

                DirectionVector dir = agent.getParameters()
                                .getParameter("direction", DirectionVectorImpl.class).get().getValue();
                Integer angle = agent.getParameters()
                                .getParameter("angle", Integer.class).get().getValue();
                Integer distance = agent.getParameters()
                                .getParameter("distance", Integer.class).get().getValue();

                DirectionVector newDir = mixer(
                                collisionAvoindance(currentState, agentPosition, dir, distance, angle),
                                directionAlignment(currentState, agentPosition, dir, distance, angle),
                                centerCohesion(currentState, agentPosition, dir, distance, angle),
                                agent.getParameters().getParameter("collisionAvoidanceWeight", Double.class)
                                                .get().getValue(),
                                agent.getParameters().getParameter("alignmentWeight", Double.class)
                                                .get().getValue(),
                                agent.getParameters().getParameter("cohesionWeight", Double.class)
                                                .get().getValue());

                agent.setParameter("direction", newDir);
                Pos newPos = limit(move(agentPosition, newDir,
                                agent.getParameters().getParameter("stepSize", Integer.class)
                                                .get().getValue()),
                                currentState.getDimensions());

                if (!currentState.isFree(newPos)) {
                        newPos = limit(new PosImpl(r.nextInt(3) - 1 + newPos.getX(), r.nextInt(3) - 1 + newPos.getX()),
                                        currentState.getDimensions());
                }
                if (currentState.isFree(newPos)) {
                        currentState.moveAgent(agentPosition, newPos);
                }
                return currentState;
        }

        public Agent createAgent() {
                AgentBuilder builder = new AgentBuilderImpl();
                return builder
                                .addParameter(new ParameterImpl<>("distance", Integer.class))
                                .addParameter(new ParameterImpl<>("angle", Integer.class))
                                .addParameter(new ParameterImpl<>("direction", DirectionVectorImpl.class))
                                .addParameter(new ParameterImpl<>("collisionAvoidanceWeight", Double.class))
                                .addParameter(new ParameterImpl<>("alignmentWeight", Double.class))
                                .addParameter(new ParameterImpl<>("cohesionWeight", Double.class))
                                .addParameter(new ParameterImpl<>("stepSize", Integer.class))
                                .addStrategy(this::tickFunction)
                                .build();
        }
}
