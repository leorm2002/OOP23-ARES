package it.unibo.ares.core.agent;

import java.util.function.BiFunction;

import it.unibo.ares.core.utils.parameters.Parameter;
import it.unibo.ares.core.utils.parameters.Parameters;
import it.unibo.ares.core.utils.parameters.ParametersImpl;
import it.unibo.ares.core.utils.pos.Pos;
import it.unibo.ares.core.utils.state.State;

/**
 * Implementation of the AgentBuilder interface.
 */
public class AgentBuilderImpl implements AgentBuilder {
    private BiFunction<State, Pos, State> strategy;
    private Parameters parameters;

    /**
     * Constructs a new AgentBuilderImpl object.
     */
    public AgentBuilderImpl() {
        this.strategy = null;
        this.parameters = new ParametersImpl();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Agent build() {
        if (strategy == null) {
            throw new IllegalStateException("Cannot build agent without strategy or parameters");
        }
        return new Agent() {

            @Override
            public State tick(final State state, final  Pos pos) {
                return strategy.apply(state, pos);
            }

            @Override
            public Parameters getParameters() {
                return parameters;
            }

            @Override
            public <T> void setParameter(final String key, final T value) {
                parameters.setParameter(key, value);
            }
        };
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void reset() {
        this.strategy = null;
        this.parameters = new ParametersImpl();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public <T> AgentBuilder addParameter(final Parameter<T> parameter) {
        parameters.addParameter(parameter);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AgentBuilder addStrategy(final BiFunction<State, Pos, State> strategy) {
        if (strategy == null) {
            throw new NullPointerException("Strategy cannot be null");
        }
        this.strategy = strategy;
        return this;
    }
}
