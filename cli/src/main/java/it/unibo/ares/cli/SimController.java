package it.unibo.ares.cli;

import java.util.Optional;

import it.unibo.ares.core.api.DataReciever;
import it.unibo.ares.core.api.SimulationOutputDataApi;
import it.unibo.ares.core.controller.AresSupplier;
import it.unibo.ares.core.controller.SimulationOutputData;
import it.unibo.ares.core.utils.pos.Pos;
import it.unibo.ares.core.utils.pos.PosImpl;
import it.unibo.ares.core.utils.statistics.Statistics;

/**
 * The SimController class is responsible for controlling the simulation and
 * displaying the simulation output data.
 */
public final class SimController extends DataReciever {
    private static final String START = "s";
    private static final String PAUSE = "p";
    private static final String STOP = "e";
    private static final String SAVE = "o";
    private static final String LOAD = "l";
    private final String inizializationId;
    private static final String SEPARATOR = "    ";
    private String simulationId;
    private boolean isOver;
    private final IOManager ioManager;

    /**
     * Constructs a SimController object with the specified initialization ID.
     *
     * @param inizializationId the initialization ID for the simulation
     * @param ioManager        the IOManager object to use for input/output
     */
    public SimController(final String inizializationId, final IOManager ioManager) {
        this.inizializationId = inizializationId;
        this.ioManager = ioManager;
    }

    private void processChar(final String ch) {
        switch (ch) {
            case START:
                AresSupplier.getInstance().startSimulation(simulationId);
                break;
            case STOP:
                AresSupplier.getInstance().pauseSimulation(simulationId);
                this.isOver = true;
                break;
            case PAUSE:
                AresSupplier.getInstance().pauseSimulation(simulationId);
                break;
            case SAVE:
                final String savePath = AresSupplier.getInstance().saveSimulation(simulationId);
                this.ioManager.print("Il file è stato salvato in " + savePath);
                this.isOver = true;
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
        ioManager.print("Inizio simulazione");
        this.simulationId = AresSupplier.getInstance().startSimulation(inizializationId, this);
        AresSupplier.getInstance().setTickRate(inizializationId, stepSize);
        final Thread reader = new Thread(new AsyncReader(this::processChar, this::isOver, ioManager));
        reader.start();
        try {
            reader.join();
        } catch (InterruptedException e) {
            ioManager.print("Errore nell'avvio");
        }
    }

    public void startSimulationFromFile(final String savePath, final Integer stepSize) {
        ioManager.print("Inizio simulazione");
        this.simulationId = AresSupplier.getInstance().startSimulationFromFile(savePath, this);
        AresSupplier.getInstance().setTickRate(this.simulationId, stepSize);
        final Thread reader = new Thread(new AsyncReader(this::processChar, this::isOver, ioManager));
        reader.start();
        try {
            reader.join();
        } catch (InterruptedException e) {
            ioManager.print("Errore nell'avvio");
        }
    }

    @Override
    public void onNext(final SimulationOutputData item) {
        printData(item);
    }

    private void printInfo() {
        ioManager
                .print("Premi " + PAUSE + " per mettere in pausa, " + START + " per far ricominciare e " + STOP
                        + " per uscire, " + SAVE + " per salvare ");
        ioManager.print("");
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
        out.ifPresent(s -> {
            ioManager.print("\nStatistiche");
            ioManager.print(s);
        });
    }

    private String getHorizontalBar(final Integer width, final Integer cellWidth) {
        return "\n" + "-".repeat(width * (cellWidth + 2) + 1) + "\n";
    }

    private void printData(final SimulationOutputDataApi data) {
        if (data.isFinished()) {
            ioManager.print("Simulazione terminata, premi " + STOP + " per uscire");
            return;
        }
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
        ioManager.print(str.toString());
        printStatistics(data.getStatistics());
        printInfo();
    }
}
