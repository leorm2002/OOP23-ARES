package it.unibo.ares.cli;

import it.unibo.ares.core.controller.CalculatorSupplier;

/**
 * Permette di lanciare l'app in modalità cli.
 */
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
        final IOManager ioManager = new IOManagerImpl();
        final Thread t = new Thread(() -> {
            ioManager.print("Benvenuto in ARES!");
            final CliInitializer cliController = new CliInitializer(ioManager);
            final String inizializationId = cliController.startParametrization();
            final SimController simController = new SimController(inizializationId, ioManager);
            final Integer step = getStep(ioManager);
            ioManager.print("Premi invio per iniziare la simulazione");
            ioManager.read();
            simController.startSimulation(step);
            ioManager.print("Simulazione terminata");
            // Close the application
            System.exit(0);
        });

        t.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            ioManager.print("Errore a runtime");
        }

    }

    private static int getStep(final IOManager ioManager) {

        ioManager.print("Inserire lo step (ms) tra un tick e l'altro (valore minimo " + MINSTEP + " ms)");

        ioManager.print("Lo step verra' arrotondato al multiplo di " + STEPSIZE
                + " ms più vicino, se inserito 0 allora il sistema cercherà di fare il tick il piu' velocemente possibile.");

        final String step = ioManager.read();
        try {
            final int stepInt = Integer.parseInt(step);
            if (stepInt < MINSTEP) {
                ioManager.print("Inserire un valore maggiore o uguale a " + MINSTEP);

                return Math.round(stepInt / (float) STEPSIZE) * STEPSIZE;
            }
            return stepInt;
        } catch (NumberFormatException e) {
            ioManager.print("Inserire un valore valido");
            return getStep(ioManager);
        }
    }

    /**
     * Avvia cli, utilizzato quando cli è lanciata come libreria.
     * 
     * @param args
     */
    public static void mainLib(final String[] args) {
        final IOManager ioManager = new IOManagerImpl();
        ioManager.print("Benvenuto in ARES!");
        final CliInitializer cliController = new CliInitializer(ioManager);
        final String inizializationId = cliController.startParametrization();
        final SimController simController = new SimController(inizializationId, ioManager);
        final Integer step = getStep(ioManager);
        ioManager.print("Premi invio per iniziare la simulazione");
        ioManager.read();
        simController.startSimulation(step);
        ioManager.print("Simulazione terminata");
    }
}
