package it.unibo.ares.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

public class BoardImpl<V> implements Board<V> {
    Map<Pos, V> entities = new HashMap<>();
    @Override
    public Set<Pair<Pos, V>> getEntities() {
        return entities.entrySet().stream()
            .map(e -> new Pair<Pos, V>(e.getKey(), e.getValue()))
            .collect(Collectors.toSet());
    }

    @Override
    public void addEntity(Pos pos, V entity) {
        entities.put(pos, entity);
    }

    @Override
    public void removeEntity(Pos pos, V entity) {
        entities.remove(pos, entity);
    }

    @Override
    public V getEntity(Pos pos) {
        return entities.get(pos);
    }
    
}
