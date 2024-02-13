package it.unibo.ares.gui;

import java.net.URL;
import java.util.HashMap;
import java.util.ResourceBundle;

import it.unibo.ares.core.controller.CalculatorSupplier;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.VBox;

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

    /**
     * calculatorSupplier is an instance of CalculatorSupplier used to supply
     * calculator instances.
     */
    private CalculatorSupplier calculatorSupplier = CalculatorSupplier.getInstance();

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

    @FXML
    /**
     * btnLoadClicked is a method that handles the action event of the Load button
     * being clicked.
     *
     * @param event the ActionEvent instance representing the Load button click
     *              event
     */
    void btnLoadClicked(final ActionEvent event) {

    }

    @FXML
    /**
     * btnStartClicked is a method that handles the action event of the Start button
     * being clicked. It sets the model in the calculator initializer to the
     * selected model ID and sets the parameters of the model and the agents.
     *
     * @param event the ActionEvent instance representing the Start button click
     *              event
     */
    void btnStartClicked(final ActionEvent event) {
        calculatorSupplier.getInitializer().setModel(modelIDselected);
        ParameterSetter parameterSetter = new ParameterSetter();
        ParameterReader parameterReader = new ParameterReader();
        HashMap<String, Object> modelParameters = parameterReader.readParameters(VBOXModelPar);
        parameterSetter.setModelParameters(calculatorSupplier.getInitializer(), modelParameters);
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
        //HashMap<String, String> modelMap = calculatorSupplier.getInitializer().getModels();
        //choiceModel.getItems().addAll(modelMap.keySet());
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
        String modelIDselected = calculatorSupplier.getInitializer().getModels().get(choiceModel.getValue());
        calculatorSupplier.getInitializer().setModel(modelIDselected);
        writer.writeChoiceBox(choiceAgent, calculatorSupplier.getInitializer().getAgentsSimplified().keySet());
        writer.writeVBox(VBOXModelPar,
                calculatorSupplier.getInitializer().getModelParametersParameters(modelIDselected).getParameters());
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
        writer.writeVBox(VBOXAgentPar,
                calculatorSupplier.getInitializer()
                        .getAgentParametersSimplified(
                                calculatorSupplier.getInitializer().getAgentsSimplified().get(choiceAgent.getValue()))
                        .getParameters());
    }

}
