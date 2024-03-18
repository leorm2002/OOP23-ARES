package it.unibo.ares.cli;

import it.unibo.ares.core.controller.CalculatorSupplier;

/**
 * Permette di lanciare l'app in modalità cli.
 */
public final class App {
    /**
     * Avvia cli.
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
            Integer step = getStep();
            System.out.println("Premi invio per iniziare la simulazione");
            System.console().readLine();
            simController.startSimulation(step);
            System.out.println("Simulazione terminata");
        });

        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    private static final Integer MINSTEP = 0;
    private static final Integer STEPSIZE = 50;

    private static int getStep() {

        System.out.println("Inserire lo step (ms) tra un tick e l'altro (valore minimo " + MINSTEP + " ms)");
        System.out.println("Lo step verrà arrotondato al multiplo di " + STEPSIZE
                + " ms più vicino, se inserito 0 allora il sistema cercherà di fare il tick il più velocemente possibile.");
        String step = System.console().readLine();
        try {
            int stepInt = Integer.parseInt(step);
            if (stepInt < MINSTEP) {
                System.out.println("Lo step deve essere maggiore di " + MINSTEP);
                return Math.round(stepInt / (float) STEPSIZE) * STEPSIZE;
            }
            return stepInt;
        } catch (NumberFormatException e) {
            System.out.println("Inserire un valore valido");
            return getStep();
        }
    }

    /**
     * Avvia cli, utilizzato quando cli è lanciata come libreria.
     * 
     * @param args
     */
    public static void mainLib(final String[] args) {
        CalculatorSupplier.getInstance(); // Faccio in modo che non sia sul thread della cli
        System.out.println("Benvenuto in ARES!");
        CliInitializer cliController = new CliInitializer();
        String inizializationId = cliController.startParametrization();
        SimController simController = new SimController(inizializationId);
        Integer step = getStep();
        System.out.println("Premi invio per iniziare la simulazione");
        System.console().readLine();
        simController.startSimulation(step);
        System.out.println("Simulazione terminata");
    }

    private App() {
        throw new IllegalAccessError("This is an utility class");
    }
}
