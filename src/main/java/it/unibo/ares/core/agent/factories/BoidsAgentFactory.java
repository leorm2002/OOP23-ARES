package it.unibo.ares.core.agent.factories;

import java.util.Set;
import java.util.stream.Collectors;

import it.unibo.ares.core.agent.Agent;
import it.unibo.ares.core.agent.AgentBuilder;
import it.unibo.ares.core.agent.AgentBuilderImpl;
import it.unibo.ares.core.utils.directionvector.DirectionVector;
import it.unibo.ares.core.utils.directionvector.DirectionVectorImpl;
import it.unibo.ares.core.utils.parameters.ParameterImpl;
import it.unibo.ares.core.utils.pos.Pos;
import it.unibo.ares.core.utils.pos.PosImpl;
import it.unibo.ares.core.utils.state.State;
import it.unibo.ares.core.utils.state.StateImpl;

public class BoidsAgentFactory {
    
    public boolean insideCone(final Pos pos, final Pos center, final DirectionVector dir, final Integer distance, final Integer angle) {
        double radAng = Math.toRadians(angle);

        DirectionVector vectorToNewPoint = new DirectionVectorImpl(pos.diff(center).getX(), pos.diff(center).getY());
        double dotProduct = dir.getNormalized().pointProduct(vectorToNewPoint.getNormalized());
        double radAngleBetween = Math.acos(dotProduct);

        return radAngleBetween <= radAng && vectorToNewPoint.getMagnitude() <= distance;
    }

    public Set<Pos> getAgentsCells(State state, Set<Pos> cells){
        return cells.stream()
                .filter(p -> state.getAgentAt(p).isPresent())
                .collect(Collectors.toSet());
    }

    public Set<Pos> getObstacles(State state, Set<Pos> cells){
        return cells.stream()
                .filter(p -> state.getAgentAt(p).isPresent() || state.getEntityAt(p).isPresent())
                .collect(Collectors.toSet());
    }

        Set<Pos> computeCloseCells(final Pos pos, final DirectionVector dir, final Integer distance, final Integer angle) {
        State a = new StateImpl(pos.getX()+distance+1, pos.getY()+distance+1);

        return a.getPosByPosAndRadius(pos, distance)
                .stream()
                .filter(p -> insideCone(p, pos, dir, distance, angle))
                .collect(Collectors.toSet());
    }

    public DirectionVector collisionAvoindance(State s, final Pos pos, final DirectionVector dir, final Integer distance, final Integer angle){
        var closeCells = computeCloseCells(pos, dir, distance, angle);

        return null;
    }

    public DirectionVector directionAlignment(State s, final Pos pos, final DirectionVector dir, final Integer distance, final Integer angle){
        var closeCells = computeCloseCells(pos, dir, distance, angle);
        var agents = getAgentsCells(s, closeCells);
        var dirMean = agents.stream()
                    .map(p -> s.getAgentAt(p).get().getParameters().getParameter("direction", DirectionVector.class).get().getValue())
                    .reduce((a,b) -> a.mean(b))
                    .map(d -> new DirectionVectorImpl(d.getX()/agents.size(), d.getY()/ agents.size()))
                    .map(d -> d.getNormalized())
                    .orElse(dir);
        return dirMean;
    }
    public DirectionVector directionCenterCohesion(State s, final Pos pos, final DirectionVector dir, final Integer distance, final Integer angle){
        //Compute a vector pointing to che center of the flock
        var closeCells = computeCloseCells(pos, dir, distance, angle);
        var agents = getAgentsCells(s, closeCells);
        var center = closeCells.stream()
                    .filter(p -> s.getAgentAt(p).isPresent())
                    .reduce((a,b) -> new PosImpl(a.getX()+b.getX(), a.getY()+b.getY()))
                    .map(p -> new PosImpl(p.getX()/agents.size(), p.getY()/agents.size()))
                    .orElse(new PosImpl(pos.getX(), pos.getY()));
        return new DirectionVectorImpl(center.getX()-pos.getX(), center.getY()-pos.getY()).getNormalized();
    }
    



    public Agent createBoid() {
        AgentBuilder builder = new AgentBuilderImpl();
        //Parametsisiers:
        // Distance to scan
        // Angle
        builder.addParameter(new ParameterImpl<>("distance", Integer.class));
        builder.addParameter(new ParameterImpl<>("angle", Integer.class));
        builder.addParameter(new ParameterImpl<>("direction", DirectionVector.class));

        return null;
    }
}
