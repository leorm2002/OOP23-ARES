package it.unibo.ares.core.utils.state;

import it.unibo.ares.core.agent.Agent;
import it.unibo.ares.core.agent.Entity;
import it.unibo.ares.core.utils.Pair;
import it.unibo.ares.core.utils.pos.Pos;

import java.util.Optional;
import java.util.Set;
import java.io.Serializable;

/**
 * Represents the state of a system.
 */
public interface State extends Serializable {
    /**
     * Retrieves the entities in the state along with their positions.
     *
     * @return a set of pairs containing the position and entity.
     */
    Set<Pair<Pos, Entity>> getEntities();

    /**
     * Retrieves the agents in the state along with their positions.
     *
     * @return a set of pairs containing the position and agent.
     */
    Set<Pair<Pos, Agent>> getAgents();

    /**
     * Adds an agent to the state at the specified position.
     *
     * @param pos   the position to add the agent.
     * @param agent the agent to add.
     * @throws IllegalArgumentException if the position is already occupied.
     */
    void addAgent(Pos pos, Agent agent);

    /**
     * Removes an agent from the state at the specified position.
     *
     * @param pos   the position to remove the agent.
     * @param agent the agent to remove.
     * @throws IllegalArgumentException if the agent is not found at the specified
     *                                  position.
     */
    void removeAgent(Pos pos, Agent agent);

    /**
     * Moves an agent from one position to another in the state.
     *
     * @param from the current position of the agent.
     * @param to   the new position for the agent.
     * @throws IllegalArgumentException if the agent is not found at the specified
     *                                  position.
     * @throws IllegalArgumentException if the position is already occupied.
     */
    void moveAgent(Pos from, Pos to);

    /**
     * Retrieves the agent at the specified position, if any.
     *
     * @param pos the position to check for an agent.
     * @return an optional containing the agent, or empty if no agent is found.
     */
    Optional<Agent> getAgentAt(Pos pos);

    /**
     * Retrieves the entity at the specified position, if any.
     *
     * @param pos the position to check for an entity.
     * @return an optional containing the entity, or empty if no entity is found.
     */
    Optional<Entity> getEntityAt(Pos pos);

    /**
     * Adds an entity to the state at the specified position.
     *
     * @param pos    the position to add the entity.
     * @param entity the entity to add.
     * @throws IllegalArgumentException if the position is already occupied.
     * @throws IllegalArgumentException if the entity is already present in the
     *                                  state.
     */
    void addEntity(Pos pos, Entity entity);

    /**
     * Removes an entity from the state at the specified position.
     *
     * @param pos    the position to remove the entity.
     * @param entity the entity to remove.
     * @throws IllegalArgumentException if the entity is not found at the specified
     *                                  position.
     */
    void removeEntity(Pos pos, Entity entity);

    /**
     * Moves an entity from one position to another in the state.
     *
     * @param from the current position of the entity.
     * @param to   the new position for the entity.
     * @throws IllegalArgumentException if the entity is not found at the specified
     *                                  position.
     * @throws IllegalArgumentException if the position is already occupied.
     */
    void moveEntity(Pos from, Pos to);

    /**
     * Retrieves the dimensions of the state.
     *
     * @return a pair containing the width and height of the state.
     */
    Pair<Integer, Integer> getDimensions();

    /**
     * Retrieves the agents within a specified radius of a given position.
     *
     * @param pos    the center position.
     * @param radius the radius within which to search for agents.
     * @return a set of agents within the specified radius of the position.
     */
    Set<Agent> getAgentsByPosAndRadius(Pos pos, Integer radius);

    /**
     * Retrieves the positions within a specified radius of a given position.
     *
     * @param pos    the center position.
     * @param radius the radius within which to search for positions.
     * @return a set of positions within the specified radius of the position.
     */
    Set<Pos> getPosByPosAndRadius(Pos pos, Integer radius);

    /**
     * Retrieves the agents at the specified positions, if any.
     *
     * @param positions the set position to check for an agent.
     * @return a set of agents at the specified positions.
     */
    Set<Agent> getAgentsFromASetOfPos(Set<Pos> positions);

    /**
     * Retrieves the entities at the specified positions, if any.
     *
     * @param entities the set position to check for an itierities.
     * @return a set of entities at the specified positions.
     */
    Set<Entity> getEntitiesFromASetOfPos(Set<Pos> entities);

    /**
     * Copy the state.
     * 
     * @return a safe modifiable copy of the state.
     */
    State copy();

    /**
     * Check if a position is free.
     * 
     * @param pos the position to check.
     * @return true if the position is free, false otherwise.
     */
    boolean isFree(Pos pos);

    /**
     * Check if a position is inside the state.
     * 
     * @param pos the position to check.
     * @return true if the position is inside the state, false otherwise.
     */
    boolean isInside(Pos pos);
}
