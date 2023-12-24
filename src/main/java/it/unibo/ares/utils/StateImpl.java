package it.unibo.ares.utils;

import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import it.unibo.ares.agent.Agent;
import it.unibo.ares.agent.Entity;

public class StateImpl implements State {
    Board<Entity> entityBoard = new BoardImpl<>();
    Board<Agent> agentBoard = new BoardImpl<>();
    private final Pair<Integer, Integer> size;
    public StateImpl(Integer width, Integer height) {
        this.size = new Pair<Integer, Integer>(width, height);
    }

    private void assertInsideBoard(Pos pos) {
        if(!( pos.getX() >= 0 && pos.getX() < size.getFirst()
            || pos.getY() >= 0 && pos.getY() < size.getSecond())) {
            throw new IllegalArgumentException("Position " + pos + " is outside the board");
        }
    }

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
        assertInsideBoard(pos);
        agentBoard.addEntity(pos, agent);
    }

    @Override
    public void removeAgent(Pos pos, Agent agent) {
        agentBoard.removeEntity(pos, agent);
    }

    @Override
    public void moveAgent(Pos from, Pos to) {
        assertInsideBoard(to);
        Agent agent = agentBoard.getEntity(from).get();
        agentBoard.removeEntity(from, agent);
        agentBoard.addEntity(to, agent);
    }

    @Override
    public void addEntity(Pos pos, Entity entity) {
        assertInsideBoard(pos);
        entityBoard.addEntity(pos, entity);
    }

    @Override
    public void removeEntity(Pos pos, Entity entity) {
        entityBoard.removeEntity(pos, entity);
    }

    @Override
    public void moveEntity(Pos from, Pos to) {
        assertInsideBoard(to);

        Entity entity = entityBoard.getEntity(from).get();
        entityBoard.removeEntity(from, entity);
        entityBoard.addEntity(to, entity);
    }

    @Override
    public Optional<Agent> getAgentAt(Pos pos) {
        return agentBoard.getEntity(pos);
    }
    @Override
    public Pair<Integer, Integer> getDimensions() {
        return this.size;
    }

    private Boolean isValidPosition(Pos pos) {
        return  pos.getX() >= 0 && pos.getX() < size.getFirst()
                && pos.getY() >= 0 && pos.getY() < size.getSecond();
    }

    public Set<Pos> getPosByPosAndRadius(Pos pos, Integer radius){
        return
            IntStream.rangeClosed(pos.getX()-radius, pos.getX()+radius)
            .boxed()
            .flatMap(x ->
                IntStream.rangeClosed(pos.getY()-radius, pos.getY()+radius).mapToObj(y -> new PosImpl(x, y)))
            .filter(this::isValidPosition)
            .filter(p -> !p.equals(pos))
            .collect(Collectors.toSet());
    }

    @Override
    public Set<Agent> getAgentsByPosAndRadius(Pos pos, Integer radius) {
        var positions = getPosByPosAndRadius(pos, radius);
        var a = this.getAgentAt(new PosImpl(1, 0));
        var p =  positions.stream()
            .map(this::getAgentAt)
            .filter(Optional::isPresent)
            .map(Optional::get)
            .collect(Collectors.toSet());
        return p;
    }

    public void debugPring(){
        var out = IntStream.range(0, size.getFirst())
            .boxed()
            .map(x -> IntStream.range(0, size.getSecond())
                .mapToObj(y -> new PosImpl(x, y))
                .map(pos -> {
                    var a = getAgentAt(pos);
                    if(a.isPresent()){
                        return a.get().getParameters().getParameter("type", Integer.class).get().getValue().toString();
                    }else {
                        return " ";
                    }
                }).collect(Collectors.joining(" "))
            ).collect(Collectors.joining("\n"));
        
        System.out.println(out);
    }
    
}
