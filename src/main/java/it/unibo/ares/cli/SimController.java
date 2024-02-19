package it.unibo.ares.cli;

import java.util.Optional;

import it.unibo.ares.core.api.DataReciever;
import it.unibo.ares.core.api.SimulationOutputData;
import it.unibo.ares.core.controller.CalculatorSupplier;
import it.unibo.ares.core.utils.pos.Pos;
import it.unibo.ares.core.utils.pos.PosImpl;

public class SimController extends DataReciever {
    private static final String START = "s";
    private static final String PAUSE = "p";
    private static final String STOP = "e";
    private final String inizializationId;
    private String simulationId;
    Thread reader;

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
                break;
            case PAUSE:
                break;
            default:
                break;
        }
    }

    public void startSimulation() {
        System.out.println("Inizio simulazione");
        this.simulationId = CalculatorSupplier.getInstance().startSimulation(inizializationId, this);
        reader = new Thread(new AsyncReader(this::readChar));
        reader.start();
        while (true) {
            ;
        }
    }

    @Override
    public void onNext(SimulationOutputData item) {
        printData(item);
    }

    private Optional<String> getBorder(final Integer x, final Integer y, final Integer size) {
        if (y == -1 || y == size - 2) {
            return Optional.of("--");
        }
        if (x == -1) {
            return Optional.of("| ");
        }
        if (x == size - 2) {
            return Optional.of(" |");
        }

        return Optional.empty();
    }

    private void printInfo() {
        System.out.println("Premi p per mettere in pausa, s per uscire");
    }

    private void printData(final SimulationOutputData data) {
        Integer newWidth = data.getWidth() + 1;
        Integer newWHeigth = data.getHeight() + 1;
        for (int y = -1; y < data.getHeight() + 1; y++) {
            for (int x = -1; x < data.getWidth() + 1; x++) {
                Pos pos = new PosImpl(x, y);
                if (getBorder(x, y, newWidth).isPresent()) {
                    System.out.print(getBorder(x, y, newWidth).get());
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
    }
}
