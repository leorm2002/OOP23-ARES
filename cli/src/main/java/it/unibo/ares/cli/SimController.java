package it.unibo.ares.cli;

import it.unibo.ares.core.api.DataReciever;
import it.unibo.ares.core.api.SimulationOutputData;
import it.unibo.ares.core.api.SimulationOutputDataApi;
import it.unibo.ares.core.controller.CalculatorSupplier;
import it.unibo.ares.core.utils.pos.Pos;
import it.unibo.ares.core.utils.pos.PosImpl;

import java.util.Optional;

public final class SimController extends DataReciever {
    private static final String START = "s";
    private static final String PAUSE = "p";
    private static final String STOP = "e";
    private final String inizializationId;
    private String simulationId;
    private boolean isOver = false;

    public SimController(String inizializationId) {
        this.inizializationId = inizializationId;
    }

    private void readChar(String ch) {
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

    public void startSimulation() {
        System.out.println("Inizio simulazione");
        this.simulationId = CalculatorSupplier.getInstance().startSimulation(inizializationId, this);
        Thread reader = new Thread(new AsyncReader(this::readChar, this::isOver));
        reader.start();
        try {
            reader.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onNext(SimulationOutputData item) {
        printData(item);
    }

    private Optional<String> getBorder(final Integer x, final Integer y, final Integer width, final Integer height) {
        if (y == -1 || y == width - 2) {
            return Optional.of("--");
        }
        if (x == -1) {
            return Optional.of("| ");
        }
        if (x == width - 2) {
            return Optional.of(" |");
        }

        return Optional.empty();
    }

    private void printInfo() {
        System.out.println("Premi " + PAUSE + " per mettere in pausa, " + START + " per far ricominciare e " + STOP
                + " per uscire");
        System.out.println("");
    }

    private void printData(final SimulationOutputDataApi data) {
        Integer offset = 1;
        Integer width = data.getWidth() + offset;
        Integer height = data.getHeight() + offset;
        for (int y = -1; y < height; y++) {
            for (int x = -1; x < width; x++) {
                Pos pos = new PosImpl(x, y);
                if (getBorder(x, y, width, height).isPresent()) {
                    System.out.print(getBorder(x, y, width, height).get());
                } else {
                    if (data.getData().containsKey(pos)) {
                        System.out.print(data.getData().get(pos) + " ");
                    } else {
                        System.out.print("  ");
                    }
                }
            }
            System.out.println();
        }
        printInfo();
    }
}
