package it.unibo.ares.gui.utils;

import it.unibo.ares.core.utils.parameters.Parameters;
import it.unibo.ares.core.utils.pos.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;

import java.util.Map;
import java.util.Set;

/**
 * WriteOnGUI is an interface for writing parameters on a specific projected
 * GUI.
 * It provides methods to manage a GUI.
 */
public interface GuiDinamicWriter {

    /**
     * Writes the specified list of strings to a ChoiceBox.
     *
     * @param choiceBox the ChoiceBox<String> to be written to
     * @param list      the list of strings to be written to the ChoiceBox
     * @param <T>       the type of the elements in the list
     */
    <T> void writeChoiceBox(ChoiceBox<T> choiceBox, Set<T> list);

    /**
     * Writes the specified Parameters to a VBox.
     *
     * @param vBox        the VBox to be written to
     * @param parameters  the parameters to be written to the VBox
     */
    void writeVBox(VBox vBox, Parameters parameters);

    /**
     * This method disables all the TextFields in the given VBox.
     * It iterates over the children of the VBox, and if the child is a TextField,
     * it disables it.
     *
     * @param vbox the VBox whose TextFields should be disabled
     */
    void disableVBox(VBox vbox);

    /**
     * Writes a 2D map of items to a specified container.
     *
     * @param items     the map of items to be written
     * @param container the container where the items should be written
     * @param width     the width of the container
     * @param height    the height of the container
     */
    void write2dMap(Map<Pos, String> items, AnchorPane container, int width, int height);

    /**
     * This method displays an error message in a dialog box and disables a
     * specified button.
     * It is typically used to handle exceptions or errors in the GUI, providing
     * feedback to the user.
     *
     * @param message The error message to be displayed in the dialog box.
     */
    void showError(String message);

    /**
     * This method disables the specified button.
     *
     * @param button the button to be disabled
     */
    void disableButton(Button button);

    /**
     * This method enables the specified button.
     *
     * @param button the button to be enabled
     */
    void enableButton(Button button);

    /**
     * This method cleans the specified VBox.
     *
     * @param vBox the VBox to be cleaned
     */
    void clearVBox(VBox vBox);

    /**
     * This method cleans the specified ChoiceBox.
     *
     * @param choiceBox the ChoiceBox to be cleaned
     */
    void disableChoiceBox(ChoiceBox<?> choiceBox);

    /**
     * This method enables the specified ChoiceBox.
     *
     * @param choiceBox the ChoiceBox to be enabled
     */
    void enableChoiceBox(ChoiceBox<?> choiceBox);
}
