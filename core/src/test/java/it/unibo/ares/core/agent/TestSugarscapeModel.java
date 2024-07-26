package it.unibo.ares.core.agent;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;

import it.unibo.ares.core.utils.Pair;
import it.unibo.ares.core.utils.pos.Pos;
import it.unibo.ares.core.utils.pos.PosImpl;
import it.unibo.ares.core.utils.state.State;
import it.unibo.ares.core.utils.state.StateImpl;

/**
 * Unit test for the Sugarscape model agents.
 */
class TestSugarscapeModel {

    private static final String MAXSUGAR = "maxSugar";
    private static final String SUGARAMOUNT = "sugarAmount";
    private static final String VISIONRADIUS = "visionRadius";
    private static final String METABOLISMRATE = "metabolismRate";
    private static final String SUGAR = "sugar";

    private State getInitialState(final int size, final int numConsumers, final int numSugars) {
        // CHECKSTYLE: MagicNumber OFF
        final State state = new StateImpl(size, size);

        final SugarAgentFactory sugarFactory = new SugarAgentFactory();
        final ConsumerAgentFactory consumerFactory = new ConsumerAgentFactory();

        for (int i = 0; i < numSugars; i++) {
            final Pos pos = new PosImpl(i, 0);
            state.addAgent(pos, sugarFactory.createAgent());
        }

        for (int i = 0; i < numConsumers; i++) {
            final Pos pos = new PosImpl(i, 1);
            state.addAgent(pos, consumerFactory.createAgent());
        }

        return state;
    }

    <T extends Serializable> void setAgentParameter(final State state, final Pos pos, final String key,
            final T value) {
        state.getAgentAt(pos)
                .ifPresent(agent -> agent.getParameters().setParameter(key, value));
    }

    @Test
    void testSugarscapeModelInitialization() {
        // CHECKSTYLE: MagicNumber OFF
        final State state = getInitialState(5, 2, 2);

        assertEquals(4, state.getAgents().size());

        // Check the types and initial parameters of the agents
        final long sugarAgents = state.getAgents().stream()
                .filter(a -> SugarAgentFactory.SUGAR.equals(a.getSecond().getType())).count();
        final long consumerAgents = state.getAgents().stream()
                .filter(a -> ConsumerAgentFactory.CONSUMER.equals(a.getSecond().getType())).count();

        assertEquals(2, sugarAgents);
        assertEquals(2, consumerAgents);
    }

    @Test
    void testSugarGrowth() {
        // CHECKSTYLE: MagicNumber OFF
        State state = getInitialState(5, 0, 1);

        final Pos pos = state.getAgents()
                .stream()
                .filter(a -> SugarAgentFactory.SUGAR.equals(a.getSecond().getType()))
                .findFirst()
                .get()
                .getFirst();

        setAgentParameter(state, pos, MAXSUGAR, 10);
        setAgentParameter(state, pos, SUGARAMOUNT, 2);
        setAgentParameter(state, pos, "growthRate", 3);

        state = state.getAgentAt(pos).get().tick(state, pos);

        final int sugarAmount = state.getAgentAt(pos).get().getParameters().getParameter(SUGARAMOUNT, Integer.class)
                .get()
                .getValue();
        assertEquals(5, sugarAmount); // 2 + 3 = 5
    }

    @Test
    void testConsumerMovementAndSugarConsumption() {
        // CHECKSTYLE: MagicNumber OFF
        State state = new StateImpl(5, 5);

        final SugarAgentFactory sugarFactory = new SugarAgentFactory();
        final ConsumerAgentFactory consumerFactory = new ConsumerAgentFactory();

        final Pos consumerPos = new PosImpl(1, 1);
        state.addAgent(consumerPos, consumerFactory.createAgent());

        setAgentParameter(state, consumerPos, VISIONRADIUS, 5);
        setAgentParameter(state, consumerPos, METABOLISMRATE, 4);
        setAgentParameter(state, consumerPos, SUGAR, 5);
        setAgentParameter(state, consumerPos, MAXSUGAR, 10);

        final Pos sugarPos = new PosImpl(1, 2);
        state.addAgent(sugarPos, sugarFactory.createAgent());
        setAgentParameter(state, sugarPos, SUGARAMOUNT, 5);

        state = state.getAgentAt(consumerPos).get().tick(state, consumerPos);

        final Optional<Pos> newConsumerPos = state.getAgents().stream()
                .filter(pair -> ConsumerAgentFactory.CONSUMER.equals(pair.getSecond().getType()))
                .map(Pair::getFirst).findFirst();
        assertTrue(newConsumerPos.isPresent());

        final int newSugarAmount = state.getAgentAt(newConsumerPos.get()).get().getParameters()
                .getParameter(SUGAR, Integer.class).get().getValue();
        assertEquals(6, newSugarAmount); // 5 - 4 + 5 = 6
    }

    @Test
    void testConsumerDeathDueToMetabolism() {
        // CHECKSTYLE: MagicNumber OFF
        State state = new StateImpl(5, 5);

        final ConsumerAgentFactory consumerFactory = new ConsumerAgentFactory();

        final Pos consumerPos = new PosImpl(1, 1);
        state.addAgent(consumerPos, consumerFactory.createAgent());
        setAgentParameter(state, consumerPos, VISIONRADIUS, 2);
        setAgentParameter(state, consumerPos, METABOLISMRATE, 5);
        setAgentParameter(state, consumerPos, SUGAR, 2);
        setAgentParameter(state, consumerPos, MAXSUGAR, 100);

        state = state.getAgentAt(consumerPos).get().tick(state, consumerPos);

        assertTrue(state.getAgents().stream()
                .noneMatch(pair -> ConsumerAgentFactory.CONSUMER.equals(pair.getSecond().getType())));
    }

    @Test
    void testConsumerMovesTowardsSugar() {
        // CHECKSTYLE: MagicNumber OFF
        State state = new StateImpl(5, 5);

        final SugarAgentFactory sugarFactory = new SugarAgentFactory();
        final ConsumerAgentFactory consumerFactory = new ConsumerAgentFactory();

        final Pos consumerPos = new PosImpl(1, 2);
        state.addAgent(consumerPos, consumerFactory.createAgent());

        setAgentParameter(state, consumerPos, VISIONRADIUS, 5);
        setAgentParameter(state, consumerPos, METABOLISMRATE, 1);
        setAgentParameter(state, consumerPos, SUGAR, 5);
        setAgentParameter(state, consumerPos, MAXSUGAR, 10);

        final Pos sugarPos = new PosImpl(4, 2);
        state.addAgent(sugarPos, sugarFactory.createAgent());
        setAgentParameter(state, sugarPos, SUGARAMOUNT, 3);

        state = state.getAgentAt(consumerPos).get().tick(state, consumerPos);

        final Optional<Pos> newConsumerPos = state.getAgents().stream()
                .filter(pair -> ConsumerAgentFactory.CONSUMER.equals(pair.getSecond().getType()))
                .map(Pair::getFirst)
                .findFirst();

        assertTrue(newConsumerPos.isPresent());
        assertEquals(2, newConsumerPos.get().getX());
        assertEquals(2, newConsumerPos.get().getY());
    }

    /*
     * Test the consumer moves towards the sugar with less competition even if it's
     * not the closest one.
     */
    @Test
    void testConsumerMovesTowardsSugarWithLessCompetion() {
        // CHECKSTYLE: MagicNumber OFF
        final State state = new StateImpl(10, 10);

        final SugarAgentFactory sugarFactory = new SugarAgentFactory();
        final ConsumerAgentFactory consumerFactory = new ConsumerAgentFactory();

        final Pos consumerPos1 = new PosImpl(2, 2);
        final Pos consumerPos2 = new PosImpl(5, 2);

        final List<Pos> consumerPositions = Arrays.asList(consumerPos1, consumerPos2);

        consumerPositions.forEach(pos -> {
            state.addAgent(pos, consumerFactory.createAgent());
            setAgentParameter(state, pos, VISIONRADIUS, 5);
            setAgentParameter(state, pos, METABOLISMRATE, 1);
            setAgentParameter(state, pos, SUGAR, 5);
            setAgentParameter(state, pos, MAXSUGAR, 10);
        });

        final Pos sugarPos1 = new PosImpl(3, 2);
        final Pos sugarPos2 = new PosImpl(8, 2);
        final List<Pos> sugarPositions = Arrays.asList(sugarPos1, sugarPos2);
        sugarPositions.forEach(pos -> {
            state.addAgent(pos, sugarFactory.createAgent());
            setAgentParameter(state, pos, SUGARAMOUNT, 3);
        });

        final State newState = state.getAgentAt(consumerPos2).get().tick(state, consumerPos2);

        final Optional<Pos> newConsumerPos = newState.getAgents().stream()
                .filter(pair -> ConsumerAgentFactory
                        .CONSUMER.equals(pair.getSecond().getType()))
                .filter(pair -> !pair.getFirst().equals(consumerPos1))
                .map(Pair::getFirst)
                .findFirst();

        assertTrue(newConsumerPos.isPresent());
        assertEquals(6, newConsumerPos.get().getX());
        assertEquals(2, newConsumerPos.get().getY());
    }

}
