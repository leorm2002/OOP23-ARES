package it.unibo.ares.cli;

import java.util.Optional;

import it.unibo.ares.core.api.DataReciever;
import it.unibo.ares.core.api.SimulationOutputDataApi;
import it.unibo.ares.core.controller.CalculatorSupplier;
import it.unibo.ares.core.controller.SimulationOutputData;
import it.unibo.ares.core.utils.pos.Pos;
import it.unibo.ares.core.utils.pos.PosImpl;
import it.unibo.ares.core.utils.statistics.Statistics;

/**
 * The SimController class is responsible for controlling the simulation and
 * displaying the simulation output data.
 */
@SuppressWarnings("PMD.SystemPrintln") // E UN PROGRAMMA CLI
public final class SimController extends DataReciever {
    private static final String START = "s";
    private static final String PAUSE = "p";
    private static final String STOP = "e";
    private final String inizializationId;
    private static final String SEPARATOR = "    ";
    private String simulationId;
    private boolean isOver;

    /**
     * Constructs a SimController object with the specified initialization ID.
     *
     * @param inizializationId the initialization ID for the simulation
     */
    public SimController(final String inizializationId) {
        this.inizializationId = inizializationId;
    }

    private void readChar(final String ch) {
        switch (ch) {
            case START:
                CalculatorSupplier.getInstance().startSimulation(simulationId);
                break;
            case STOP:
                CalculatorSupplier.getInstance().pauseSimulation(simulationId);
                this.isOver = true;
                break;
            case PAUSE:
                CalculatorSupplier.getInstance().pauseSimulation(simulationId);
                break;
            default:
                break;
        }
    }

    private boolean isOver() {
        return this.isOver;
    }

    /**
     * Starts the simulation and waits for user input to control the simulation.
     * 
     * @param stepSize the step in ms of the sim
     */
    public void startSimulation(final Integer stepSize) {
        System.out.println("Inizio simulazione");
        this.simulationId = CalculatorSupplier.getInstance().startSimulation(inizializationId, this);
        CalculatorSupplier.getInstance().setTickRate(inizializationId, stepSize);
        final Thread reader = new Thread(new AsyncReader(this::readChar, this::isOver));
        reader.start();
        try {
            reader.join();
        } catch (InterruptedException e) {
            System.out.println("Errore nell'avvio");
        }
    }

    @Override
    public void onNext(final SimulationOutputData item) {
        printData(item);
    }

    private void printInfo() {
        System.out.println("Premi " + PAUSE + " per mettere in pausa, " + START + " per far ricominciare e " + STOP
                + " per uscire");
        System.out.println("");
    }

    /**
     * Print the statistics to the stdio.
     * 
     * @param statistics the statistisics to print
     */
    private void printStatistics(final Statistics statistics) {
        final Optional<String> out = statistics.getStatistics().stream()
                .map(p -> p.getFirst() + " " + p.getSecond())
                .reduce((a, b) -> a + SEPARATOR + b);
        if (out.isPresent()) {
            System.out.println("\nStatistiche");
            System.out.println(out.get());
        } else {
            System.out.println("\n\n");
        }
    }

    private String getHorizontalBar(final Integer width, final Integer cellWidth) {
        return "\n" + "-".repeat(width * (cellWidth + 2) + 1) + "\n";
    }

    private void printData(final SimulationOutputDataApi data) {
        final Integer width = data.getWidth();
        final Integer height = data.getHeight();
        final Integer cellWidth = data.getData().values().stream().mapToInt(String::length).max().orElse(0) + 1;
        final StringBuilder str = new StringBuilder();

        str.append(getHorizontalBar(width, cellWidth));

        for (int y = 0; y < height; y++) {
            str.append('|');
            for (int x = 0; x < width; x++) {
                final Pos pos = new PosImpl(x, y);
                if (data.getData().containsKey(pos)) {
                    str.append(String.format("%-" + cellWidth + "s |", data.getData().get(pos)));
                } else {
                    str.append(" ".repeat(cellWidth) + " |");
                }
            }
            str.append(getHorizontalBar(width, cellWidth));
        }
        System.out.println(str.toString());
        printStatistics(data.getStatistics());
        printInfo();
    }
}
