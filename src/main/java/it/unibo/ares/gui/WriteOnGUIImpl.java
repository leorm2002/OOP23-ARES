package it.unibo.ares.gui;

import java.util.Iterator;
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
public class WriteOnGUIImpl implements WriteOnGUI{

    /**
     * Writes the specified set of parameters to a VBox.
     * It creates a label and a textfield for each parameter and adds them to the VBox.
     *
     * @param vbox the VBox to be written to
     * @param parameters the set of parameters to be written to the VBox
     */
    @Override
    public void writeVBox(VBox vbox, Set<Parameter<?>> parameters) {
        vbox.getChildren().clear();
        Iterator<Parameter<?>> it = parameters.iterator();
        
        while(it.hasNext()){
            Parameter<?> p = it.next();

            /*
             * creating a label and a textfield for each parameter and setting his style
             */
            Label lbl = new Label(p.getKey());
            TextField txt = new TextField();
            VBox.setMargin(txt, new Insets(0, 10, 20, 10));
            txt.setId("txt"+p.getKey());
            txt.setFont(Font.font(15));
            lbl.setFont(Font.font(18));
            lbl.wrapTextProperty().setValue(true);

            /*
             * setting the textfield with the value of the parameter and adding it to the vbox
             */
            txt.appendText(p.getValue().toString());
            vbox.getChildren().add(lbl);
            vbox.getChildren().add(txt);
        }
    }

    @Override
    public void writeChoiceBox(ChoiceBox<String> choiceBox, Set<String> set) {
        /*
         * clearing the choicebox and adding the new set of strings
         */
        choiceBox.getItems().clear();
        choiceBox.getItems().addAll(set);
    }
    
}
