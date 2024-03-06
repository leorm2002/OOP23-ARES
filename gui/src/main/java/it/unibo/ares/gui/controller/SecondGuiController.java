package it.unibo.ares.gui.controller;

import it.unibo.ares.core.api.DataReciever;
import it.unibo.ares.core.controller.CalculatorSupplier;
import it.unibo.ares.core.controller.SimulationOutputData;
import it.unibo.ares.gui.utils.GuiDinamicWriter;
import it.unibo.ares.gui.utils.GuiDinamicWriterImpl;
import it.unibo.ares.gui.utils.HandlerAdapter;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
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
     * It also sets the event handlers for the buttons in the GUI.
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
    }

    /**
     * This method is called when the next item in the simulation is available.
     * It updates the GUI with the new simulation data on the JavaFX Application
     * thread. DATARECIEVER INTERFACE
     *
     * @param item The next simulation output data.
     */
    @Override
    public void onNext(final SimulationOutputData item) {
        Platform.runLater(() -> {
            guiWriter.write2dMap(item.getData(), anchorPane, item.getWidth(), item.getHeight());
        });
    }

    //METHODS TO HANDLE

    /**
     * This method is used to start the first GUI. It loads the scene from
     * "scene1.fxml",
     * sets the scene to the stage, and shows the stage.
     *
     * @param stage The stage on which the scene is to be set and shown.
     */
    public void startFirstGui(final Stage stage) {
        Parent root;
        try {
            root = FXMLLoader.load(ClassLoader.getSystemResource("scene1.fxml"));
            Scene scene = new Scene(root);
            stage.setTitle("ARES");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * The pauseSimulation method is called when the
     * "Pause" button is clicked.
     * It pauses the simulation and the GUI will not be updated until the
     * simulation is restarted (no OnNext method called).
     */
    void pauseSimulation() {
        calculatorSupplier.pauseSimulation(simulationId);
    }

    /**
     * The restartSimulation method is called when the
     * "Restart" button is clicked.
     * It restarts the simulation and the GUI will be updated accordingly.
     */
    void restartSimulation() {
        calculatorSupplier.startSimulation(simulationId);
    }

    /**
     * The stopSimulation method  is called when the "Stop"
     * button is clicked.
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
