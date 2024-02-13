package it.unibo.ares.gui;

import java.util.HashMap;

import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

/**
 * ParameterReader is a class that provides a method to read parameters from a
 * VBox.
 */
public class ParameterReader {

    /**
     * The readParameters method reads parameters from a VBox. It iterates over the
     * children of the VBox,
     * and if a child is a TextField, it adds the TextField's ID as the key and the
     * TextField's text as the value to a HashMap.
     *
     * @param vbox the VBox to read the parameters from
     * @return a HashMap containing the parameters read from the VBox
     */
    public HashMap<String, Object> readParameters(final VBox vbox) {
        HashMap<String, Object> parameters = new HashMap<>();
        for (Node node : vbox.getChildren()) {
            if (node instanceof TextField) {
                TextField txt = (TextField) node;
                parameters.put(txt.getId(), txt.getText());
            }
        }
        return parameters;
    }
}
