package it.unibo.ares.core.model;

import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.Function;

import it.unibo.ares.core.agent.Agent;
import it.unibo.ares.core.utils.Pair;
import it.unibo.ares.core.utils.parameters.Parameter;
import it.unibo.ares.core.utils.parameters.Parameters;
import it.unibo.ares.core.utils.parameters.ParametersImpl;
import it.unibo.ares.core.utils.pos.Pos;
import it.unibo.ares.core.utils.state.State;

class ModelBuilderImpl implements ModelBuilder {
    private Parameters parameters;
    private BiPredicate<State, State> exitfFunction;
    private Function<Parameters, State> initFunction;

    @Override
    public void reset() {
        this.parameters = null;
        this.exitfFunction = null;
        this.initFunction = null;
    }

    @Override
    public <T> ModelBuilder addParameter(final Parameter<T> parameter) {
        if (parameters == null) {
            parameters = new ParametersImpl();
        }
        parameters.addParameter(parameter);

        return this;
    }

    @Override
    public ModelBuilder addExitFunction(final BiPredicate<State, State> exitfFunction) {
        this.exitfFunction = exitfFunction;
        return this;
    }

    @Override
    public Model build() {
        return new Model() {

            @Override
            public State tick(final State state) {
                Set<Pair<Pos, Agent>> agents = state.getAgents();
                State newState = state.copy();
                for (Pair<Pos, Agent> pair : agents) {
                    Agent agent = pair.getSecond();
                    Pos pos = pair.getFirst();
                    agent.tick(newState, pos);
                }
                return newState;
            }

            @Override
            public Parameters getParameters() {
                return parameters;
            }

            @Override
            public <T> void setParameter(final String key, final T value) {
                parameters.setParameter(key, value);
            }

            @Override
            public boolean isOver(final State oldState, final State newState) {
                return exitfFunction.test(oldState, newState);
            }

            @Override
            public State initilize() {
                return initFunction.apply(parameters);
            }
        };
    }

    @Override
    public ModelBuilder addInitFunction(final Function<Parameters, State> initFunction) {
        this.initFunction = initFunction;

        return this;
    }

}
