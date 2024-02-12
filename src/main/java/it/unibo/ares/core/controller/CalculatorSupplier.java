package it.unibo.ares.core.controller;

public final class CalculatorSupplier {
    private static volatile CalculatorSupplier instance;

    private SimulationsController controller;
    private SimulationInitializer initializer;

    /**
     * Starts the simulation with the given initialization id.
     *
     * @param initializationId The id of the initialization to start.
     * @return The id of the simulation.
     */
    public String startSimulation(final String initializationId) {
        var resp = initializer.startSimulation(initializationId);
        // TODO creare simulation
        controller.addSimulation(initializationId, null);
        return initializationId;
    }

    /**
     * Returns the singleton instance of the calculator supplier.
     * 
     * @return
     */
    public static CalculatorSupplier getInstance() {
        CalculatorSupplier curr = instance;

        if (curr != null) {
            return curr;
        }
        synchronized (CalculatorSupplier.class) {
            if (instance == null) {
                instance = new CalculatorSupplier(
                        new SimulationsControllerImpl(), new SimulationInitializerImpl());
            }
            return instance;
        }
    }

    public SimulationsController getController() {
        return controller;
    }

    public void setController(SimulationsController controller) {
        this.controller = controller;
    }

    public SimulationInitializer getInitializer() {
        return initializer;
    }

    public void setInitializer(SimulationInitializer initializer) {
        this.initializer = initializer;
    }

    private CalculatorSupplier(final SimulationsController c, final SimulationInitializer i) {
        this.controller = c;
        this.initializer = i;
    }
}
