package it.unibo.ares.gui;

import java.util.Set;

import it.unibo.ares.core.controller.SimulationInitializer;
import it.unibo.ares.core.utils.parameters.Parameter;
import javafx.scene.control.ChoiceBox;
import javafx.scene.layout.VBox;

/**
 * WriteOnGUI is an interface for writing parameters on a specific projected GUI.
 * It provides methods to write data to a ChoiceBox and a VBox.
 */
public interface WriteOnGUI {

    /**
     * Writes the specified list of strings to a ChoiceBox.
     *
     * @param choiceBox the ChoiceBox<String> to be written to
     * @param list the list of strings to be written to the ChoiceBox
     */
    void writeChoiceBox(ChoiceBox<String> choiceBox, Set<String> list);

    /**
     * Writes the specified map to a VBox.
     *
     * @param vBox        the VBox to be written to
     * @param parameters  the parameters to be written to the VBox
     * @param initializer the SimulationInitializer instance for setting the parameters
     */
    void writeVBox(VBox vBox, Set<Parameter<?>> parameters, SimulationInitializer initializer);
}
