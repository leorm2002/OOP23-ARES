package it.unibo.ares.cli;

import it.unibo.ares.core.api.DataReciever;
import it.unibo.ares.core.api.SimulationOutputDataApi;
import it.unibo.ares.core.controller.CalculatorSupplier;
import it.unibo.ares.core.controller.SimulationOutputData;
import it.unibo.ares.core.utils.pos.Pos;
import it.unibo.ares.core.utils.pos.PosImpl;

/**
 * The SimController class is responsible for controlling the simulation and
 * displaying the simulation output data.
 */
public final class SimController extends DataReciever {
    private static final String START = "s";
    private static final String PAUSE = "p";
    private static final String STOP = "e";
    private final String inizializationId;
    private String simulationId;
    private boolean isOver = false;

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
     */
    public void startSimulation(Integer stepSize) {
        System.out.println("Inizio simulazione");
        this.simulationId = CalculatorSupplier.getInstance().startSimulation(inizializationId, this);
        CalculatorSupplier.getInstance().setTickRate(inizializationId, stepSize);
        Thread reader = new Thread(new AsyncReader(this::readChar, this::isOver));
        reader.start();
        try {
            reader.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
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

    private String getHorizontalBar(final Integer width, final Integer cellWidth) {
        return "\n" + "-".repeat(width * (cellWidth + 2) + 1) + "\n";
    }

    private void printData(final SimulationOutputDataApi data) {
        Integer width = data.getWidth();
        Integer height = data.getHeight();
        Integer cellWidth = data.getData().values().stream().mapToInt(String::length).max().orElse(0) + 1;
        var str = new StringBuilder();

        str.append(getHorizontalBar(width, cellWidth));

        for (int y = 0; y < height; y++) {
            str.append("|");
            for (int x = 0; x < width; x++) {
                Pos pos = new PosImpl(x, y);
                if (data.getData().containsKey(pos)) {
                    str.append(String.format("%-" + cellWidth + "s |", data.getData().get(pos)));
                } else {
                    str.append(" ".repeat(cellWidth) + " |");
                }
            }
            str.append(getHorizontalBar(width, cellWidth));
        }
        System.out.println(str.toString());
        printInfo();
    }
}
