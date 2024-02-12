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
 * It implements the Initializable interface and manages the interaction between the user and the GUI.
 */
public class GuiController implements Initializable{

    /**
     * writer is an instance of WriteOnGUIImpl used to write parameters on the GUI.
     */
    WriteOnGUI writer = new WriteOnGUIImpl();

    /**
     * calculatorSupplier is an instance of CalculatorSupplier used to supply calculator instances.
     */
    CalculatorSupplier calculatorSupplier = CalculatorSupplier.getInstance();

    /**
     * modelIDselected is a string that holds the ID of the selected model.
     */
    String modelIDselected;

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
     * btnLoadClicked is a method that handles the action event of the Load button being clicked.
     *
     * @param event the ActionEvent instance representing the Load button click event
     */
    void btnLoadClicked(ActionEvent event) {

    }

    @FXML
    void btnStartClicked(ActionEvent event) {
        calculatorSupplier.initializer.setModel(modelIDselected);
        /*
         * set parameters of the model
         */
    }

    @Override
    public void initialize(URL arg0, ResourceBundle arg1) {
        /*
         * write models
         */
        HashMap<String,String> modelMap = calculatorSupplier.initializer.getModels();
        choiceModel.getItems().addAll(modelMap.keySet());
        choiceModel.setOnAction(this::writeAgentsAndModelParametersList);
    }

    private void writeAgentsAndModelParametersList(ActionEvent e) {
        /*
         * write parameters of the model and agents
         */
        String modelIDselected = calculatorSupplier.initializer.getModels().get(choiceModel.getValue());
        calculatorSupplier.initializer.setModel(modelIDselected);
        writer.writeChoiceBox(choiceAgent, calculatorSupplier.initializer.getAgentsSimplified().keySet());
        writer.writeVBox(VBOXModelPar, calculatorSupplier.initializer.getModelParametersParameters(modelIDselected).getParameters());
        
        /*
         * method to call when an agent is selected
         */
        choiceAgent.setOnAction(this::writeAgentParametersList);
    }

    private void writeAgentParametersList(ActionEvent e) {
        /*
         * write parameters of the agent
         */
        writer.writeVBox(VBOXAgentPar, calculatorSupplier.initializer.getAgentParametersSimplified(calculatorSupplier.initializer.getAgentsSimplified().get(choiceAgent.getValue())).getParameters());
    }

}
