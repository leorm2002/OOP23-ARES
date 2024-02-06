package it.unibo.ares.model;

import java.util.Set;

import it.unibo.ares.agent.Agent;
import it.unibo.ares.utils.Pair;
import it.unibo.ares.utils.parameters.Parameters;
import it.unibo.ares.utils.pos.Pos;
import it.unibo.ares.utils.state.State;

public class ModelImplA implements Model {
    UpdateMode updateMode;
    Parameters parameters;
    @Override
    public State tick(final State state) {
        //For each agent in the state, call the tick method
        Set<Pair<Pos,Agent>> agents = state.getAgents();
        State newState = state; //TODO: deep copy of the state
        switch (updateMode) {
            case RANDOM:
                for (var agent : agents) {
                    newState = agent.getSecond().tick(newState, agent.getFirst());
                }
                break;
            case SEQUENTIAL:
                //Sorta the agents by position
                //For each agent in the state, call the tick method
                for (var agent : agents) {
                    newState = agent.getSecond().tick(newState, agent.getFirst());
                }
                //Do nothing
                break;
            default:
                throw new UnsupportedOperationException("Unimplemented update mode");
        }
        return newState;
    }

    @Override
    public Parameters getParameters() {
        return parameters;
    }

    @Override
    public <T> void setParameter(String key, T value) {
        parameters.setParameter(key, value);
    }
    
}
