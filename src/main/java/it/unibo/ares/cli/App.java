package it.unibo.ares.cli;

public class App {
    public static void main(String[] args) {
        System.out.println("Benvenuto in ARES!");
        CliInitializer cliController = new CliInitializer();
        String inizializationId = cliController.startParametrization();
        SimController simController = new SimController(inizializationId);
        System.out.println("Premi invio per iniziare la simulazione");
        System.console().readLine();
        simController.startSimulation();
        System.out.println("Simulazione terminata");
    }
}
