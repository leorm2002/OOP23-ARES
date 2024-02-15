package it.unibo.ares.gui;

import java.util.Set;

import it.unibo.ares.core.controller.SimulationInitializer;
import it.unibo.ares.core.utils.parameters.Parameter;
import javafx.geometry.Insets;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;

/**
 * WriteOnGUIImpl is a class that implements the WriteOnGUI interface.
 * It provides methods to write data to a VBox and a ChoiceBox.
 */
public class WriteOnGUIImpl implements WriteOnGUI {
    /*
     * The agentOrModel attribute is a string that specifies whether the data is for
     * an agent or a model.
     */
    private String agentOrModel;
    /*
     * The modelId attribute is a string that holds the ID of the model.
     */
    private String modelId;

    /**
     * The writeVBox method writes a set of parameters to a VBox. It clears the
     * VBox's children,
     * then for each parameter, it creates a new TextField with the parameter's name
     * as the ID and the parameter's value as the text,
     * and adds the TextField to the VBox's children.
     *
     * @param vbox        the VBox to write the parameters to
     * @param parameters  a Set containing the parameters to write to the VBox
     * @param initializer the SimulationInitializer instance for setting the
     *                    parameters
     */
    @Override
    public void writeVBox(final VBox vbox, final Set<Parameter<?>> parameters,
            final SimulationInitializer initializer) {
        vbox.getChildren().clear();
        final int txtSize = 15, lblSize = 18, marginBottom = 20, marginRightLeft = 10;
        parameters.stream().forEach(p -> {
            /*
             * creating a label and a textfield for each parameter and setting his style
             */
            Label lbl = new Label(p.getKey());
            TextField txt = new TextField();
            VBox.setMargin(txt, new Insets(0, marginRightLeft, marginBottom, marginRightLeft));
            txt.setId(p.getKey());
            txt.setFont(Font.font(txtSize));
            lbl.setFont(Font.font(lblSize));
            lbl.wrapTextProperty().setValue(true);
            /*
             * adding an event listener to the textfield to update the parameter value when
             * the textfield loses focus
             * 2 cases: agent or model texfield, the event listener is different because the
             * method to call is different
             */
            switch (agentOrModel) {
                case "agent":
                    txt.focusedProperty().addListener((obs, oldVal, newVal) -> {
                        if (!newVal) {
                            initializer.setAgentParameterSimplified(this.modelId, txt.getId(), p.getKey(), txt.getText());
                        }
                    });
                    break;
                case "model":
                    txt.focusedProperty().addListener((obs, oldVal, newVal) -> {
                        if (!newVal) {
                            initializer.setModelParameter(this.modelId, p.getKey(), txt.getText());
                        }
                    });
                default:
                    break;
            }
            /*
             * setting the textfield with the value of the parameter and adding it to the
             * vbox
             */
            txt.appendText(p.getValue().toString());
            vbox.getChildren().add(lbl);
            vbox.getChildren().add(txt);
        });
    }

    /**
     * Writes the specified set of strings to a ChoiceBox.
     * It clears the ChoiceBox and then adds the new set of strings.
     *
     * @param choiceBox the ChoiceBox<String> to be written to
     * @param set       the set of strings to be written to the ChoiceBox
     */
    @Override
    public void writeChoiceBox(final ChoiceBox<String> choiceBox, final Set<String> set) {
        /*
         * clearing the choicebox and adding the new set of strings
         */
        choiceBox.getItems().clear();
        choiceBox.getItems().addAll(set);
    }

    /**
     * The setAgentOrModel method sets whether the current context is for an agent
     * or a model.
     * If the input character is 'a', the context is set to "agent". Otherwise, it
     * is set to "model".
     *
     * @param c the character indicating the context (either 'a' or 'm')
     */
    public void setAgentOrModel(final char c) {
        this.agentOrModel = c == 'a' ? "agent" : "model";
    }

    /*
     * The setModelId method sets the modelId attribute to the specified string.
     */
    public void setModelId(final String modelId) {
        this.modelId = modelId;
    }
}
