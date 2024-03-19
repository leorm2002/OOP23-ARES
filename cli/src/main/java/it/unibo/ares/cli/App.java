package it.unibo.ares.cli;

import it.unibo.ares.core.controller.CalculatorSupplier;

/**
 * Permette di lanciare l'app in modalità cli.
 */
@SuppressWarnings("PMD.SystemPrintln") // E UN PROGRAMMA CLI
public final class App {

    private static final Integer MINSTEP = 0;
    private static final Integer STEPSIZE = 50;

    private App() {
        throw new IllegalAccessError("This is an utility class");
    }

    /**
     * Avvia cli.
     * 
     * @param args
     */
    public static void main(final String[] args) {
        CalculatorSupplier.getInstance(); // Faccio in modo che non sia sul thread della cli
        final Thread t = new Thread(() -> {
            System.out.println("Benvenuto in ARES!");
            final CliInitializer cliController = new CliInitializer();
            final String inizializationId = cliController.startParametrization();
            final SimController simController = new SimController(inizializationId);
            final Integer step = getStep();
            System.out.println("Premi invio per iniziare la simulazione");
            System.console().readLine();
            simController.startSimulation(step);
            System.out.println("Simulazione terminata");
        });

        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            System.out.println("Errore a runtime");
        }

    }

    private static int getStep() {

        System.out.println("Inserire lo step (ms) tra un tick e l'altro (valore minimo " + MINSTEP + " ms)");
        System.out.println("Lo step verrà arrotondato al multiplo di " + STEPSIZE
                + " ms più vicino, se inserito 0 allora il sistema cercherà di fare il tick il più velocemente possibile.");
        final String step = System.console().readLine();
        try {
            final int stepInt = Integer.parseInt(step);
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
        final CliInitializer cliController = new CliInitializer();
        final String inizializationId = cliController.startParametrization();
        final SimController simController = new SimController(inizializationId);
        final Integer step = getStep();
        System.out.println("Premi invio per iniziare la simulazione");
        System.console().readLine();
        simController.startSimulation(step);
        System.out.println("Simulazione terminata");
    }
}
