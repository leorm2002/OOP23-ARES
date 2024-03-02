package it.unibo.ares.core.model;

import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.Function;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import it.unibo.ares.core.agent.Agent;
import it.unibo.ares.core.utils.Pair;
import it.unibo.ares.core.utils.parameters.Parameter;
import it.unibo.ares.core.utils.parameters.Parameters;
import it.unibo.ares.core.utils.parameters.ParametersImpl;
import it.unibo.ares.core.utils.pos.Pos;
import it.unibo.ares.core.utils.state.State;

@SuppressFBWarnings(value = {
        "UWF_FIELD_NOT_INITIALIZED_IN_CONSTRUCTOR"
}, justification = "C'è un controllo esplicito che la init function "
        + "la exit function e i parameters siano stati inizializzati.")
final class ModelBuilderImpl implements ModelBuilder {
    private Parameters parameters;
    private BiPredicate<State, State> exitfFunction;
    private Function<Parameters, State> initFunction;

    ModelBuilderImpl() {
        reset();
    }

    @Override
    public void reset() {
        this.parameters = new ParametersImpl();
        this.exitfFunction = null;
        this.initFunction = null;
    }

    @Override
    public <T> ModelBuilder addParameter(final Parameter<T> parameter) {
        if (parameter == null) {
            throw new IllegalArgumentException("Parameter cannot be null");
        }
        if (parameters == null) {
            parameters = new ParametersImpl();
        }
        this.parameters.addParameter(parameter);

        return this;
    }

    @Override
    public ModelBuilder addExitFunction(final BiPredicate<State, State> exitfFunction) {
        if (exitfFunction == null) {
            throw new IllegalArgumentException("Exit function cannot be null");
        }
        this.exitfFunction = exitfFunction;
        return this;
    }

    @Override
    public Model build() {
        if (parameters == null) {
            throw new IllegalStateException("Parameters not set");
        }
        if (exitfFunction == null) {
            throw new IllegalStateException("Exit function not set");
        }
        if (initFunction == null) {
            throw new IllegalStateException("Init function not set");
        }
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
        if (initFunction == null) {
            throw new IllegalArgumentException("Init function cannot be null");
        }
        this.initFunction = initFunction;

        return this;
    }

}
