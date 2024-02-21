package it.unibo.ares;

import java.util.ArrayList;
import java.util.List;

import it.unibo.ares.core.controller.CalculatorSupplier;

/**
 * Permette di lanciare l'applicativo in vaie modalita
 */
public class ApplicationRunner {
    private static final String TERMINAL = "0";
    private static final String GRAFIC = "1";
    private static final String COMBINED = "2";
    private static final String EXIT = "3";

    /**
     * avvia l'applicazione
     * 
     * @param args
     */
    public static void main(String[] args) {
        List<String> modes = List.of(TERMINAL, GRAFIC, COMBINED);
        List<Thread> threads = new ArrayList<>();
        CalculatorSupplier.getInstance();
        System.out.println("Benvenuto in ARES!");
        System.out.println("Premi 0 per avviare l'applicazione in modalità cli");
        System.out.println("Premi 1 per avviare l'applicazione in modalità grafica");
        System.out.println("Premi 2 per avviare l'applicazione in modalità entrambe");
        System.out.println("Premi 3 per uscire");
        String mode;
        do {
            mode = System.console().readLine();
        } while (!modes.contains(mode));
        switch (mode) {
            case TERMINAL:
                System.out.println("Avvio in modalità cli");
                threads.add(
                        new Thread(() -> it.unibo.ares.cli.App.main(args)));
                break;
            case GRAFIC:
                System.out.println("Avvio in modalità grafica");
                threads.add(
                        new Thread(() -> it.unibo.ares.gui.App.main(args)));
                break;
            case COMBINED:
                System.out.println("Avvio in modalità entrambe");
                threads.addAll(List.of(
                        new Thread(() -> it.unibo.ares.cli.App.main(args)),
                        new Thread(() -> it.unibo.ares.gui.App.main(args))));
                break;
            case EXIT:
                return;
            default:
                break;
        }

        threads.forEach(Thread::start);
        threads.forEach(t -> {
            try {
                t.wait();
            } catch (InterruptedException e) {
                System.out.println("Errore nell'esecuzione");
            }
        });
    }

    private ApplicationRunner() {
        throw new IllegalAccessError("This is an utility class");
    }
}
