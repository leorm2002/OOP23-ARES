package it.unibo.ares.cli;

import it.unibo.ares.core.controller.CalculatorSupplier;

/**
 * Permette di lanciare l'app in modalità cli
 */
public class App {
    /**
     * Avvia cli
     * 
     * @param args
     */
    public static void main(final String[] args) {
        CalculatorSupplier.getInstance(); // Faccio in modo che non sia sul thread della cli
        Thread t = new Thread(() -> {
            System.out.println("Benvenuto in ARES!");
            CliInitializer cliController = new CliInitializer();
            String inizializationId = cliController.startParametrization();
            SimController simController = new SimController(inizializationId);
            System.out.println("Premi invio per iniziare la simulazione");
            System.console().readLine();
            simController.startSimulation();
            System.out.println("Simulazione terminata");
        });

        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public static void mainLib(final String[] args) {
        CalculatorSupplier.getInstance(); // Faccio in modo che non sia sul thread della cli
        System.out.println("Benvenuto in ARES!");
        CliInitializer cliController = new CliInitializer();
        String inizializationId = cliController.startParametrization();
        SimController simController = new SimController(inizializationId);
        System.out.println("Premi invio per iniziare la simulazione");
        System.console().readLine();
        simController.startSimulation();
        System.out.println("Simulazione terminata");
    }

    private App() {
        throw new IllegalAccessError("This is an utility class");
    }
}
