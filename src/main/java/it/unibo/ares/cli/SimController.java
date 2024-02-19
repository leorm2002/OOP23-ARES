package it.unibo.ares.cli;

import java.util.Optional;

import it.unibo.ares.core.api.DataReciever;
import it.unibo.ares.core.api.SimulationOutputData;
import it.unibo.ares.core.controller.CalculatorSupplier;
import it.unibo.ares.core.utils.pos.Pos;
import it.unibo.ares.core.utils.pos.PosImpl;

public class SimController extends DataReciever {
    private final String inizializationId;
    private String simulationId;
    Thread reader;

    public SimController(String inizializationId) {
        this.inizializationId = inizializationId;
    }

    private void readChar(String ch) {
        if (ch.equals('s')) {
            CalculatorSupplier.getInstance().pauseSimulation(this.simulationId);
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
        if (y == 0 || y == size - 1) {
            return Optional.of("--");
        }
        if (y == 1 || y == size - 2) {
            return Optional.of("  ");
        }
        if (x == 0 || x == size - 1) {
            return Optional.of("|");
        }
        if (x == 1 || x == size - 2) {
            return Optional.of(" ");
        }
        return Optional.empty();
    }

    private void printData(final SimulationOutputData data) {
        Integer newWidth = data.getWidth() + 4;
        Integer newWHeigth = data.getHeight() + 4;
        for (int y = 0; y <= data.getHeight() + 4; y++) {
            for (int x = 0; x <= data.getWidth() + 4; x++) {
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
