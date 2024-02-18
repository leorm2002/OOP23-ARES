package it.unibo.ares.cli;

import it.unibo.ares.core.api.DataReciever;
import it.unibo.ares.core.api.SimulationOutputData;
import it.unibo.ares.core.controller.CalculatorSupplier;
import it.unibo.ares.core.utils.pos.Pos;
import it.unibo.ares.core.utils.pos.PosImpl;

public class SimController extends DataReciever {
    private final String inizializationId;
    private String simulationId;

    public SimController(String inizializationId) {
        this.inizializationId = inizializationId;
    }

    public void startSimulation() {
        System.out.println("Inizio simulazione");
        this.simulationId = CalculatorSupplier.getInstance().startSimulation(inizializationId, this);
        while (true) {
            ;
        }
    }

    @Override
    public void onNext(SimulationOutputData item) {
        printData(item);
    }

    private void printData(SimulationOutputData data) {

        for (int y = 0; y <= data.getHeight(); y++) {
            for (int x = 0; x <= data.getWidth(); x++) {
                Pos pos = new PosImpl(x, y);
                if (data.getData().containsKey(pos)) {
                    System.out.print(data.getData().get(pos));
                } else {
                    System.out.print(" ");
                }
            }
            System.out.println();
        }
    }
}
