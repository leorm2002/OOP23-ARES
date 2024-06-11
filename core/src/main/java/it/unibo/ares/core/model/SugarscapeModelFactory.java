package it.unibo.ares.core.model;

import it.unibo.ares.core.agent.SugarAgentFactory;
import it.unibo.ares.core.agent.ConsumerAgentFactory;
import it.unibo.ares.core.utils.UniquePositionGetter;
import it.unibo.ares.core.utils.parameters.ParameterDomainImpl;
import it.unibo.ares.core.utils.parameters.ParameterImpl;
import it.unibo.ares.core.utils.parameters.Parameters;
import it.unibo.ares.core.utils.pos.Pos;
import it.unibo.ares.core.utils.pos.PosImpl;
import it.unibo.ares.core.utils.state.State;
import it.unibo.ares.core.utils.state.StateImpl;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

public final class SugarscapeModelFactory implements ModelFactory {

    private static final long serialVersionUID = 1L;
    private static final String MODEL_ID = "Sugarscape";

    @Override
    public String getModelId() {
        return MODEL_ID;
    }

    private State sugarscapeInitializer(final Parameters parameters) throws IllegalAccessException {
        final int size = parameters.getParameter(
                Model.SIZEKEY, Integer.class)
                .orElseThrow(IllegalAccessException::new).getValue();
        final int numAgentsConsumer = parameters.getParameter("numeroAgentiConsumer", Integer.class)
                .orElseThrow(IllegalAccessException::new).getValue();

        final int numAgentsSugar = parameters.getParameter("numeroAgentiSugar", Integer.class)
                .orElseThrow(IllegalAccessException::new).getValue();

        if (size * size < numAgentsConsumer + numAgentsSugar) {
            throw new IllegalArgumentException("The number of agents is greater than the size of the grid");
        }
        final StateImpl state = new StateImpl(size, size);

        final List<Pos> validPositions = IntStream.range(0, size).boxed()
                .flatMap(i -> IntStream.range(0, size).mapToObj(j -> new PosImpl(i, j)))
                .collect(Collectors.toList());

        final UniquePositionGetter getter = new UniquePositionGetter(validPositions);
        final SugarAgentFactory sugarFactory = new SugarAgentFactory();
        final ConsumerAgentFactory consumerFactory = new ConsumerAgentFactory();

        Stream.generate(
                sugarFactory::createAgent)
                .limit(numAgentsSugar).forEach(a -> state.addAgent(getter.next(), a));

        Stream.generate(
                consumerFactory::createAgent)
                .limit(numAgentsConsumer).forEach(a -> state.addAgent(getter.next(), a));

        return state;
    }

    @Override
    @SuppressWarnings("PMD.PreserveStackTrace") // La causa è sempre qella
    public Model getModel() {
        return new ModelBuilderImpl()
                .addParameter(new ParameterImpl<>("numeroAgentiConsumer", Integer.class,
                        new ParameterDomainImpl<>("Numero di agenti consumer",
                                (Integer n) -> n >= 0),
                        true))
                .addParameter(new ParameterImpl<>("numeroAgentiSugar", Integer.class,
                        new ParameterDomainImpl<>("Numero di agenti sugar",
                                (Integer n) -> n >= 0),
                        true))
                .addParameter(new ParameterImpl<>(Model.SIZEKEY, Integer.class,
                        new ParameterDomainImpl<>("Dimensione della griglia",
                                (Integer n) -> n >= 0),
                        true))
                .addExitFunction(
                        (o, n) -> n.getAgents().stream().map(a -> a.getSecond().getType())
                                .distinct().count() == 1)
                .addInitFunction(params -> {
                    try {
                        return sugarscapeInitializer(params);
                    } catch (IllegalAccessException e) {
                        throw new IllegalArgumentException(
                                "Missing parameters for the model initialization");
                    }
                })
                .build();
    }
}