package it.unibo.ares.gui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import it.unibo.ares.core.api.DataReciever;
import it.unibo.ares.core.api.SimulationOutputData;
import it.unibo.ares.core.controller.CalculatorSupplier;
import it.unibo.ares.core.utils.StringCaster;
import it.unibo.ares.core.utils.directionvector.DirectionVectorImpl;
import it.unibo.ares.core.utils.parameters.Parameter;
import it.unibo.ares.core.utils.parameters.Parameters;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
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
     * GuiWriter is an instance of WriteOnGUIImpl used to write parameters on the
     * GUI.
     */
    private GuiDinamicWriter guiWriter = new GuiDinamicWriterImpl();
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
    private Button btnPause, btnRestart, btnStop, btnStart, btnInitialize, btnSetAgent;

    @FXML
    private GridPane gridPaneMap;

    @FXML
    private VBox vboxAgentPar, vboxModelPar;

    @FXML
    private ChoiceBox<String> choiceAgent, choiceModel;

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
        simulationId = calculatorSupplier.startSimulation(configurationSessionId, this);
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
         * write models if the scene is scene1
         */
        if (arg0.toString().contains("scene1.fxml")) {
            guiWriter.writeChoiceBox(choiceModel, calculatorSupplier.getModels());
            btnSetAgent.setDisable(true);
            btnStart.setDisable(true);
            choiceModel.setOnAction(this::writeModelParametersList);
        }
    }

    /**
     * This method writes the parameters of the model to the VBox.
     * It first sets the model in the calculator supplier using the selected model
     * from the choice box.
     * Then it retrieves the parameters for the model and writes them to the VBox.
     *
     * @param e the ActionEvent instance representing the event that triggered this
     *          method
     */
    private void writeModelParametersList(final ActionEvent e) {
        /*
         * write parameters of the model
         */
        configurationSessionId = calculatorSupplier.setModel(choiceModel.getValue());
        guiWriter.writeVBox(vboxModelPar,
                calculatorSupplier.getModelParametersParameters(configurationSessionId)
                        .getParameters());
    }

    /**
     * This method is triggered when the Initialize button is clicked.
     * It reads the parameters from the VBox, sets them in the calculator supplier,
     * and then disables the VBox.
     * It also sets the choice box with the agents from the calculator supplier.
     * If any exception occurs during this process, it shows the error message and
     * disables the Initialize button.
     *
     * @param event the ActionEvent instance representing the event that triggered
     *              this method
     */
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
            guiWriter.writeChoiceBox(choiceAgent,
                    calculatorSupplier.getAgentsSimplified(configurationSessionId));
            guiWriter.disableVBox(vboxModelPar);
            btnInitialize.setDisable(true);
            btnSetAgent.setDisable(false);
            choiceAgent.setOnAction(this::writeAgentParametersList);
        } catch (Exception e) {
            guiWriter.showErrorAndDisable(e.getMessage(), btnInitialize);
        }
    }

    /**
     * This method reads the parameters from the VBox and returns them as a HashMap.
     * It iterates over the children of the VBox, and if the child is a TextField,
     * it gets its ID and uses it to retrieve the parameter from the Parameters
     * object.
     * The parameter's type is then used as the key, and the TextField's text is
     * used as the value in the HashMap.
     *
     * @param vbox   the VBox from which to read the parameters
     * @param params the Parameters object from which to retrieve the parameter
     *               types
     * @return a HashMap with the parameter types as keys and the TextField texts as
     *         values
     */
    private HashMap<String, Object> readParameters(final VBox vbox, final Parameters params) {
        HashMap<String, Object> map = new HashMap<>();
        for (javafx.scene.Node node : vbox.getChildren()) {
            if (node instanceof TextField) {
                TextField txt = (TextField) node;
                String typeToString = params.getParameter(txt.getId()).map(Parameter::getType).map(Class::getSimpleName)
                        .orElse("");
                Class<?> type = params.getParameter(txt.getId()).map(Parameter::getType).orElse(null);
                switch (typeToString) {
                    case "Integer":
                        try {
                            params.setParameter(txt.getId(), StringCaster.cast(txt.getText(), type));
                            map.put(txt.getId(), Integer.parseInt(txt.getText()));
                        } catch (Exception e) {
                            guiWriter.showErrorAndDisable(e.getMessage(), btnStart);
                        }
                        break;
                    case "Double":
                        try {
                            double value = Double.parseDouble(txt.getText().replace(",", "."));
                            params.setParameter(txt.getId(), value);
                            map.put(txt.getId(), value);
                        } catch (Exception e) {
                            guiWriter.showErrorAndDisable(e.getMessage(), btnStart);
                        }
                        break;
                    case "Boolean":
                        try {
                            params.setParameter(txt.getId(), StringCaster.cast(txt.getText(), type));
                            map.put(txt.getId(), StringCaster.cast(txt.getText(), type));
                        } catch (Exception e) {
                            guiWriter.showErrorAndDisable(e.getMessage(), btnStart);
                        }
                        break;
                    case "Float":
                        try {
                            params.setParameter(txt.getId(), StringCaster.cast(txt.getText(), type));
                            map.put(txt.getId(), StringCaster.cast(txt.getText(), type));
                        } catch (Exception e) {
                            guiWriter.showErrorAndDisable(e.getMessage(), btnStart);
                        }
                        break;
                    case "DirectionVectorImpl":
                        /*
                         * // cast to Direction Vector
                         * // Dividi la stringa in sottostringhe utilizzando lo spazio come delimitatore
                         * String[] elementi = txt.getText().split("\\s+");
                         * 
                         * if (elementi.length < 2) {
                         * System.out.println("Inserire almeno due elementi separati da spazio!");
                         * }
                         * DirectionVectorImpl vector = new
                         * DirectionVectorImpl(Integer.parseInt(elementi[0]),
                         * Integer.parseInt(elementi[1]));
                         * if (!inDomainRange(params, txt.getId(), vector)) {
                         * showErrorAndDisable(vector + " out of domain range", btnStart);
                         * break;
                         * }
                         * map.put(txt.getId(), vector);
                         * break;
                         */
                    default:
                        break;
                }
            }
        }
        return map;
    }

    /**
     * This method is triggered when the Set Agent button is clicked.
     * It reads the parameters from the VBox, sets them in the calculator supplier,
     * and then enables the Start button.
     * If any exception occurs during this process, it shows the error message and
     * disables the Start button.
     *
     * @param event the ActionEvent instance representing the event that triggered
     *              this method
     */
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
            guiWriter.showErrorAndDisable(e.getMessage(), btnStart);
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
        guiWriter.writeVBox(vboxAgentPar, calculatorSupplier
                .getAgentParametersSimplified(configurationSessionId,
                        choiceAgent
                                .getValue())
                .getParameters());
        guiWriter.writeVBox(vboxAgentPar,
                calculatorSupplier
                        .getAgentParametersSimplified(configurationSessionId, choiceAgent.getValue()).getParameters());
    }

    @Override
    public void onNext(final SimulationOutputData item) {
        guiWriter.write2dMap(item, gridPaneMap);
    }
}
