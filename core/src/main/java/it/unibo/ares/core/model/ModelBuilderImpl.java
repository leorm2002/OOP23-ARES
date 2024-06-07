package it.unibo.ares.core.model;

import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import it.unibo.ares.core.agent.Agent;
import it.unibo.ares.core.utils.Pair;
import it.unibo.ares.core.utils.lambda.SerializableBiPredicate;
import it.unibo.ares.core.utils.lambda.SerializableFunction;
import it.unibo.ares.core.utils.parameters.Parameter;
import it.unibo.ares.core.utils.parameters.Parameters;
import it.unibo.ares.core.utils.parameters.ParametersImpl;
import it.unibo.ares.core.utils.pos.Pos;
import it.unibo.ares.core.utils.state.State;
import it.unibo.ares.core.utils.statistics.Statistics;
import it.unibo.ares.core.utils.statistics.StatisticsGenerator;

import java.io.Serializable;
import java.util.Collections;
import java.util.Set;
import java.util.function.BiPredicate;
import java.util.function.Function;

@SuppressFBWarnings(value = {
        "UWF_FIELD_NOT_INITIALIZED_IN_CONSTRUCTOR"
}, justification = "C'è un controllo esplicito che la init function "
        + "la exit function e i parameters siano stati inizializzati.")
final class ModelBuilderImpl implements ModelBuilder {
    private Parameters parameters;
    private SerializableBiPredicate<State, State> exitfFunction;
    private SerializableFunction<Parameters, State> initFunction;
    private transient StatisticsGenerator generator;

    ModelBuilderImpl() {
        reset();
    }

    @Override
    public void reset() {
        this.parameters = new ParametersImpl();
        this.exitfFunction = null;
        this.initFunction = null;
        this.generator = null;
    }

    @Override
    public <T extends Serializable> ModelBuilder addParameter(final Parameter<T> parameter) {
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
    public ModelBuilder addExitFunction(final SerializableBiPredicate<State, State> exitfFunction) {
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
                final Set<Pair<Pos, Agent>> agents = state.getAgents();
                final State newState = state.copy();
                for (final Pair<Pos, Agent> pair : agents) {
                    final Agent agent = pair.getSecond();
                    final Pos pos = pair.getFirst();
                    if (newState.getAgentAt(pos).isPresent() && newState.getAgentAt(pos).get().equals(agent)) {
                        agent.tick(newState, pos);
                    }
                }
                return newState;
            }

            @Override
            public Parameters getParameters() {
                return parameters;
            }

            @Override
            public <T extends Serializable> void setParameter(final String key, final T value) {
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

            @Override
            public Statistics getStatistics(final State s) {
                return generator == null ? Collections::emptyList : generator.generate(s);
            }
        };
    }

    @Override
    public ModelBuilder addInitFunction(final SerializableFunction<Parameters, State> initFunction) {
        if (initFunction == null) {
            throw new IllegalArgumentException("Init function cannot be null");
        }
        this.initFunction = initFunction;

        return this;
    }

    @Override
    public ModelBuilder addStatisticsGenerator(final StatisticsGenerator generator) {
        this.generator = generator;
        return this;
    }

}
