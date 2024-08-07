package it.unibo.ares.cli;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.UUID;

import it.unibo.ares.core.controller.AresSupplier;

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
     * @param args args passati da riga di comando
     */
    public static void main(final String[] args) {
        AresSupplier.getInstance(); // Faccio in modo che non sia sul thread della cli
        final IOManager ioManager = new IOManagerImpl();
        final Thread t = new Thread(() -> {
            mainLib(args);
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

    private static String getPath(final IOManager ioManager) {
        ioManager.print("Inserisci il path del file da caricare");
        final String path = ioManager.read();
        try {
            if (!Files.exists(Paths.get(path))) {
                ioManager.print("Il percorso specificato non esiste. Inserisci un percorso valido.");
                return getPath(ioManager);
            }
            return path;
        } catch (NumberFormatException e) {
            ioManager.print("Inserisci un valore valido");
            return getPath(ioManager);
        }
    }

    private static void loadFromFileAndStart(final IOManager ioManager) {
        final String path = getPath(ioManager);
        final String inizializationId = UUID.randomUUID().toString();
        final Integer step = getStep(ioManager);
        final SimController simController = new SimController(inizializationId, ioManager);
        simController.startSimulationFromFile(path, step);
    }

    private static void normalRun(final IOManager ioManager) {
        final CliInitializer cliController = new CliInitializer(ioManager);
        final String inizializationId = cliController.startParametrization();
        ioManager.print(
                "Vuoi esportare i parametri inseriti in un file? Premi s per esportare, "
                        + "qualsiasi altro tasto per continuare senza esportare.");

        final String choice = ioManager.read();
        if ("s".equals(choice)) {
            cliController.exportParametersData();
        }
        final SimController simController = new SimController(inizializationId, ioManager);
        final Integer step = getStep(ioManager);
        ioManager.print("Premi invio per iniziare la simulazione");
        ioManager.read();
        simController.startSimulation(step);
        ioManager.print("Simulazione terminata");

    }

    /**
     * Avvia cli, utilizzato quando cli è lanciata come libreria.
     * 
     * @param args args passati da riga di comando
     */
    public static void mainLib(final String[] args) {
        final IOManager ioManager = new IOManagerImpl();
        ioManager.print("Benvenuto in ARES!");
        ioManager.print(
                "Vuoi fare una nuova simulazione o caricarne una? Premi n per iniziarne una nuova, c per caricarne una da file");
        final String choice = ioManager.read();
        if ("c".equals(choice)) {
            loadFromFileAndStart(ioManager);
        } else {
            normalRun(ioManager);
        }
    }
}
