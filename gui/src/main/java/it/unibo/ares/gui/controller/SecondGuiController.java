package it.unibo.ares.gui.controller;

import it.unibo.ares.core.api.DataReciever;
import it.unibo.ares.core.controller.CalculatorSupplier;
import it.unibo.ares.core.controller.SimulationOutputData;
import it.unibo.ares.gui.utils.GuiDinamicWriter;
import it.unibo.ares.gui.utils.GuiDinamicWriterImpl;
import it.unibo.ares.gui.utils.HandlerAdapter;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * GuiController is a class that controls the GUI of the application.
 * It implements the Initializable interface and manages the interaction between
 * the user and the GUI.
 */
public final class SecondGuiController extends DataReciever implements Initializable {

    /**
     * GuiWriter is an instance of WriteOnGUIImpl used to write on the
     * GUI.
     */
    private GuiDinamicWriter guiWriter = new GuiDinamicWriterImpl();
    /*
     * configurationSessionId is a string that holds the ID of the configuration
     * simlationId is a string that holds the ID of the simulation
     * 
     */
    private static String configurationSessionId;
    private String simulationId;

    // we set the max tick rate to 1200 ms, we can set it to whatever value we want
    private static final double MAXSTEP = 5000;

    /**
     * calculatorSupplier is an instance of CalculatorSupplier used to supply
     * calculator instances.
     */
    private CalculatorSupplier calculatorSupplier = CalculatorSupplier.getInstance();

    /*
     * FXML variables
     */
    @FXML
    private Button btnPause, btnRestart, btnStop;
    @FXML
    private AnchorPane anchorPane;

    @FXML
    private Label lblStep;

    @FXML
    private Slider slidStep;

    /**
     * This static method sets the configuration session ID for the
     * SecondGuiController, so the SecondGuiController when initialized
     * can start a new simulation with the given id.
     *
     * @param configurationSessionId the configuration session ID to be set
     */
    public static void setConfigurationId(final String configurationSessionId) {
        SecondGuiController.configurationSessionId = configurationSessionId;
    }

    /**
     * This method is called to initialize the controller after its root element has
     * been
     * completely processed. It starts a simulation with the given configuration
     * session ID.
     * It also sets the event handler adapters for the buttons in the GUI.
     *
     * @param arg0 The location used to resolve relative paths for the root object,
     *             or null if the location is not known.
     * @param arg1 The resources used to localize the root object, or null if the
     *             root object was not localized.
     */
    @Override
    public void initialize(final URL arg0, final ResourceBundle arg1) {
        simulationId = calculatorSupplier.startSimulation(configurationSessionId, this);
        btnPause.setOnAction(new HandlerAdapter(this::pauseSimulation));
        btnRestart.setOnAction(new HandlerAdapter(this::restartSimulation));
        btnStop.setOnAction(new HandlerAdapter(this::stopSimulation));
        lblStep.setText("Step: " + calculatorSupplier.getTickRate(simulationId) + " ms");
        slidStep.setValue(mapToSliderStep(calculatorSupplier.getTickRate(simulationId)));
        slidStep.valueProperty().addListener(new ChangeListener<Number>() {
            /**
             * This method is called when the value of the observable object is changed.
             * It updates the step of the simulation and the label accordingly.
             */
            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldNumber,
                    Number newNumber) {
                int step = mapToRange(slidStep.getValue(), slidStep.getMin(), slidStep.getMax(), (int) MAXSTEP,
                        (int) CalculatorSupplier.getInstance().getTickRate());
                String stepString = step != 0 ? String.valueOf(step) + " ms" : "Max speed";
                lblStep.setText("Step: " + stepString);
                calculatorSupplier.setTickRate(simulationId, step);
            }
        });
    }

    /**
     * This method maps the value, given in ms, in a decimal value between 0 and 1
     * to be used in the slider.
     *
     * @param value the value in ms
     * @return the value mapped to be used in the slider
     */
    private double mapToSliderStep(final int value) {
        int transitionPoint = 1000;

        return value <= transitionPoint ? (double) value / transitionPoint * 0.5
                : 0.5 + ((double) (value - transitionPoint) / (5000 - transitionPoint)) * 0.5;
    }

    /*
     * This method maps the input value to a range between minOutput and maxOutput
     * Maps the slider value from 0.0 to 0.5 in 0-1000 ms and from 0.5 to 1.0 in
     * 1000-5000 ms
     *
     */
    private int mapToRange(double inputValue, double minInput, double maxInput, int maxOutput,
            int step) {
        // Ensure the value is within the specified range
        Double value = Math.max(minInput, Math.min(maxInput, inputValue));

        double transitionPoint = 0.5;
        double mappedValue = value <= transitionPoint ? (inputValue / transitionPoint * 1000)
                : 1000 + (value - transitionPoint) * 8000;
        mappedValue = Math.round(mappedValue / step) * (float) step;
        return (int) mappedValue;
    }

    /**
     * This method is called when the next item in the simulation is available.
     * It updates the GUI with the new simulation data on the JavaFX Application
     * thread. DATARECIEVER INTERFACE
     * If the simulation is finished, it shows an alert to the user and disables the
     * pause and restart buttons.
     *
     * @param item The next simulation output data.
     */
    @Override
    public void onNext(final SimulationOutputData item) {
        if (!item.isFinished()) {
            Platform.runLater(() -> {
                guiWriter.write2dMap(item.getData(), anchorPane, item.getWidth(), item.getHeight());
            });
        } else {
            Platform.runLater(() -> {
                guiWriter.showAlert(
                        "Fine della simulazione, premi stop per tornare alla selezione di un nuovo modello.");
                guiWriter.disableElement(btnPause);
                guiWriter.disableElement(btnRestart);
                guiWriter.disableElement(slidStep);
            });
        }
    }

    // METHODS TO HANDLE

    /**
     * The pauseSimulation method is called when we want to stop the simulation.
     * It pauses the simulation and the GUI will not be updated until the
     * simulation is restarted (no OnNext method called).
     */
    void pauseSimulation() {
        calculatorSupplier.pauseSimulation(simulationId);
    }

    /**
     * The restartSimulation method is called when we want to restart the
     * simulation.
     * It restarts the simulation and the GUI will be updated accordingly.
     */
    void restartSimulation() {
        calculatorSupplier.startSimulation(simulationId);
    }

    /**
     * The stopSimulation method is called when we want to stop the simulation.
     * It stops the simulation and updates the GUI accordingly, switching to scene1
     * where the user can select a new model to initialize.
     */
    @FXML
    void stopSimulation() {
        calculatorSupplier.removeSimulation(simulationId);
        Parent root;
        try {
            root = FXMLLoader.load(ClassLoader.getSystemResource("scene1.fxml"));
            Stage stage = (Stage) btnStop.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
