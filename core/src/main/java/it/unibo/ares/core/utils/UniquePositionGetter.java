package it.unibo.ares.core.utils;

import it.unibo.ares.core.utils.pos.Pos;


import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Random;
import java.util.Set;
import java.util.stream.Stream;

/**
 * This class provides a method to get a unique position from a list of
 * positions.
 */
public class UniquePositionGetter implements Iterator<Pos> {
    private final List<Pos> positions;
    private final Random r;
    private final Set<Integer> extracted;

    /**
     * Creates a new UniquePositionGetter.
     *
     * @param positions the list of positions to get the index.
     */
    public UniquePositionGetter(final List<Pos> positions) {
        r = new Random();
        this.positions = Collections.unmodifiableList(positions);
        this.extracted = new HashSet<>();
    }

    /**
     * Returns true if there are still unique positions to extract, otherwise false.
     *
     * @return true if there are still unique positions to extract, otherwise false.
     */
    @Override
    public boolean hasNext() {
        return extracted.size() < positions.size();
    }

    /**
     * Returns the next unique position if present, otherwise throws a
     * NoSuchElementException.
     * 
     * @throws NoSuchElementException if all positions have been extracted.
     * @return the next unique position.
     */
    @Override
    public Pos next() {
        if (extracted.size() == positions.size()) {
            throw new NoSuchElementException("All positions have been extracted");
        }
        int position = Stream.generate(() -> r.nextInt(positions.size()))
                .filter(i -> !extracted.contains(i))
                .limit(1)
                .findAny()
                .orElseThrow(() -> new NoSuchElementException("All positions have been extracted"));
        extracted.add(position);
        return positions.get(position);
    }
}
