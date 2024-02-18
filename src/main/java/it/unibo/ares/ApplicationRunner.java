package it.unibo.ares;

import it.unibo.ares.core.controller.CalculatorSupplier;

public class ApplicationRunner {
    public static void main(String[] args) {
        CalculatorSupplier.getInstance();
        System.out.println("Benvenuto in ARES!");
        System.out.println("Premi 0 per avviare l'applicazione in modalità cli, 1 per grafica 2 per entrambe");
        System.out.println("Premi 1 per avviare l'applicazione in modalità grafica");
        System.out.println("Premi 2 per avviare l'applicazione in modalità entrambe");
        System.out.println("Premi 0 per avviare l'applicazione in modalità cli");
        String mode = System.console().readLine();
        if (mode.equals("0")) {
            System.out.println("Avvio in modalità cli");
            Thread thread = new Thread(() -> {
                it.unibo.ares.cli.App.main(args);
            });
            thread.start();
        } else if (mode.equals("1")) {
            System.out.println("Avvio in modalità grafica");
            Thread thread = new Thread(() -> {
                it.unibo.ares.gui.App.main(args);
            });
            thread.start();
        } else if (mode.equals("2")) {
            System.out.println("Avvio in modalità entrambe");
            Thread thread = new Thread(() -> {
                it.unibo.ares.cli.App.main(args);
            });
            Thread thread2 = new Thread(() -> {
                it.unibo.ares.gui.App.main(args);
            });
            thread.start();
            thread2.start();
        } else {
            System.out.println("Modalità non valida");
        }

    }
}
