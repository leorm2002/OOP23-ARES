package it.unibo.ares.gui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import it.unibo.ares.core.api.DataReciever;
import it.unibo.ares.core.api.SimulationOutputData;
import it.unibo.ares.core.controller.CalculatorSupplier;
import it.unibo.ares.core.utils.directionvector.DirectionVectorImpl;
import it.unibo.ares.core.utils.parameters.Parameter;
import it.unibo.ares.core.utils.parameters.Parameters;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.scene.Node;
import java.util.HashMap;

/**
 * GuiController is a class that controls the GUI of the application.
 * It implements the Initializable interface and manages the interaction between
 * the user and the GUI.
 */
public final class GuiController extends DataReciever implements Initializable {

    /**
     * writer is an instance of WriteOnGUIImpl used to write parameters on the GUI.
     */
    private WriteOnGUI writer = new WriteOnGUIImpl();
    /*
     * configurationSessionId is a string that holds the ID of the configuration
     * 
     */
    private String configurationSessionId, simulationId;

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
        Parent root = FXMLLoader.load(ClassLoader.getSystemResource("scene1.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    /**
     * VBOXAgentPar is a VBox that holds the parameters for the agent.
     */
    private VBox vboxAgentPar;

    @FXML
    /**
     * VBOXModelPar is a VBox that holds the parameters for the model.
     */
    private VBox vboxModelPar;

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
        simulationId = calculatorSupplier.startSimulation(configurationSessionId, this);

        /*
         * switch scene
         */
        Parent root = FXMLLoader.load(ClassLoader.getSystemResource("scene2.fxml"));
        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        Scene scene = new Scene(root);
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
        if (arg0.toString().contains("scene1.fxml")) {
            writer.writeChoiceBox(choiceModel, calculatorSupplier.getModels());
            btnSetAgent.setDisable(true);
            btnStart.setDisable(true);
            choiceModel.setOnAction(this::writeModelParametersList);
        }
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
        configurationSessionId = calculatorSupplier.setModel(modelIDselected);
        writer.writeVBox(vboxModelPar,
                calculatorSupplier.getModelParametersParameters(configurationSessionId)
                        .getParameters());
    }

    @FXML
    void btnInitializeClicked(final ActionEvent event) {
        try {
            readParameters(vboxModelPar,
                    calculatorSupplier.getModelParametersParameters(configurationSessionId))
                    .entrySet().forEach(e -> {
                        calculatorSupplier.setModelParameter(configurationSessionId,
                                e.getKey(),
                                e.getValue());
                    });
                    writer.writeChoiceBox(choiceAgent,
                    calculatorSupplier.getAgentsSimplified(configurationSessionId));
            disableVBox(vboxModelPar);
            btnInitialize.setDisable(true);
            btnSetAgent.setDisable(false);
            choiceAgent.setOnAction(this::writeAgentParametersList);
        } catch (Exception e) {
            showErrorAndDisable(e.getMessage(), btnStart);
        }
        
    }

    private HashMap<String, Object> readParameters(final VBox vbox, Parameters params) {
        HashMap<String, Object> map = new HashMap<>();
        for (javafx.scene.Node node : vbox.getChildren()) {
            if (node instanceof TextField) {
                TextField txt = (TextField) node;
                String type = params.getParameter(txt.getId()).map(Parameter::getType).map(Class::getSimpleName)
                        .orElse("");
                switch (type) {
                    case "Integer":
                        try {
                            params.setParameter(txt.getId(), Integer.parseInt(txt.getText()));
                            map.put(txt.getId(), Integer.parseInt(txt.getText()));
                        } catch (Exception e) {
                            showErrorAndDisable(e.getMessage(), btnStart);
                        }
                        break;
                    case "Double":
                        try {
                            double value = Double.parseDouble(txt.getText().replace(",", "."));
                            params.setParameter(txt.getId(), Double.parseDouble(txt.getText()));
                            map.put(txt.getId(), value);
                        } catch (Exception e) {
                            showErrorAndDisable(e.getMessage(), btnStart);
                        }
                        break;
                    case "Boolean":
                        try {
                            params.setParameter(txt.getId(), Boolean.parseBoolean(txt.getText()));
                            map.put(txt.getId(), Boolean.parseBoolean(txt.getText()));
                        } catch (Exception e) {
                            showErrorAndDisable(e.getMessage(), btnStart);
                        }
                        break;
                    case "Float":
                        try {
                            params.setParameter(txt.getId(), Float.parseFloat(txt.getText()));
                            map.put(txt.getId(), Float.parseFloat(txt.getText()));
                        } catch (Exception e) {
                            showErrorAndDisable(e.getMessage(), btnStart);
                        }
                        break;
                    case "DirectionVectorImpl":
                    /*
                        // cast to Direction Vector
                        // Dividi la stringa in sottostringhe utilizzando lo spazio come delimitatore
                        String[] elementi = txt.getText().split("\\s+");

                        if (elementi.length < 2) {
                            System.out.println("Inserire almeno due elementi separati da spazio!");
                        }
                        DirectionVectorImpl vector = new DirectionVectorImpl(Integer.parseInt(elementi[0]),
                                Integer.parseInt(elementi[1]));
                        if (!inDomainRange(params, txt.getId(), vector)) {
                            showErrorAndDisable(vector + " out of domain range", btnStart);
                            break;
                        }
                        map.put(txt.getId(), vector);
                        break;*/
                    default:
                        break;
                }
            }
        }
        return map;
    }

    @FXML
    void btnSetAgentClicked(final ActionEvent event) {
        try {
            readParameters(vboxAgentPar, calculatorSupplier
                    .getAgentParametersSimplified(configurationSessionId, choiceAgent.getValue()))
                    .entrySet().forEach(e -> {
                        calculatorSupplier.setAgentParameterSimplified(
                                configurationSessionId,
                                choiceAgent.getValue(), e.getKey(), e.getValue());
                    });
            btnStart.setDisable(false);  
        } catch (Exception e) {
            showErrorAndDisable(e.getMessage(), btnStart);
        }
    }

    private void disableVBox(final VBox vbox) {
        for (javafx.scene.Node node : vbox.getChildren()) {
            if (node instanceof TextField) {
                TextField textField = (TextField) node;
                textField.setDisable(true);
            }
        }
    }

    private void showErrorAndDisable(final String message, final Button btn) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Errore");
        alert.setContentText(message);
        alert.showAndWait();
        btn.setDisable(true);
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
        writer.writeVBox(vboxAgentPar, calculatorSupplier
                .getAgentParametersSimplified(configurationSessionId,
                        choiceAgent
                                .getValue())
                .getParameters());
        writer.writeVBox(vboxAgentPar,
                calculatorSupplier
                        .getAgentParametersSimplified(configurationSessionId, choiceAgent.getValue()).getParameters());
    }

    @Override
    public void onNext(SimulationOutputData item) {
        gridPaneMap = write2dMap(item, gridPaneMap);
    }

    private GridPane write2dMap(SimulationOutputData item, GridPane grid) {
        grid.getChildren().clear();
        item.getData().forEach((pos, agent) -> {
            TextField txt = new TextField();
            txt.setText(agent);
            grid.add(txt, pos.getX(), pos.getY());
        });
        return grid;
    }
}
