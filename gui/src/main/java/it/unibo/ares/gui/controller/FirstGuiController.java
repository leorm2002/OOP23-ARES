package it.unibo.ares.gui.controller;

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
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.function.BiConsumer;
/**
 * GuiController is a class that controls the first GUI of the application.
 * It implements the Initializable interface and manages the interaction between
 * the user and the GUI.
 */
public final class FirstGuiController implements Initializable {

    /**
     * GuiWriter is an instance of WriteOnGUIImpl used to manage the
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
     * calculator instances, models, and agents.
     */
    private CalculatorSupplier calculatorSupplier = CalculatorSupplier.getInstance();

    /*
     * FXML variables
     */
    @FXML
    private Button btnStart, btnInitialize, btnSetAgent;

    /*
     * The VBox that holds the parameters of the agent and the model
     */
    @FXML
    private VBox vboxAgentPar, vboxModelPar;

    /*
     * The choice boxes that hold the agents and the models
     */
    @FXML
    private ChoiceBox<String> choiceAgent, choiceModel;

    /**
     * This method is triggered when the 'Start' button is clicked.
     * It starts the second GUI.
     *
     * @param event the ActionEvent associated with the button click
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
     * agents and model parameters list, and disables all the rest of the GUI
     * for preventing the user to interact with it before the model is selected.
     *
     * @param arg0 The location used to resolve relative paths for the root object,
     *             or null if the location is not known.
     * @param arg1 The resources used to localize the root object, or null if the
     *             root object was not localized.
     */
    @Override
    public void initialize(final URL arg0, final ResourceBundle arg1) {
        guiWriter.writeChoiceBox(choiceModel, calculatorSupplier.getModels());
        guiWriter.disableButton(btnStart);
        guiWriter.disableButton(btnSetAgent);
        guiWriter.disableButton(btnInitialize);
        guiWriter.disableChoiceBox(choiceAgent);
        choiceModel.setOnAction(this::writeModelParametersList);
    }

    /**
     * This method is triggered when the Initialize button is clicked.
     * It reads the parameters from the VBox, sets them in the calculator supplier,
     * and then disables the VBox and the Initialize button.
     * It also enables the Set Agent button and the ChoiceBox for the agents.
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
            guiWriter.disableButton(btnInitialize);
            guiWriter.enableButton(btnSetAgent);
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
        /*
         * iterate over the children of the vbox and if the child is a TextField, get its
         * ID and use it to retrieve the parameter from the Parameters object. The
         * TextField's ID is then used as the key, and the TextField's text is used as
         * the value.
         */
        vbox.getChildren().stream().filter(node -> node instanceof TextField).map(node -> (TextField) node)
                .forEach(txt -> {
                    String typeToString = params.getParameter(txt.getId()).map(Parameter::getType).map(Class::getSimpleName)
                            .orElse("");
                    Class<?> type = params.getParameter(txt.getId()).map(Parameter::getType).orElse(null);
                    /*
                     * switch on the type of the parameter and cast the text of the TextField to the
                     * correct type for setting it in the calculator
                     * switch on simpleName instead of the class because we can also have not built-in
                     * types, like DirectionVectorImpl
                     */
                    switch (typeToString) {
                        /*
                         * try to set the parameter in the calculator, if an exception occurs, show the
                         * error message
                         */
                        case "String":
                            try {
                                parameterSetter.accept(txt.getId(), txt.getText());
                            } catch (Exception e) {
                                guiWriter.showError(e.getMessage());
                            }
                            break;
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
                             *      showErrorAndDisable(vector + " out of domain range", btnStart);
                             *      break;
                             * }
                             * map.put(txt.getId(), vector);
                             * break;
                             */
                        default:
                            break;
                    }
                });
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
                guiWriter.enableButton(btnStart);
            }
        } catch (Exception e) {
            guiWriter.showError(e.getMessage());
        }
    }

    /**
     * This method is used to check if all the parameters are set.
     * It iterates over the agents and checks if all the parameters are set.
     * If all the parameters are set, it returns true, otherwise it returns false.
     * Helpfull to enable the start button.
     * @return true if all the parameters are set, false otherwise
     */
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
     * This method is used to write the agent parameters to the VBox.
     * It first gets the agent parameters from the calculator supplier, then clears
     * the VBox,
     * and finally writes the parameters to the VBox.
     * @param e the ActionEvent instance representing the event that triggered this
     *         method
     */
    private void writeAgentParametersList(final ActionEvent e) {
        Parameters agentParameters = calculatorSupplier.getAgentParametersSimplified(configurationSessionId,
                choiceAgent.getValue());
        guiWriter.clearVBox(vboxAgentPar);
        guiWriter.writeVBox(vboxAgentPar, agentParameters);
    }

    /**
     * This method is used to write the model parameters to the VBox.
     * It first disables the agent choice box and the start button, then clears the
     * VBoxes,
     * sets the model in the calculator supplier, gets the model parameters from the
     * calculator supplier,
     * writes the parameters to the VBox, enables the initialize button, and finally
     * disables the set agent button.
     *
     * @param e the ActionEvent associated with the button click
     */
    private void writeModelParametersList(final ActionEvent e) {
        choiceAgent.setOnAction(null);
        guiWriter.disableChoiceBox(choiceAgent);
        guiWriter.disableButton(btnSetAgent);
        guiWriter.disableButton(btnStart);
        /*
         * detach the event handler from the choiceAgent and disable the choiceAgent
         * and the setAgent button to prevent the user to interact with them before the
         * model is selected, helpfull when the user changes the model after he has
         * already selected one
         */
        guiWriter.clearVBox(vboxModelPar);
        guiWriter.clearVBox(vboxAgentPar);
        configurationSessionId = calculatorSupplier.setModel(choiceModel.getValue());
        Parameters modelParameters = calculatorSupplier
                .getModelParametersParameters(configurationSessionId);
        guiWriter.writeVBox(vboxModelPar, modelParameters);
        guiWriter.enableButton(btnInitialize);
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
