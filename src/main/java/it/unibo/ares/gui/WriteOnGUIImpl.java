package it.unibo.ares.gui;

import java.util.Set;

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
    public void writeVBox(final VBox vbox, final Set<Parameter<?>> parameters) {
        vbox.getChildren().clear();
        final int txtSize = 13, lblSize = 15, domainSize = 13, marginBottom = 20, marginRightLeft = 10;
        parameters.stream().forEach(p -> {
            /*
             * creating a label and a textfield for each parameter and setting his style
             */
            Label lbl = new Label(p.getKey());
            Label domain = new Label();
            TextField txt = new TextField();
            VBox.setMargin(domain, new Insets(0, marginRightLeft, marginBottom, marginRightLeft));
            txt.setId(p.getKey());
            domain.setFont(Font.font(domainSize));
            txt.setFont(Font.font(txtSize));
            lbl.setFont(Font.font(lblSize));
            lbl.wrapTextProperty().setValue(true);
            if (p.getDomain().isPresent()) {
                domain.setText(p.getDomain().get().getDescription());

            }
            try {
                txt.setText(p.getValue().toString());
            } catch (Exception e) {
                txt.setText("");
                txt.setPromptText(p.getType().getSimpleName());
            }
            vbox.getChildren().add(lbl);
            vbox.getChildren().add(txt);
            vbox.getChildren().add(domain);
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
}
