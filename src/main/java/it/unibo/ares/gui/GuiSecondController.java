package it.unibo.ares.gui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import it.unibo.ares.core.api.DataReciever;
import it.unibo.ares.core.api.SimulationOutputData;
import it.unibo.ares.core.controller.CalculatorSupplier;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.scene.Node;

/**
 * GuiController is a class that controls the GUI of the application.
 * It implements the Initializable interface and manages the interaction between
 * the user and the GUI.
 */
public final class GuiSecondController extends DataReciever implements Initializable {

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
    private HBox hboxGrid;

    @FXML
    private GridPane grid;

    /**
     * The btnPauseClicked method is an event handler that is called when the
     * "Pause" button is clicked.
     * It pauses the simulation and updates the GUI accordingly.
     *
     * @param event the ActionEvent instance representing the button click event
     */
    @FXML
    void btnPauseClicked(final ActionEvent event) {
        /*
         * pause simulation
         */
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
        /*
         * restart simulation
         */
        // calculatorSupplier.getController().restartSimulation(simulationId);
    }

    public static void setConfigurationSessionId(String configurationSessionId) {
        GuiSecondController.configurationSessionId = configurationSessionId;
    }
    /**
     * The btnStopClicked method is an event handler that is called when the "Stop"
     * button is clicked.
     * It stops the simulation and updates the GUI accordingly.
     *
     * @param event the ActionEvent instance representing the button click event
     */
    @FXML
    void btnStoplicked(final ActionEvent event) throws IOException {
        /*
         * stop simulation and switch scene to scene1, where the user can select a new
         * model
         */
        calculatorSupplier.pauseSimulation(simulationId);
        calculatorSupplier.removeSimulation(simulationId);
        Parent root = FXMLLoader.load(ClassLoader.getSystemResource("scene1.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    /**
     * The btnStartClicked method is an event handler that is called when the
     * "Start" button is clicked.
     * It starts the simulation and updates the GUI accordingly.
     *
     * @param event the ActionEvent instance representing the button click event
     */
    @FXML
    void btnStartClicked(final ActionEvent event) throws IOException {
        /*
         * start simulation and switch scene to scene2, where the user can see the
         * simulation
         */
        Parent root = FXMLLoader.load(ClassLoader.getSystemResource("scene2.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
        // initialize(new
        // URL("file:/C:/Users/loren/Desktop/ARES/ARES/build/resources/main/scene2.fxml"),
        // null);

    }

    /**
     * The initialize method is called after all @FXML annotated members have been
     * injected.
     * This method initializes the choiceModel with the model names from the
     * calculator initializer.
     * It also sets an action event handler for the choiceModel that writes the
     * agents and model parameters list.
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

    private GuiDinamicWriter writer = new GuiDinamicWriterImpl();

    @Override
    public void onNext(final SimulationOutputData item) {
        int size = item.getHeight();
        Platform.runLater(() -> {
            writer.write2dMap(item, hboxGrid, size);
        
        });
        //grid.getChildren().add(new Label("ciao"));
        //guiWriter.write2dMap(item, grid, 10);
        /*for (int row = 0; row < size; row++) {
            for (int col = 0; col < size; col++) {
                Label label = new Label();
                grid.add(label, col, row); // Aggiunge il nodo alla cella corrispondente
                grid.setVgap(10);
                grid.setHgap(10);
            }
        }
        item.getData().forEach((pos, agent) -> {
        Label txt = new Label(agent);
        GridPane.setConstraints(txt, pos.getX(), pos.getY());
        });
        //hboxGrid.getChildren().add(grid);*/
    }

    public void startFirstGui(Stage stage) {
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
