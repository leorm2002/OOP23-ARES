package it.unibo.ares.utils;

import java.util.Set;

import it.unibo.ares.agent.Agent;
import it.unibo.ares.agent.Entity;

public interface State {
    Set<Pair<Pos, Entity>> getEntities();
    Set<Pair<Pos, Agent>> getAgents();

    void addAgent(Pos pos, Agent agent);
    void removeAgent(Pos pos, Agent agent);
    void moveAgent(Pos from, Pos to);
    Agent getAgentAt(Pos pos);
    void addEntity(Pos pos, Entity entity);
    void removeEntity(Pos pos, Entity entity);
    void moveEntity(Pos from, Pos to);
}
