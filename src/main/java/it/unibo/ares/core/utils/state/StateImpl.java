package it.unibo.ares.core.utils.state;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import it.unibo.ares.core.agent.Agent;
import it.unibo.ares.core.agent.Entity;
import it.unibo.ares.core.utils.Pair;
import it.unibo.ares.core.utils.board.Board;
import it.unibo.ares.core.utils.board.BoardImpl;
import it.unibo.ares.core.utils.pos.Pos;
import it.unibo.ares.core.utils.pos.PosImpl;

/**
 * Implementation of the State interface that represents the state of a
 * simulation.
 * It manages the entities and agents on a board.
 */
public final class StateImpl implements State {
    private final Board<Entity> entityBoard = new BoardImpl<>();
    private final Board<Agent> agentBoard = new BoardImpl<>();
    private final Pair<Integer, Integer> size;

    /**
     * Constructs a new State object with the specified dimensions.
     *
     * @param width  the width of the state
     * @param height the height of the state
     */
    public StateImpl(final Integer width, final Integer height) {
        this.size = new Pair<Integer, Integer>(width, height);
    }

    private Boolean isValidPosition(final Pos pos) {
        return pos.getX() >= 0 && pos.getX() < size.getFirst()
                && pos.getY() >= 0 && pos.getY() < size.getSecond();
    }

    private void assertInsideBoard(final Pos pos) {
        if (!isValidPosition(pos)) {
            throw new IllegalArgumentException("Position " + pos + " is outside the board");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<Pair<Pos, Entity>> getEntities() {
        return entityBoard.getEntities();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<Pair<Pos, Agent>> getAgents() {
        return agentBoard.getEntities();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addAgent(final Pos pos, final Agent agent) {
        assertInsideBoard(pos);
        agentBoard.addEntity(pos, agent);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeAgent(final Pos pos, final Agent agent) {
        agentBoard.removeEntity(pos, agent);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void moveAgent(final Pos from, final Pos to) {
        assertInsideBoard(to);
        Agent agent = agentBoard.getEntity(from).get();
        agentBoard.removeEntity(from, agent);
        agentBoard.addEntity(to, agent);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addEntity(final Pos pos, final Entity entity) {
        assertInsideBoard(pos);
        entityBoard.addEntity(pos, entity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeEntity(final Pos pos, final Entity entity) {
        entityBoard.removeEntity(pos, entity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void moveEntity(final Pos from, final Pos to) {
        assertInsideBoard(to);
        Entity entity = entityBoard.getEntity(from).get();
        entityBoard.removeEntity(from, entity);
        entityBoard.addEntity(to, entity);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Agent> getAgentAt(final Pos pos) {
        return agentBoard.getEntity(pos);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Optional<Entity> getEntityAt(final Pos pos) {
        return entityBoard.getEntity(pos);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Pair<Integer, Integer> getDimensions() {
        return this.size;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<Pos> getPosByPosAndRadius(final Pos pos, final Integer radius) {
        return IntStream.rangeClosed(pos.getX() - radius, pos.getX() + radius)
                .boxed()
                .flatMap(x -> IntStream.rangeClosed(pos.getY() - radius, pos.getY() + radius)
                        .mapToObj(y -> new PosImpl(x, y)))
                .filter(this::isValidPosition)
                .filter(p -> !p.equals(pos))
                .collect(Collectors.toSet());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<Agent> getAgentsByPosAndRadius(final Pos pos, final Integer radius) {
        return getPosByPosAndRadius(pos, radius).stream()
                .map(this::getAgentAt)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toSet());
    }

    @Override
    public void debugPrint() {
        var out = IntStream.range(0, size.getFirst())
                .boxed()
                .map(x -> IntStream.range(0, size.getSecond())
                        .mapToObj(y -> new PosImpl(x, y))
                        .map(pos -> {
                            var a = getAgentAt(pos);
                            if (a.isPresent()) {
                                return a.get().getType();
                            } else {
                                return " ";
                            }
                        }).collect(Collectors.joining(" ")))
                .collect(Collectors.joining("\n"));
        System.out.println(out);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<Agent> getAgentsFromASetOfPos(final Set<Pos> positions) {
        return positions.stream().map(this::getAgentAt)
                .filter(Optional::isPresent).map(Optional::get).collect(Collectors.toSet());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Set<Entity> getEntitiesFromASetOfPos(final Set<Pos> positions) {
        return positions.stream()
                .map(this::getEntityAt)
                .filter(Optional::isPresent).map(Optional::get).collect(Collectors.toSet());
    }

    @Override
    public State copy() {
        StateImpl copy = new StateImpl(size.getFirst(), size.getSecond());
        entityBoard.getEntities().forEach(e -> copy.addEntity(e.getFirst(), e.getSecond()));
        agentBoard.getEntities().forEach(e -> copy.addAgent(e.getFirst(), e.getSecond()));
        return copy;
    }

    @Override
    public boolean isFree(Pos pos) {
        return entityBoard.getEntity(pos).isEmpty() && agentBoard.getEntity(pos).isEmpty();
    }
}
