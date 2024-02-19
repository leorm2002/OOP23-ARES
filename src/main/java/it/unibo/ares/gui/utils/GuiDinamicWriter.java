package it.unibo.ares.gui.utils;

import java.util.Set;
import java.util.Map;

import it.unibo.ares.core.utils.parameters.Parameter;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import it.unibo.ares.core.utils.pos.Pos;

/**
 * WriteOnGUI is an interface for writing parameters on a specific projected
 * GUI.
 * It provides methods to write data to a ChoiceBox and a VBox.
 */
public interface GuiDinamicWriter {

    /**
     * Writes the specified list of strings to a ChoiceBox.
     *
     * @param choiceBox the ChoiceBox<String> to be written to
     * @param list      the list of strings to be written to the ChoiceBox
     */
    void writeChoiceBox(ChoiceBox<String> choiceBox, Set<String> list);

    /**
     * Writes the specified map to a VBox.
     *
     * @param vBox        the VBox to be written to
     * @param parameters  the parameters to be written to the VBox
     */
    void writeVBox(VBox vBox, Set<Parameter<?>> parameters);

    /**
     * This method disables all the TextFields in the given VBox.
     * It iterates over the children of the VBox, and if the child is a TextField,
     * it disables it.
     *
     * @param vbox the VBox whose TextFields should be disabled
     */
    void disableVBox(VBox vbox);

    /**
     * This method writes the simulation output data to a 2D map represented by a
     * GridPane.
     * The specific implementation of how the data is written to the map is not
     * provided here.
     *
     * @param items the map containing the positions and the agents to be written 
     * to the map
     * @param container the GridPane representing the 2D map
     */
    void write2dMap(Map<Pos, String> items, GridPane container);

    /**
     * This method displays an error message in a dialog box and disables a
     * specified button.
     * It is typically used to handle exceptions or errors in the GUI, providing
     * feedback to the user and preventing further actions until the error is
     * resolved.
     *
     * @param message The error message to be displayed in the dialog box.
     * @param btn     The button to be disabled.
     */
    void showErrorAndDisable(String message, Button btn);
}
