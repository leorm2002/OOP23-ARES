package it.unibo.ares.core.agent;

import it.unibo.ares.core.utils.pos.Pos;
import it.unibo.ares.core.utils.pos.PosImpl;
import it.unibo.ares.core.utils.state.State;
import it.unibo.ares.core.utils.state.StateImpl;
import org.junit.jupiter.api.Test;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Unit test for the Sugarscape model agents.
 */
class TestSugarscapeModel {

    private State getInitialState(final int size, final int numConsumers, final int numSugars) {
        // CHECKSTYLE: MagicNumber OFF
        State state = new StateImpl(size, size);

        SugarAgentFactory sugarFactory = new SugarAgentFactory();
        ConsumerAgentFactory consumerFactory = new ConsumerAgentFactory();

        for (int i = 0; i < numSugars; i++) {
            Pos pos = new PosImpl(i, 0);
            state.addAgent(pos, sugarFactory.createAgent());
        }

        for (int i = 0; i < numConsumers; i++) {
            Pos pos = new PosImpl(i, 1);
            state.addAgent(pos, consumerFactory.createAgent());
        }

        return state;
    }

    private <T extends Serializable> void setAgentParameter(final State state, final Pos pos, final String key,
            final T value) {
        state.getAgentAt(pos)
                .ifPresent(agent -> agent.getParameters().setParameter(key, value));
    }

    @Test
    void testSugarscapeModelInitialization() {
        // CHECKSTYLE: MagicNumber OFF
        State state = getInitialState(5, 2, 2);

        assertEquals(4, state.getAgents().size());

        // Check the types and initial parameters of the agents
        long sugarAgents = state.getAgents().stream()
                .filter(a -> a.getSecond().getType().equals(SugarAgentFactory.SUGAR)).count();
        long consumerAgents = state.getAgents().stream()
                .filter(a -> a.getSecond().getType().equals(ConsumerAgentFactory.CONSUMER)).count();

        assertEquals(2, sugarAgents);
        assertEquals(2, consumerAgents);
    }

    @Test
    void testSugarGrowth() {
        // CHECKSTYLE: MagicNumber OFF
        State state = getInitialState(5, 0, 1);

        Pos pos = state.getAgents()
                .stream()
                .filter(a -> a.getSecond().getType().equals(SugarAgentFactory.SUGAR))
                .findFirst()
                .get()
                .getFirst();

        setAgentParameter(state, pos, "maxSugar", 10);
        setAgentParameter(state, pos, "sugarAmount", 2);
        setAgentParameter(state, pos, "growthRate", 3);

        state = state.getAgentAt(pos).get().tick(state, pos);

        int sugarAmount = state.getAgentAt(pos).get().getParameters().getParameter("sugarAmount", Integer.class).get()
                .getValue();
        assertEquals(5, sugarAmount); // 2 + 3 = 5
    }

    @Test
    void testConsumerMovementAndSugarConsumption() {
        // CHECKSTYLE: MagicNumber OFF
        State state = new StateImpl(5, 5);

        SugarAgentFactory sugarFactory = new SugarAgentFactory();
        ConsumerAgentFactory consumerFactory = new ConsumerAgentFactory();

        Pos consumerPos = new PosImpl(1, 1);
        state.addAgent(consumerPos, consumerFactory.createAgent());

        setAgentParameter(state, consumerPos, "visionRadius", 5);
        setAgentParameter(state, consumerPos, "metabolismRate", 4);
        setAgentParameter(state, consumerPos, "sugar", 5);
        setAgentParameter(state, consumerPos, "maxSugar", 10);

        Pos sugarPos = new PosImpl(1, 2);
        state.addAgent(sugarPos, sugarFactory.createAgent());
        setAgentParameter(state, sugarPos, "sugarAmount", 5);

        state = state.getAgentAt(consumerPos).get().tick(state, consumerPos);

        Optional<Pos> newConsumerPos = state.getAgents().stream()
                .filter(pair -> pair.getSecond().getType().equals(ConsumerAgentFactory.CONSUMER))
                .map(pair -> pair.getFirst()).findFirst();
        assertTrue(newConsumerPos.isPresent());

        int newSugarAmount = state.getAgentAt(newConsumerPos.get()).get().getParameters()
                .getParameter("sugar", Integer.class).get().getValue();
        assertEquals(6, newSugarAmount); // 5 - 4 + 5 = 6
    }

    @Test
    void testConsumerDeathDueToMetabolism() {
        // CHECKSTYLE: MagicNumber OFF
        State state = new StateImpl(5, 5);

        ConsumerAgentFactory consumerFactory = new ConsumerAgentFactory();

        Pos consumerPos = new PosImpl(1, 1);
        state.addAgent(consumerPos, consumerFactory.createAgent());
        setAgentParameter(state, consumerPos, "visionRadius", 2);
        setAgentParameter(state, consumerPos, "metabolismRate", 5);
        setAgentParameter(state, consumerPos, "sugar", 2);
        setAgentParameter(state, consumerPos, "maxSugar", 100);

        state = state.getAgentAt(consumerPos).get().tick(state, consumerPos);

        assertTrue(state.getAgents().stream()
                .noneMatch(pair -> pair.getSecond().getType().equals(ConsumerAgentFactory.CONSUMER)));
    }

    @Test
    void testConsumerMovesTowardsSugar() {
        // CHECKSTYLE: MagicNumber OFF
        State state = new StateImpl(5, 5);

        SugarAgentFactory sugarFactory = new SugarAgentFactory();
        ConsumerAgentFactory consumerFactory = new ConsumerAgentFactory();

        Pos consumerPos = new PosImpl(1, 2);
        state.addAgent(consumerPos, consumerFactory.createAgent());

        setAgentParameter(state, consumerPos, "visionRadius", 5);
        setAgentParameter(state, consumerPos, "metabolismRate", 1);
        setAgentParameter(state, consumerPos, "sugar", 5);
        setAgentParameter(state, consumerPos, "maxSugar", 10);

        Pos sugarPos = new PosImpl(4, 2);
        state.addAgent(sugarPos, sugarFactory.createAgent());
        setAgentParameter(state, sugarPos, "sugarAmount", 3);

        state = state.getAgentAt(consumerPos).get().tick(state, consumerPos);

        Optional<Pos> newConsumerPos = state.getAgents().stream()
                .filter(pair -> pair.getSecond().getType().equals(ConsumerAgentFactory.CONSUMER))
                .map(pair -> pair.getFirst())
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

        SugarAgentFactory sugarFactory = new SugarAgentFactory();
        ConsumerAgentFactory consumerFactory = new ConsumerAgentFactory();

        Pos consumerPos1 = new PosImpl(2, 2);
        Pos consumerPos2 = new PosImpl(5, 2);

        List<Pos> consumerPositions = Arrays.asList(consumerPos1, consumerPos2);

        consumerPositions.forEach(pos -> {
            state.addAgent(pos, consumerFactory.createAgent());
            setAgentParameter(state, pos, "visionRadius", 5);
            setAgentParameter(state, pos, "metabolismRate", 1);
            setAgentParameter(state, pos, "sugar", 5);
            setAgentParameter(state, pos, "maxSugar", 10);
        });

        Pos sugarPos1 = new PosImpl(3, 2);
        Pos sugarPos2 = new PosImpl(8, 2);
        List<Pos> sugarPositions = Arrays.asList(sugarPos1, sugarPos2);
        sugarPositions.forEach(pos -> {
            state.addAgent(pos, sugarFactory.createAgent());
            setAgentParameter(state, pos, "sugarAmount", 3);
        });

        final State newState = state.getAgentAt(consumerPos2).get().tick(state, consumerPos2);

        Optional<Pos> newConsumerPos = newState.getAgents().stream()
                .filter(pair -> pair.getSecond().getType().equals(ConsumerAgentFactory.CONSUMER))
                .filter(pair -> !pair.getFirst().equals(consumerPos1))
                .map(pair -> pair.getFirst())
                .findFirst();

        assertTrue(newConsumerPos.isPresent());
        assertEquals(6, newConsumerPos.get().getX());
        assertEquals(2, newConsumerPos.get().getY());
    }

}
