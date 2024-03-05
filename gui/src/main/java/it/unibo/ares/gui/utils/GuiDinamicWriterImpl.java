package it.unibo.ares.gui.utils;

import it.unibo.ares.core.utils.parameters.Parameters;
import it.unibo.ares.core.utils.parameters.Parameter;
import it.unibo.ares.core.utils.pos.Pos;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import java.util.Collection;

import java.util.Map;

/**
 * WriteOnGUIImpl is a class that implements the WriteOnGUI interface.
 * It provides methods to manage a GUI that needs to write and read
 * parameters and write agents to a 2d map.
 */
public class GuiDinamicWriterImpl implements GuiDinamicWriter {

    /**
     * The writeVBox method writes parameters to a VBox. For each parameter, 
     * it creates a new TextField with the parameter's name
     * as the ID and the parameter's value as the text,
     * and adds the TextField to the VBox's children.
     *
     * @param vbox       the VBox to write the parameters to
     * @param parameters the Parameters to write to the VBox
     */
    @Override
    public void writeVBox(final VBox vbox, final Parameters parameters) {
        // setting the default style of the textfields and labels
        final int txtSize = 13, lblSize = 15, domainSize = 13, marginBottom = 20, marginRightLeft = 10;
        /*
         * creating a label and a textfield for each parameter and setting his style,
         * then adding them to the vbox and if the parameter has a domain, adding a
         * label
         * with the domain description
         */
        parameters.getParameters().stream().filter(Parameter::userSettable).forEach(p -> {
            Label lbl = new Label(p.getKey());
            Label domain = new Label();
            TextField txt = new TextField();
            txt.setId(p.getKey());
            VBox.setMargin(domain, new Insets(0, marginRightLeft, marginBottom, marginRightLeft));
            setElementStyle(domain, domainSize);
            setElementStyle(txt, txtSize);
            setElementStyle(lbl, lblSize);
            if (p.getDomain().isPresent()) {
                domain.setText(p.getDomain().get().getDescription());
            }
            /*
             * setting the text of the textfield with the value of the parameter, if the
             * value is not present, setting the prompt text with the type of the parameter
             */
            try {
                txt.setText(p.getValue().toString());
            } catch (Exception e) {
                txt.setText("");
                txt.setPromptText(p.getType().getSimpleName());
            }
            vbox.getChildren().addAll(lbl, txt, domain);
        });
    }

    /**
     * This method sets the style of the given Node to the given size.
     *
     * @param node the Node whose style should be set
     * @param size the size to set the style to
     * @param <T>  the type of the Node
     */
    private <T extends Node> void setElementStyle(final T node, final int size) {
        node.setStyle("-fx-font-size: " + size + ";");
    }

    /**
     * This method writes a collection to a ChoiceBox.
     * It first clears the ChoiceBox, then adds all items from the set to the
     * ChoiceBox.
     *
     * @param choiceBox the ChoiceBox to which the items should be written
     * @param set       the collection of items to be written to the ChoiceBox
     * @param <T>       the type of the items in the collection and the ChoiceBox
     */
    @Override
    public <T> void writeChoiceBox(final ChoiceBox<T> choiceBox, final Collection<T> collection) {
        choiceBox.getItems().clear();
        choiceBox.getItems().addAll(collection);
    }

    /**
     * This method writes a map of positions and its relatives strings to a 2D map
     * represented by a
     * GridPane.
     * It first clears the container that will contain the new GridPane, then
     * iterates over the data in the
     * given Map.
     * For each data entry, it creates a new label, sets its text to the agent's
     * name, and adds it to the GridPane at the position specified by the data
     * entry.
     *
     * @param items     The Map containing the data to be
     *                  written
     *                  to the map.
     * @param container The container that will contain the 2D map.
     * @param width     The width of the map.
     * @param height    The height of the map.
     */
    @Override
    public void write2dMap(final Map<Pos, String> items, final Pane container, final int width,
            final int height) {
        final int maxSizeGrid = 655, prefLblSize = 40;
        container.getChildren().clear();
        GridPane grid = new GridPane();
        grid.setMaxSize(maxSizeGrid, maxSizeGrid);
        /*
         * creating a label (cell) for each position in the map and setting his min and max
         * size
         */
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                Label lbl = new Label();
                lbl.setPrefSize(prefLblSize, prefLblSize);
                grid.add(lbl, i, j);
            }
        }
        /*
         * setting the text of the textfield for each agent in the map
         * (occupied positions)
         */
        items.forEach((pos, agent) -> {
            Label txt = new Label(agent);
            setElementStyle(txt, prefLblSize/2);
            grid.add(txt, pos.getX(), pos.getY());
        });
        grid.setGridLinesVisible(true);
        container.getChildren().add(grid);
    }

    /**
     * This method displays an error message in a dialog box.
     * It is typically used to handle exceptions or errors in the GUI, providing
     * feedback to the user and preventing further actions until the error is
     * resolved.
     *
     * @param message The error message to be displayed in the dialog box.
     */
    @Override
    public void showError(final String message) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Errore");
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * This method disables the given element if it is not already disabled.
     *
     * @param elem the Node to be disabled
     */
    @Override
    public void disableElement(final Node element) {
        if (!element.isDisable()) {
            element.setDisable(true);
        }
    }

    /**
     * This method enables the given Node if it is not already enabled.
     *
     * @param element the Node to be enabled
     */
    @Override
    public void enableElement(final Node element) {
        if (element.isDisable()) {
            element.setDisable(false);
        }
    }

    /**
     * This method clears all the children of the given VBox.
     *
     * @param vBox the VBox instance to be cleared
     */
    @Override
    public void clearVBox(VBox element) {
        element.getChildren().clear();
    }
}
