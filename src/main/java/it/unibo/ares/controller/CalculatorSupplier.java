package it.unibo.ares.controller;

public class CalculatorSupplier {
    private static volatile CalculatorSupplier instance;

    public SimulationController controller;
    public SimulationInitializer initializer;

    private CalculatorSupplier(SimulationController c, SimulationInitializer i){
        this.controller = c;
        this.initializer = i;
    }

    public String startSimulation(String initializationId){
        var resp = initializer.startSimulation(initializationId);
        //TODO creare simulation
        controller.addSimulation(initializationId, null);
        return initializationId;
    }


    public static CalculatorSupplier getInstance() {
        CalculatorSupplier curr = instance;

        if(curr != null){
            return curr;
        }
        synchronized(CalculatorSupplier.class){
            if(instance == null){
                instance = new CalculatorSupplier(
                    new SimulationController(), new SimulationInitializerImpl());
                }
            return instance;
        }
    }

}
