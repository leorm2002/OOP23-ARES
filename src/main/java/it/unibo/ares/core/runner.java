package it.unibo.ares.core;

import it.unibo.ares.core.model.Model;
import it.unibo.ares.core.model.SchellingModelFactories;
import it.unibo.ares.core.utils.state.State;

public class runner {
    public static void main(String[] args) throws InterruptedException {
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
        state.debugPrint();
        int i = 1;
        State newState = schelling.tick(state);
        System.out.println("");
        System.out.println("After tick" + i++);
        System.out.println("");

        state.debugPrint();

        while (schelling.isOver(state, newState)) {
            state = newState;
            newState = schelling.tick(state);
            System.out.println("");
            System.out.println("After tick" + i++);
            System.out.println("");
            newState.debugPrint();
        }

        System.out.println("Simulation ended");

    }
}
