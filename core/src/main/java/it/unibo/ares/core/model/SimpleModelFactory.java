package it.unibo.ares.core.model;

import it.unibo.ares.core.agent.AgentFactory;
import it.unibo.ares.core.agent.SimpleAgentFactory;
import it.unibo.ares.core.utils.parameters.ParameterDomainImpl;
import it.unibo.ares.core.utils.parameters.ParameterImpl;
import it.unibo.ares.core.utils.pos.PosImpl;
import it.unibo.ares.core.utils.state.State;
import it.unibo.ares.core.utils.state.StateImpl;

/**
 * Provides a mock model for testing purposes.
 * 
 */
public final class SimpleModelFactory implements ModelFactory {
    private static final String MODEL_ID = "Simple Model";
    private final AgentFactory agentProvider;

    /**
     * Creates a simple model factory.
     */
    public SimpleModelFactory() {
        this.agentProvider = new SimpleAgentFactory();
    }

    @Override
    public String getModelId() {
        return MODEL_ID;
    }

    @Override
    public Model getModel() {
        return new ModelBuilderImpl()
                .addParameter(new ParameterImpl<>(
                        Model.SIZEKEY, Integer.class,
                        new ParameterDomainImpl<Integer>(
                                "Dimensione della griglia (1-n)",
                                i -> i > 0),
                        true))
                .addExitFunction((state, state2) -> !state2.isFree(new PosImpl(0, 0)))
                .addInitFunction(params -> {
                    Integer size = params.getParameter(
                            Model.SIZEKEY, Integer.class)
                            .orElseThrow(() -> new IllegalArgumentException("No size parameter"))
                            .getValue();
                    State state = new StateImpl(size, size);
                    state.addAgent(new PosImpl(0, 0), agentProvider.createAgent());
                    return state;
                })
                .build();
    }
}
