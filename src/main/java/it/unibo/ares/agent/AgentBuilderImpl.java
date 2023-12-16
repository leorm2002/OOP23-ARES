package it.unibo.ares.agent;

import java.util.function.BiFunction;

import it.unibo.ares.utils.Pos;
import it.unibo.ares.utils.State;
import it.unibo.ares.utils.parameters.Parameter;
import it.unibo.ares.utils.parameters.Parameters;

public class AgentBuilderImpl implements AgentBuilder{
    private BiFunction<State, Pos, State> strategy;
    private Parameters parameters;

    @Override
    public Agent build() {
        return new Agent(){

            @Override
            public State tick(State state, Pos pos) {
                return strategy.apply(state, pos);
            }

            @Override
            public Parameters getParameters() {
                return parameters;
            }

            @Override
            public <T> void setParameter(String key, T value) {
                parameters.setParameter(key, value);
            }
            
        };
    }

    @Override
    public void reset() {
        this.strategy = null;
        //parameters clear

    }

    @Override
    public <T> AgentBuilder addParameter(Parameter<T> parameter) {
        parameters.addParameter(parameter);
        return this;
    }

    @Override
    public AgentBuilder addStrategy(BiFunction<State, Pos, State> strategy) {
        this.strategy = strategy;
        return this;
    }

}
