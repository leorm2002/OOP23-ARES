package it.unibo.ares.runner;

import java.util.ArrayList;
import java.util.List;

import it.unibo.ares.core.controller.AresSupplier;

/**
 * Permette di lanciare l'applicativo in vaie modalita
 */
public class ApplicationRunner {
    private static final String TERMINAL = "0";
    private static final String GRAFIC = "1";
    private static final String COMBINED = "2";
    private static final String EXIT = "3";
    private static final List<String> modes = List.of(TERMINAL, GRAFIC, COMBINED, EXIT);

    private static void printTitle() {
        System.out.println("   _____ _____________________ _________");
        System.out.println("  /  _  \\______   \\_   _____//   _____/");
        System.out.println(" /  /_\\  \\|       _/|    __)_ \\_____  \\ ");
        System.out.println("/    |    \\    |   \\|        \\/        \\");
        System.out.println("\\____|__  /____|_  /_______  /_______  /");
        System.out.println("        \\/       \\/        \\/        \\/");
    }

    /**
     * avvia l'applicazione
     *
     * @param args
     */
    public static void main(String[] args) {
        List<Thread> threads = new ArrayList<>();
        printTitle();
        System.out.println("Premi 0 per avviare l'applicazione in modalita' cli");
        System.out.println("Premi 1 per avviare l'applicazione in modalita' grafica");
        System.out.println("Premi 2 per avviare l'applicazione in modalita' entrambe");
        System.out.println("Premi 3 per uscire");

        threads.add(
                new Thread(AresSupplier::getInstance));
        String mode;
        do {
            mode = System.console().readLine();
        } while (!modes.contains(mode));
        switch (mode) {
            case TERMINAL:
                System.out.println("Avvio in modalita' cli");
                threads.add(
                        new Thread(() -> it.unibo.ares.cli.App.mainLib(args)));
                break;
            case GRAFIC:
                System.out.println("Avvio in modalita' grafica");
                threads.add(
                        it.unibo.ares.gui.App.mainLib(args));
                break;
            case COMBINED:
                System.out.println("Avvio di entrambe");
                threads.addAll(List.of(
                        new Thread(() -> it.unibo.ares.cli.App.mainLib(args)),
                        it.unibo.ares.gui.App.mainLib(args)));
                break;
            case EXIT:
                System.out.println("Arrivederci");
                System.exit(0);
                break;
            default:
                break;
        }

        threads.forEach(Thread::start);
        threads.forEach(t -> {
            try {
                t.join();
            } catch (InterruptedException e) {
                System.out.println("Errore nell'esecuzione");
            }
        });

        System.out.println("Arrivederci");
        System.exit(0);
    }

    private ApplicationRunner() {
        throw new IllegalAccessError("This is an utility class");
    }
}
