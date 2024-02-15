package it.unibo.ares.core.utils.uniquepositiongetter;

import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Stream;

import it.unibo.ares.core.utils.pos.Pos;

public class UniquePositionGetter {
    private final List<Pos> positions;
    private final Random r;
    private final Set<Integer> extracted;

    public UniquePositionGetter(final List<Pos> positions) {
        r = new Random();
        this.positions = positions;
        this.extracted = new HashSet<>();
    }

    public Pos get() {
        int position = Stream.generate(() -> r.nextInt(positions.size()))
                .filter(i -> !extracted.contains(i))
                .limit(1)
                .findAny()
                .orElseThrow();
        extracted.add(position);
        return positions.get(position);
    }
}
