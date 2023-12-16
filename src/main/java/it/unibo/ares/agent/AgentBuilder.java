package it.unibo.ares.agent;

import java.util.function.BiFunction;

import it.unibo.ares.utils.Pos;
import it.unibo.ares.utils.State;
import it.unibo.ares.utils.parameters.Parameter;

public interface AgentBuilder {
    Agent build();
    public void reset();
    public <T> AgentBuilder addParameter(Parameter<T> parameter);
    public AgentBuilder addStrategy(BiFunction<State, Pos, State> strategy);
}
