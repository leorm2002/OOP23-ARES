package it.unibo.ares.core.utils;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.lang.reflect.InvocationTargetException;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;

import it.unibo.ares.core.utils.directionvector.DirectionVector;
import it.unibo.ares.core.utils.directionvector.DirectionVectorImpl;
import it.unibo.ares.core.utils.pos.Pos;
import it.unibo.ares.core.utils.pos.PosImpl;

class ComputationUtilsTest {
    @Test
    void testInsideCone() throws NoSuchMethodException, SecurityException, ClassNotFoundException,
            IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        final Pos pos = new PosImpl(0, 0);
        final DirectionVector dir = new DirectionVectorImpl(1.0, 0.0);
        final Integer radius = 3;
        final Integer angle = 90;
        // CHECKSTYLE: MagicNumber OFF sono numeri messi in modo da poter coprire un
        // raggio sufficiente intorno al punto da testare

        final Set<PosImpl> inside = Stream.concat(
                Stream.concat(
                        IntStream.rangeClosed(-3, 3).mapToObj(y -> new PosImpl(0, y)),
                        IntStream.rangeClosed(-2, 2).boxed()
                                .flatMap(y -> IntStream.rangeClosed(1, 2)
                                        .mapToObj(x -> new PosImpl(x, y)))),
                Stream.of(new PosImpl(3, 0)))
                .filter(p -> !(p.getX() == 0 && p.getY() == 0))
                .collect(Collectors.toSet());

        final Set<PosImpl> outside = IntStream.range(-10, 10).boxed()
                .flatMap(x -> IntStream.range(-10, 10).mapToObj(y -> new PosImpl(x, y)))
                .filter(p -> !inside.contains(p))
                .filter(p -> !p.equals(pos))
                .collect(Collectors.toSet());
        // CHECKSTYLE: MagicNumber ON

        for (final Pos p : inside) {
            assertTrue(ComputationUtils.insideCone(p, pos, dir, radius, angle));
        }
        for (final Pos p : outside) {
            assertFalse(ComputationUtils.insideCone(p, pos, dir, radius, angle));
        }
    }

    /*
     * This test checks if the move method works as expected.
     * It creates a new agent and then it tries to move it.
     * The agent should move to the new position.
     */
    @Test
    void testMove() throws NoSuchMethodException, SecurityException,
            ClassNotFoundException, IllegalAccessException, IllegalArgumentException,
            InvocationTargetException {
        final Pos initialPos = new PosImpl(0, 0);
        final DirectionVector dir = new DirectionVectorImpl(1, 1);
        final Integer stepSize = 1;

        // The new position should be (1, 1) because the direction vector is (1, 1) and
        // the step size is 1,
        // starting from (0, 0)
        final Pos newPos = ComputationUtils.move(initialPos, dir, stepSize);
        assertEquals(new PosImpl(1, 1), newPos);
    }

    /*
     * This test checks if the limit method works as expected.
     * If the new position is out of the grid, the limit method should limit the
     * position to the grid
     * and return the new position (maxPosX - 1, maxPosY - 1).
     */
    @Test
    void testPosLimit() throws NoSuchMethodException, SecurityException, ClassNotFoundException,
            IllegalAccessException, IllegalArgumentException, InvocationTargetException {
        final int outOfBoundPos = 6, maxPos = 5, espectedPos = 4;

        // The new position should be (4, 4) because the new position is (6, 6)
        // and it's out of bound because the grid is 5x5, (maxPosX - 1, maxPosY - 1)
        final Pos limitedPos = ComputationUtils.limit(new PosImpl(outOfBoundPos, outOfBoundPos),
                new Pair<>(maxPos, maxPos));
        assertEquals(new PosImpl(espectedPos, espectedPos), limitedPos);
    }
}
