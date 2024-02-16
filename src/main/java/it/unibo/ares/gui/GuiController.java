package it.unibo.ares.gui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import it.unibo.ares.core.controller.CalculatorSupplier;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.Node;

/**
 * GuiController is a class that controls the GUI of the application.
 * It implements the Initializable interface and manages the interaction between
 * the user and the GUI.
 */
public class GuiController implements Initializable {

    /**
     * writer is an instance of WriteOnGUIImpl used to write parameters on the GUI.
     */
    private WriteOnGUI writer = new WriteOnGUIImpl();
    /*
     * configurationSessionId is a string that holds the ID of the configuration
     * 
     */
    private String configurationSessionId, simulationId;
    private RecieverImpl reciever = new RecieverImpl();
    /*
     * variables useful for switching scene
     */
    private Stage stage;
    private Scene scene;
    private Parent root;

    /**
     * calculatorSupplier is an instance of CalculatorSupplier used to supply
     * calculator instances.
     */
    private CalculatorSupplier calculatorSupplier = CalculatorSupplier.getInstance();

    /*
     * FXML variables
     */
    @FXML
    private Button btnPause;

    @FXML
    private Button btnRestart;

    @FXML
    private Button btnStop;

    @FXML
    private GridPane gridPaneMap;

    @FXML
    private Label lblModelChose;

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
        calculatorSupplier.getController().pauseSimulation(simulationId);
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
        calculatorSupplier.getController().restartSimulation(simulationId);
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
         * stop simulation and switch scene to scene1, where the user can select a new model
         */
        root = FXMLLoader.load(getClass().getResource("scene1.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }


    /**
     * modelIDselected is a string that holds the ID of the selected model.
     */
    private String modelIDselected;

    @FXML
    /**
     * VBOXAgentPar is a VBox that holds the parameters for the agent.
     */
    private VBox VBOXAgentPar;

    @FXML
    /**
     * VBOXModelPar is a VBox that holds the parameters for the model.
     */
    private VBox VBOXModelPar;

    @FXML
    /**
     * btnLoad is a Button that triggers the loading of a model when clicked.
     */
    private Button btnLoad;

    @FXML
    /**
     * btnStart is a Button that triggers the start of the application when clicked.
     */
    private Button btnStart;

    @FXML
    /**
     * choiceAgent is a ChoiceBox that allows the user to select an agent.
     */
    private ChoiceBox<String> choiceAgent;

    @FXML
    /**
     * choiceModel is a ChoiceBox that allows the user to select a model.
     */
    private ChoiceBox<String> choiceModel;

    /**
     * btnLoadClicked is a method that handles the action event of the Load button
     * being clicked.
     *
     * @param event the ActionEvent instance representing the Load button click
     *              event
     */
    @FXML
    void btnLoadClicked(final ActionEvent event) {

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
         * start simulation
         */
        configurationSessionId = calculatorSupplier.getInitializer().setModel(modelIDselected);
        simulationId = calculatorSupplier.startSimulation(configurationSessionId, reciever);


        /*
         * switch scene
         */
        lblModelChose.setText(modelIDselected);
        Parent root = FXMLLoader.load(getClass().getResource("scene2.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();

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
        /*
         * write models
         */
        writer.writeChoiceBox(choiceModel, calculatorSupplier.getInitializer().getModels());
        choiceModel.setOnAction(this::writeAgentsAndModelParametersList);
    }

    /**
     * The writeAgentsAndModelParametersList method is called when an action event
     * occurs.
     * It writes the parameters of the model and agents to the GUI.
     * It also sets an action event handler for the choiceAgent that calls the
     * writeAgentParametersList method.
     *
     * @param e the ActionEvent instance representing the event that triggered this
     *          method
     */
    private void writeAgentsAndModelParametersList(final ActionEvent e) {
        /*
         * write parameters of the model and agents
         */
        String modelIDselected = choiceModel.getValue();
        calculatorSupplier.getInitializer().setModel(modelIDselected);
        writer.setModelId(modelIDselected);
        writer.writeChoiceBox(choiceAgent, calculatorSupplier.getInitializer().getAgentsSimplified(modelIDselected));
        writer.setAgentOrModel('m');
        writer.writeVBox(VBOXModelPar,
                calculatorSupplier.getInitializer().getModelParametersParameters(modelIDselected).getParameters(),
                calculatorSupplier.getInitializer());
        /*
         * method to call when an agent is selected
         */
        choiceAgent.setOnAction(this::writeAgentParametersList);
    }

    /**
     * The writeAgentParametersList method is called when an action event occurs.
     * It writes the parameters of the selected agent to the GUI.
     *
     * @param e the ActionEvent instance representing the event that triggered this
     *          method
     */
    private void writeAgentParametersList(final ActionEvent e) {
        /*
         * write parameters of the agent
         */
        writer.setAgentOrModel('a');
        writer.writeVBox(VBOXAgentPar,
                calculatorSupplier.getInitializer()
                        .getAgentParametersSimplified(choiceAgent.getValue()).getParameters(),
                         calculatorSupplier.getInitializer());
    }
}
