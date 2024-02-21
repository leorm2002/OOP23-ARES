package it.unibo.ares.gui.controller;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.BiConsumer;

import it.unibo.ares.core.controller.CalculatorSupplier;
import it.unibo.ares.core.utils.StringCaster;
import it.unibo.ares.core.utils.parameters.Parameter;
import it.unibo.ares.core.utils.parameters.Parameters;
import it.unibo.ares.gui.utils.GuiDinamicWriter;
import it.unibo.ares.gui.utils.GuiDinamicWriterImpl;
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
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
/**
 * GuiController is a class that controls the GUI of the application.
 * It implements the Initializable interface and manages the interaction between
 * the user and the GUI.
 */
public final class FirstGuiController implements Initializable {

    /**
     * GuiWriter is an instance of WriteOnGUIImpl used to write parameters on the
     * GUI.
     */
    private GuiDinamicWriter guiWriter = new GuiDinamicWriterImpl();
    /*
     * configurationSessionId is a string that holds the ID of the configuration
     * 
     */
    private String configurationSessionId;

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
    private HBox hboxGrid;

    @FXML
    private GridPane grid;

    @FXML
    private VBox vboxAgentPar, vboxModelPar;

    @FXML
    private ChoiceBox<String> choiceAgent, choiceModel;

    /**
     * The btnStartClicked method is an event handler that is called when the
     * "Start" button is clicked.
     * It starts the simulation and updates the GUI accordingly.
     *
     * @param event the ActionEvent instance representing the button click event
     */
    @FXML
    void btnStartClicked(final ActionEvent event) {
        startSecondGui();
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
        guiWriter.writeChoiceBox(choiceModel, calculatorSupplier.getModels());
        guiWriter.disableButtonIfEnabled(btnStart);
        guiWriter.disableButtonIfEnabled(btnSetAgent);
        guiWriter.disableButtonIfEnabled(btnInitialize);
        guiWriter.disableChoiceBox(choiceAgent);
        choiceModel.setOnAction(this::writeModelParametersList);
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
            BiConsumer<String, Object> parameterSetter = (key, value) -> {
                calculatorSupplier.setModelParameter(configurationSessionId, key, value);
            };
            Parameters modelParameters = calculatorSupplier.getModelParametersParameters(configurationSessionId);
            readParamatersValueAndSet(vboxModelPar, modelParameters, parameterSetter);
            guiWriter.writeChoiceBox(choiceAgent,
                    calculatorSupplier.getAgentsSimplified(configurationSessionId));
            guiWriter.disableVBox(vboxModelPar);
            guiWriter.disableButtonIfEnabled(btnInitialize);
            guiWriter.enableButtonIfDisabled(btnSetAgent);
            guiWriter.enableChoiceBox(choiceAgent);
            choiceAgent.setOnAction(this::writeAgentParametersList);
        } catch (Exception e) {
            guiWriter.showError(e.getMessage());
        }
    }

    /**
     * This method reads the parameters from the VBox and returns them as a HashMap.
     * It iterates over the children of the VBox, and if the child is a TextField,
     * it gets its ID and uses it to retrieve the parameter from the Parameters
     * object.
     * The TextField's ID is then used as the key, and the TextField's text is
     * used as the value in the HashMap.
     *
     * @param vbox   the VBox from which to read the parameters
     * @param params the Parameters object from which to retrieve the parameter
     *               types
     * @param parameterSetter the BiConsumer that sets the parameter in the calculator
     */
    private void readParamatersValueAndSet(final VBox vbox, final Parameters params,
        final BiConsumer<String, Object> parameterSetter) {
        for (javafx.scene.Node node : vbox.getChildren()) {
            if (node instanceof TextField) {
                TextField txt = (TextField) node;
                String typeToString = params.getParameter(txt.getId()).map(Parameter::getType).map(Class::getSimpleName)
                        .orElse("");
                Class<?> type = params.getParameter(txt.getId()).map(Parameter::getType).orElse(null);
                switch (typeToString) {
                    case "Integer":
                        try {
                            parameterSetter.accept(txt.getId(), StringCaster.cast(txt.getText(), type));
                        } catch (Exception e) {
                            guiWriter.showError(e.getMessage());
                        }
                        break;
                    case "Double":
                        try {
                            double value = Double.parseDouble(txt.getText().replace(",", "."));
                            parameterSetter.accept(txt.getId(), value);
                        } catch (Exception e) {
                            guiWriter.showError(e.getMessage());
                        }
                        break;
                    case "Boolean":
                        try {
                            parameterSetter.accept(txt.getId(), StringCaster.cast(txt.getText(), type));
                        } catch (Exception e) {
                            guiWriter.showError(e.getMessage());
                        }
                        break;
                    case "Float":
                        try {
                            parameterSetter.accept(txt.getId(), StringCaster.cast(txt.getText(), type));
                        } catch (Exception e) {
                            guiWriter.showError(e.getMessage());
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
    }

    /**
     * This method is triggered when the Set Agent button is clicked.
     * It reads the parameters from the VBox, sets them in the calculator supplier,
     * and then disables the VBox and the Set Agent button.
     * It also enables the Start button.
     * If any exception occurs during this process, it shows the error message and
     * disables the Set Agent button.
     *
     * @param event the ActionEvent instance representing the event that triggered
     *              this method
     */
    @FXML
    void btnSetAgentClicked(final ActionEvent event) {
        try {
            BiConsumer<String, Object> parameterSetter = (key, value) -> {
                calculatorSupplier.setAgentParameterSimplified(configurationSessionId, choiceAgent.getValue(), key,
                        value);
            };
            Parameters agentParameters = calculatorSupplier.getAgentParametersSimplified(configurationSessionId,
                    choiceAgent.getValue());
            readParamatersValueAndSet(vboxAgentPar, agentParameters, parameterSetter);
            if (everythingIsSet()) {
                guiWriter.enableButtonIfDisabled(btnStart);
            }
        } catch (Exception e) {
            guiWriter.showError(e.getMessage());
        }
    }

    private boolean everythingIsSet() {
        for (String agentID : calculatorSupplier.getAgentsSimplified(configurationSessionId)) {
            if (!calculatorSupplier.getAgentParametersSimplified(configurationSessionId, agentID)
                    .areAllParametersSetted()) {
                return false;
            }
        }
        return true;
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
        Parameters agentParameters = calculatorSupplier.getAgentParametersSimplified(configurationSessionId,
                choiceAgent.getValue());
        guiWriter.clearVBox(vboxAgentPar);
        guiWriter.writeVBox(vboxAgentPar, agentParameters);
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
        choiceAgent.setOnAction(null);
        guiWriter.disableChoiceBox(choiceAgent);
        guiWriter.disableButtonIfEnabled(btnStart);
        guiWriter.clearVBox(vboxModelPar);
        guiWriter.clearVBox(vboxAgentPar);
        configurationSessionId = calculatorSupplier.setModel(choiceModel.getValue());
        Parameters modelParameters = calculatorSupplier
                .getModelParametersParameters(configurationSessionId);
        guiWriter.writeVBox(vboxModelPar, modelParameters);
        guiWriter.enableButtonIfDisabled(btnInitialize);
        guiWriter.disableButtonIfEnabled(btnSetAgent);
    }

    /**
     * This method is used to start the second GUI. It loads the scene from
     * "scene2.fxml",
     * sets the scene to the stage, and shows the stage.
     * If there is an error loading the scene, it prints the stack trace of the
     * exception.
     */
    private void startSecondGui() {
        Parent root;
        Stage stage = (Stage) btnStart.getScene().getWindow();
        try {
            SecondGuiController.setSecondGuiController(configurationSessionId);
            root = FXMLLoader.load(ClassLoader.getSystemResource("scene2.fxml"));
            Scene scene = new Scene(root);
            stage.setTitle("ARES");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
