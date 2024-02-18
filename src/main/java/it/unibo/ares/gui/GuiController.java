package it.unibo.ares.gui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.Set;

import it.unibo.ares.core.controller.CalculatorSupplier;
import it.unibo.ares.core.utils.directionvector.DirectionVectorImpl;
import it.unibo.ares.core.utils.parameters.Parameter;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.Node;
import java.util.HashMap;
import java.util.LinkedHashSet;

/**
 * GuiController is a class that controls the GUI of the application.
 * It implements the Initializable interface and manages the interaction between
 * the user and the GUI.
 */
public class GuiController implements Initializable {

    /*
     * parameters is an instance of Parameters used to hold the parameters
     */
    Set<Parameter<?>> parameters = new LinkedHashSet<>();

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

    @FXML
    private Button btnInitialize;

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
    private Button btnSetAgent;

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
        btnSetAgent.setDisable(false);
        choiceModel.setOnAction(this::writeModelParametersList);
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
    private void writeModelParametersList(final ActionEvent e) {
        /*
         * write parameters of the model
         */
        String modelIDselected = choiceModel.getValue();
        configurationSessionId = calculatorSupplier.getInitializer().setModel(modelIDselected);
        writer.setModelId(modelIDselected);
        parameters = calculatorSupplier.getInitializer().getModelParametersParameters(configurationSessionId).getParameters();
        writer.setAgentOrModel('m');
        writer.writeVBox(VBOXModelPar, parameters,
                calculatorSupplier.getInitializer());
    }
    
    @FXML
    void btnInitializeClicked(final ActionEvent event) {
        readParameters(VBOXModelPar).entrySet().forEach(e -> {
            calculatorSupplier.getInitializer().setModelParameter(configurationSessionId, e.getKey(), Integer.parseInt(e.getValue().toString()));
        });
        writer.writeChoiceBox(choiceAgent,
                calculatorSupplier.getInitializer().getAgentsSimplified(configurationSessionId));
        disableVBox(VBOXModelPar);
        btnInitialize.setDisable(true);
        btnSetAgent.setDisable(false);
        choiceAgent.setOnAction(this::writeAgentParametersList);
    }

    private HashMap<String, Object> readParameters(VBox vbox) {
        HashMap<String, Object> map = new HashMap<>();
        for (javafx.scene.Node node : vbox.getChildren()) {
            if (node instanceof TextField) {
                TextField textField = (TextField) node;
                switch (parameters.stream().filter(p -> p.getKey().equals(textField.getId())).findFirst().get().getType().getSimpleName()) {
                    case "Integer":
                        //cast to Integer
                        map.put(textField.getId(), Integer.parseInt(textField.getText()));
                        break;
                    case "Double":
                        //cast to Double
                        double value = Double.parseDouble(textField.getText().replace(",", "."));
                        map.put(textField.getId(), value);
                        break;
                    case "Boolean":
                        //cast to Boolean
                        map.put(textField.getId(), Boolean.parseBoolean(textField.getText()));
                        break;
                    case "Float":
                        //cast to Float
                        map.put(textField.getId(), Float.parseFloat(textField.getText()));
                        break;
                    case "DirectionVectorImpl":
                        //cast to Direction Vector
                        // Dividi la stringa in sottostringhe utilizzando lo spazio come delimitatore
                        String[] elementi = textField.getText().split("\\s+");

                        if (elementi.length < 2) {
                            System.out.println("Inserire almeno due elementi separati da spazio!");
                        }
                        map.put(textField.getId(), new DirectionVectorImpl(Integer.parseInt(elementi[0]), Integer.parseInt(elementi[1])));
                        break;
                    default:
                        break;
                }
            }
        }
        return map;
    }

    @FXML
    void btnSetAgentClicked(final ActionEvent event) {
        readParameters(VBOXAgentPar).entrySet().forEach(e -> {
            calculatorSupplier.getInitializer().setAgentParameterSimplified(configurationSessionId, choiceAgent.getValue(), e.getKey(),
                    e.getValue());
        });
    }

    private void disableVBox(VBox vbox) {
        for (javafx.scene.Node node : vbox.getChildren()) {
            if (node instanceof TextField) {
                TextField textField = (TextField) node;
                textField.setDisable(true);
            }
        }
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
         * write parameters of the agent and disable the model parameters
         */
        writer.setAgentOrModel('a');
        parameters = calculatorSupplier.getInitializer().getAgentParametersSimplified(configurationSessionId, 
                choiceAgent
                        .getValue())
                .getParameters();
        writer.writeVBox(VBOXAgentPar, parameters,
                         calculatorSupplier.getInitializer());
    }
}
