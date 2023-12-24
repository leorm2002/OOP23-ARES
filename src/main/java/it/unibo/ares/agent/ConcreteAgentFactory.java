package it.unibo.ares.agent;

import java.util.Optional;
import java.util.Set;
import java.util.stream.IntStream;

import it.unibo.ares.utils.Pos;
import it.unibo.ares.utils.PosImpl;
import it.unibo.ares.utils.State;
import it.unibo.ares.utils.parameters.ParameterImpl;




public class ConcreteAgentFactory {

private boolean isAgentOfSameType(Agent a, Agent b){
        Integer type1 = a.getParameters().getParameter("type", Integer.class).get().getValue();
        Integer type2 = b.getParameters().getParameter("type", Integer.class).get().getValue();
        return type1.equals(type2);
}

private boolean isThresholdSatisfied(State state, Pos pos, Agent agent) {
    Integer visionRadius = agent.getParameters().getParameter("visionRadius", Integer.class).get().getValue();
    Double threshold = agent.getParameters().getParameter("threshold", Double.class).get().getValue();

    Set<Agent> neighBors = state.getAgentsByPosAndRadius(pos, visionRadius);
    
    //Avoid division by zero
    if(neighBors.size() == 0){
        return true;
    }

    Double sameTypeCount = (double) neighBors.stream().filter(a -> isAgentOfSameType(a, agent)).count();

    Double ratio = sameTypeCount / neighBors.size();

    return ratio >= threshold;
}

private Optional<PosImpl> getFreePositionIfAvailable(State state, Agent agent) {
    return IntStream.range(0, state.getDimensions().getFirst())
        .mapToObj(x -> IntStream.range(0, state.getDimensions().getSecond())
            .mapToObj(y -> new PosImpl(x, y)))
        .flatMap(s -> s)
        .filter(p -> state.getAgentAt(p).isEmpty())
        .filter(p -> isThresholdSatisfied(state, p, agent))
        .findAny();
}

    Agent getSchellingSegregationModelAgent(Integer type, Double threshold, Integer visionRadius){
        AgentBuilder b  =  new AgentBuilderImpl();

        b.addParameter(new ParameterImpl<Integer>("type", type));
        b.addParameter(new ParameterImpl<Double>("threshold", threshold));
        b.addParameter(new ParameterImpl<Integer>("visionRadius", visionRadius));

        //Strategy: se non soddisfatto, si muove in una posizione casuale che soddisfi il threshold
        b.addStrategy((state, pos) -> {
            Agent agent = state.getAgentAt(pos).get();
            if(!isThresholdSatisfied(state, pos, agent )){
                Optional<PosImpl> newPos = getFreePositionIfAvailable(state, agent);
                newPos.ifPresent(p -> state.moveAgent(pos, newPos.get()));
            }
            return state;
        });

        return b.build();
    }
}

