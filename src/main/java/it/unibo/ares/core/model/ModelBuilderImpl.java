package it.unibo.ares.core.model;

import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;

import it.unibo.ares.core.agent.Agent;
import it.unibo.ares.core.utils.Pair;
import it.unibo.ares.core.utils.parameters.Parameter;
import it.unibo.ares.core.utils.parameters.Parameters;
import it.unibo.ares.core.utils.state.State;

public class ModelBuilderImpl implements ModelBuilder {

    @Override
    public void reset() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'reset'");
    }

    @Override
    public <T> ModelBuilder addParameter(Parameter<T> parameter) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'addParameter'");
    }

    @Override
    public ModelBuilder addExitFunction(Predicate<State> exitfFunction) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'addExitFunction'");
    }

    @Override
    public Pair<Model, State> build() {
        Model model = new Model() {

            @Override
            public State tick(State state) {
                // TODO Auto-generated method stub
                throw new UnsupportedOperationException("Unimplemented method 'tick'");
            }

            @Override
            public Parameters getParameters() {
                // TODO Auto-generated method stub
                throw new UnsupportedOperationException("Unimplemented method 'getParameters'");
            }

            @Override
            public <T> void setParameter(String key, T value) {
                // TODO Auto-generated method stub
                throw new UnsupportedOperationException("Unimplemented method 'setParameter'");
            }

            @Override
            public boolean isOver(State state) {
                // TODO Auto-generated method stub
                throw new UnsupportedOperationException("Unimplemented method 'isOver'");
            }

            @Override
            public void initilize() {
                // TODO Auto-generated method stub
                throw new UnsupportedOperationException("Unimplemented method 'initilize'");
            }

            @Override
            public Set<Agent> getAgentsSimplified() {
                // TODO Auto-generated method stub
                throw new UnsupportedOperationException("Unimplemented method 'getAgentsSimplified'");
            }

        };
        return null;
    }

    @Override
    public ModelBuilder addInitFunction(Function<Parameters, State> initFunction) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'addInitFunction'");
    }

}
