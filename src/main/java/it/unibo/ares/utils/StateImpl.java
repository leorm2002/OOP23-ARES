package it.unibo.ares.utils;

import java.util.Set;

import it.unibo.ares.agent.Agent;
import it.unibo.ares.agent.Entity;

public class StateImpl implements State {
    Board<Entity> entityBoard = new BoardImpl<>();
    Board<Agent> agentBoard = new BoardImpl<>();
    
    @Override
    public Set<Pair<Pos, Entity>> getEntities() {
        return entityBoard.getEntities();
    }

    @Override
    public Set<Pair<Pos, Agent>> getAgents() {
        return agentBoard.getEntities();
    }

    @Override
    public void addAgent(Pos pos, Agent agent) {
        agentBoard.addEntity(pos, agent);
    }

    @Override
    public void removeAgent(Pos pos, Agent agent) {
        agentBoard.removeEntity(pos, agent);
    }

    @Override
    public void moveAgent(Pos from, Pos to) {
        Agent agent = agentBoard.getEntity(from);
        agentBoard.removeEntity(from, agent);
        agentBoard.addEntity(to, agent);
    }

    @Override
    public void addEntity(Pos pos, Entity entity) {
        entityBoard.addEntity(pos, entity);
    }

    @Override
    public void removeEntity(Pos pos, Entity entity) {
        entityBoard.removeEntity(pos, entity);
    }

    @Override
    public void moveEntity(Pos from, Pos to) {
        Entity entity = entityBoard.getEntity(from);
        entityBoard.removeEntity(from, entity);
        entityBoard.addEntity(to, entity);
    }

    @Override
    public Agent getAgentAt(Pos pos) {
        return agentBoard.getEntity(pos);
    }
    
}
