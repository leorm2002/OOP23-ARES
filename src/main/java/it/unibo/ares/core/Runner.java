package it.unibo.ares.core;

import java.util.Random;

import it.unibo.ares.core.model.BoidsModelFactory;
import it.unibo.ares.core.model.Model;
import it.unibo.ares.core.model.ModelFactory;
import it.unibo.ares.core.model.SchellingModelFactories;
import it.unibo.ares.core.utils.Pair;
import it.unibo.ares.core.utils.directionvector.DirectionVectorImpl;
import it.unibo.ares.core.utils.state.State;

class Runner {
    private static Random r;

    private static void runSchelling() {
        SchellingModelFactories f = new SchellingModelFactories();

        Model schelling = f.getModel();

        schelling.setParameter("size", 30);
        schelling.setParameter("numeroAgentiTipoA", 200);
        schelling.setParameter("numeroAgentiTipoB", 200);

        State state = schelling.initilize();
        state.getAgents().stream().forEach(a -> {
            a.getSecond().setParameter("visionRadius", 5);
            a.getSecond().setParameter("threshold", 0.9);
        });
        System.out.println("Initial state");
        state.printState();
        int i = 1;
        State newState = schelling.tick(state);
        System.out.println("");
        System.out.println("After tick" + i++);
        System.out.println("");

        state.printState();

        while (schelling.isOver(state, newState)) {
            state = newState;
            newState = schelling.tick(state);
            System.out.println("");
            System.out.println("After tick" + i++);
            System.out.println("");
            newState.printState();
        }

        System.out.println("Simulation ended");

    }

    private static DirectionVectorImpl getRandomDir() {
        return new DirectionVectorImpl(r.nextInt(50), r.nextInt(50));
    }

    private static void runBoids() throws InterruptedException {
        r = new Random();
        ModelFactory f = new BoidsModelFactory();

        Model boids = f.getModel();

        boids.setParameter("size", 30);
        boids.setParameter("numeroUccelli", 100);

        State state = boids.initilize();
        state.getAgents().stream().map(Pair::getSecond).forEach(a -> {
            a.setParameter("distance", 5);
            a.setParameter("angle", 90);
            a.setParameter("direction", getRandomDir());
            a.setParameter("collisionAvoidanceWeight", 1.0);
            a.setParameter("alignmentWeight", 3.0);
            a.setParameter("cohesionWeight", 3.0);
            a.setParameter("stepSize", 3);

        });

        System.out.println("Initial state");
        state.printState();
        int i = 1;
        State newState = boids.tick(state);
        System.out.println("");
        System.out.println("After tick" + i++);
        System.out.println("");

        state.printState();

        while (!boids.isOver(state, newState)) {
            state = newState;
            newState = boids.tick(state);
            System.out.println("");
            System.out.println("After tick" + i++);
            System.out.println("");
            newState.printState();
            Thread.sleep(100);
        }

        System.out.println("Simulation ended");

    }

    public static void main(final String[] args) throws InterruptedException {
        runBoids();
    }
}
