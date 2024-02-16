package it.unibo.ares.core.agent;

import static org.junit.jupiter.api.Assertions.*;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import it.unibo.ares.core.agent.BoidsAgentFactory;
import it.unibo.ares.core.utils.directionvector.DirectionVector;
import it.unibo.ares.core.utils.directionvector.DirectionVectorImpl;
import it.unibo.ares.core.utils.pos.Pos;
import it.unibo.ares.core.utils.pos.PosImpl;

public class TestBoids {
        @Test
        public void testInsideCone() throws NoSuchMethodException, SecurityException, ClassNotFoundException,
                        IllegalAccessException, IllegalArgumentException, InvocationTargetException {
                BoidsAgentFactory b = new BoidsAgentFactory();
                Pos pos = new PosImpl(0, 0);
                DirectionVector dir = new DirectionVectorImpl(1.0, 0.0);
                Integer radius = 3;
                Integer angle = 90;
                Set<PosImpl> inside = Stream.concat(
                                Stream.concat(
                                                IntStream.rangeClosed(-3, 3).mapToObj(y -> new PosImpl(0, y)),
                                                IntStream.rangeClosed(-2, 2).boxed()
                                                                .flatMap(y -> IntStream.rangeClosed(1, 2)
                                                                                .mapToObj(x -> new PosImpl(x, y)))),
                                Stream.of(new PosImpl(3, 0)))
                                .filter(p -> !(p.getX() == 0 && p.getY() == 0))
                                .collect(Collectors.toSet());

                Set<PosImpl> outside = IntStream.range(-10, 10).boxed()
                                .flatMap(x -> IntStream.range(-10, 10).mapToObj(y -> new PosImpl(x, y)))
                                .filter(p -> !inside.contains(p))
                                .collect(Collectors.toSet());
                Method method = Class.forName("it.unibo.ares.core.agent.BoidsAgentFactory")
                                .getDeclaredMethod("insideCone", Pos.class, Pos.class, DirectionVector.class,
                                                Integer.class,
                                                Integer.class);

                method.setAccessible(true);
                for (Pos p : inside) {
                        assertTrue((boolean) method.invoke(b, p, pos, dir, radius, angle));
                }
                for (Pos p : outside) {
                        assertFalse((boolean) method.invoke(b, p, pos, dir, radius, angle));
                }
        }
}
