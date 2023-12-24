package it.unibo.ares.agent;

import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import it.unibo.ares.agent.factories.BoidsAgentFactory;
import it.unibo.ares.utils.directionvector.DirectionVector;
import it.unibo.ares.utils.directionvector.DirectionVectorImpl;
import it.unibo.ares.utils.pos.Pos;
import it.unibo.ares.utils.pos.PosImpl;

public class TestBoids {
    @Test
    public void testInsideCone() {
        BoidsAgentFactory b = new BoidsAgentFactory();
        Pos pos = new PosImpl(0, 0);
        DirectionVector dir = new DirectionVectorImpl(1.0, 0.0);
        Integer radius = 3;
        Integer angle = 90;
        Set<PosImpl> inside =
            Stream.concat(
                Stream.concat(
                    IntStream.rangeClosed(-3, 3).mapToObj(y -> new PosImpl(0, y)),
                    IntStream.rangeClosed(-2, 2).boxed().flatMap(y -> IntStream.rangeClosed(1, 2).mapToObj(x -> new PosImpl(x, y)))
                ),
                Stream.of(new PosImpl(3, 0)))
            .filter(p -> !(p.getX() == 0 && p.getY() == 0))
            .collect(Collectors.toSet());
            
        Set<PosImpl> outside = IntStream.range(-10, 10).boxed()
                .flatMap(x -> IntStream.range(-10, 10).mapToObj(y -> new PosImpl(x, y)))
                .filter(p -> !inside.contains(p))
                .collect(Collectors.toSet());

        for(Pos p : inside) {
            assertTrue(b.insideCone(p, pos, dir, radius, angle));
        }
        for(Pos p : outside) {
            assertFalse(b.insideCone(p, pos, dir, radius, angle));
        }
    }
}
