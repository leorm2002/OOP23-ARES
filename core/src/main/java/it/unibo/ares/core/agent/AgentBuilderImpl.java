package it.unibo.ares.core.agent;

import it.unibo.ares.core.utils.lambda.SerializableFunction;
import it.unibo.ares.core.utils.parameters.Parameter;
import it.unibo.ares.core.utils.parameters.Parameters;
import it.unibo.ares.core.utils.parameters.ParametersImpl;
import it.unibo.ares.core.utils.pos.Pos;
import it.unibo.ares.core.utils.state.State;
import it.unibo.ares.core.utils.lambda.SerializableBiFunction;

import java.io.Serializable;
import java.util.UUID;
import java.util.function.BiFunction;

/**
 * Implementation of the AgentBuilder interface.
 */
class AgentBuilderImpl implements AgentBuilder {

    private SerializableBiFunction<State, Pos, State> strategy;
    private Parameters parameters;

    /**
     * Constructs a new AgentBuilderImpl object.
     */
    AgentBuilderImpl() {
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
        final String id = UUID.randomUUID().toString();
        return new Agent() {

            private String type;
            private static final long serialVersionUID = 1L;

            @Override
            public State tick(final State state, final Pos pos) {
                return strategy.apply(state, pos);
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
            public String getId() {
                return id;
            }

            @Override
            public boolean equals(final Object obj) {
                if (this == obj) {
                    return true;
                }
                if (obj == null || getClass() != obj.getClass()) {
                    return false;
                }
                final Agent agent = (Agent) obj;
                return id.equals(agent.getId());
            }

            @Override
            public int hashCode() {
                final int prime = 31;
                int result = 1;
                result = prime * result + ((id == null) ? 0 : id.hashCode());
                return result;
            }

            @Override
            public String getType() {
                return type;
            }

            @Override
            public void setType(final String type) {
                this.type = type;
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
    public <T extends Serializable> AgentBuilder addParameter(final Parameter<T> parameter) {
        parameters.addParameter(parameter);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AgentBuilder addStrategy(final SerializableBiFunction<State, Pos, State> strategy) {
        if (strategy == null) {
            throw new IllegalStateException("Strategy cannot be null");
        }
        this.strategy = strategy;
        return this;
    }
}
