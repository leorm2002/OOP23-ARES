package it.unibo.ares.gui.utils;

import it.unibo.ares.core.utils.parameters.Parameters;
import it.unibo.ares.core.utils.pos.Pos;
import javafx.scene.Node;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import java.util.Collection;

import java.util.Map;

/**
 * WriteOnGUI is an interface for writing parameters on a specific projected
 * GUI.
 * It provides methods to manage a GUI.
 */
public interface GuiDinamicWriter {

    /**
     * Writes the specified collection of T to a ChoiceBox.
     *
     * @param choiceBox the ChoiceBox<T> to be written to
     * @param list      the collection of T to be written to the ChoiceBox
     * @param <T>       the type of the elements in the list
     */
    <T> void writeChoiceBox(ChoiceBox<T> choiceBox, Collection<T> list);

    /**
     * This method disables the specified element.
     *
     * @param element the element to be disabled
     */
    void disableElement(Node element);

    /**
     * This method enables the specified element.
     *
     * @param element the element to be enabled
     */
    void enableElement(Node element);
    /**
     * Writes the specified Parameters to a VBox.
     *
     * @param vBox        the VBox to be written to
     * @param parameters  the parameters to be written to the VBox
     */
    void writeVBox(VBox vBox, Parameters parameters);

    /**
     * Writes a 2D map of items to a specified container.
     *
     * @param items     the map of items to be written
     * @param container the Pane that will contain the 2D map
     * @param width     the width of the container
     * @param height    the height of the container
     */
    void write2dMap(Map<Pos, String> items, Pane container, int width, int height);

    /**
     * This method displays an error message in a dialog box
     * It is typically used to handle exceptions or errors in the GUI, providing
     * feedback to the user.
     *
     * @param message The error message to be displayed in the dialog box.
     */
    void showError(String message);

    /**
     * This method displays an alert message in a dialog box
     * It is typically used to show notifications in the GUI, providing
     * feedback to the user.
     *
     * @param message The error message to be displayed in the dialog box.
     */
    void showAlert(String message);

    /**
     * This method cleans the specified element.
     *
     * @param element the VBox to be cleaned
     */
    void clearVBox(VBox element);
}
