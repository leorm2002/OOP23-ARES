package it.unibo.ares.gui.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import it.unibo.ares.core.api.DataReciever;
import it.unibo.ares.core.api.SimulationOutputData;
import it.unibo.ares.core.controller.CalculatorSupplier;
import it.unibo.ares.gui.utils.GuiDinamicWriter;
import it.unibo.ares.gui.utils.GuiDinamicWriterImpl;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.scene.Node;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * GuiController is a class that controls the GUI of the application.
 * It implements the Initializable interface and manages the interaction between
 * the user and the GUI.
 */
public final class SecondGuiController extends DataReciever implements Initializable {

    /**
     * GuiWriter is an instance of WriteOnGUIImpl used to write parameters on the
     * GUI.
     */
    private GuiDinamicWriter guiWriter = new GuiDinamicWriterImpl();
    /*
     * configurationSessionId is a string that holds the ID of the configuration
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
     * The btnPauseClicked method is an event handler that is called when the
     * "Pause" button is clicked.
     * It pauses the simulation and updates the GUI accordingly.
     *
     * @param event the ActionEvent instance representing the button click event
     */
    @FXML
    void btnPauseClicked(final ActionEvent event) {
        calculatorSupplier.pauseSimulation(simulationId);
    }

    /**
     * The btnRestartClicked method is an event handler that is called when the
     * "Restart" button is clicked.
     * It restarts the simulation and updates the GUI accordingly.
     *
     * @param event the ActionEvent instance representing the button click event
     */
    @FXML
    void btnRestartClicked(final ActionEvent event) {
        // calculatorSupplier.restartSimulation(simulationId);
    }

    /**
     * This static method sets the configuration session ID for the
     * SecondGuiController, so the SecondGuiController when initialized
     * can start a new simulation with the given id.
     *
     * @param configurationSessionId the configuration session ID to be set
     */
    public static void setSecondGuiController(final String configurationSessionId) {
        SecondGuiController.configurationSessionId = configurationSessionId;
    }

    /**
     * The btnStopClicked method is an event handler that is called when the "Stop"
     * button is clicked.
     * It stops the simulation and updates the GUI accordingly, switching to scene1
     * where the user can select a new model to initialize.
     *
     * @param event the ActionEvent instance representing the button click event
     */
    @FXML
    void btnStoplicked(final ActionEvent event) throws IOException {
        calculatorSupplier.removeSimulation(simulationId);
        Parent root = FXMLLoader.load(ClassLoader.getSystemResource("scene1.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * This method is called to initialize the controller after its root element has
     * been
     * completely processed. It starts a simulation with the given configuration
     * session ID.
     *
     * @param arg0 The location used to resolve relative paths for the root object,
     *             or null if the location is not known.
     * @param arg1 The resources used to localize the root object, or null if the
     *             root object was not localized.
     */
    @Override
    public void initialize(final URL arg0, final ResourceBundle arg1) {
        simulationId = calculatorSupplier.startSimulation(configurationSessionId, this);
    }
    

    /**
     * This method is called when the next item in the simulation is available.
     * It updates the GUI with the new simulation data on the JavaFX Application
     * thread.
     *
     * @param item The next simulation output data.
     */
    @Override
    public void onNext(final SimulationOutputData item) {
        Platform.runLater(() -> {
            guiWriter.write2dMap(item.getData(), anchorPane, item.getWidth(), item.getHeight());
        });
    }

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

}
