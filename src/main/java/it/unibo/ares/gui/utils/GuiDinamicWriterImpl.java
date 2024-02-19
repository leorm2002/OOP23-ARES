package it.unibo.ares.gui.utils;

import java.util.Set;
import java.util.Map;

import it.unibo.ares.core.utils.parameters.Parameter;
import javafx.geometry.Insets;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import it.unibo.ares.core.utils.pos.Pos;

/**
 * WriteOnGUIImpl is a class that implements the WriteOnGUI interface.
 * It provides methods to write data to a VBox and a ChoiceBox.
 */
public class GuiDinamicWriterImpl implements GuiDinamicWriter {

    /**
     * The writeVBox method writes a set of parameters to a VBox. It clears the
     * VBox's children,
     * then for each parameter, it creates a new TextField with the parameter's name
     * as the ID and the parameter's value as the text,
     * and adds the TextField to the VBox's children.
     *
     * @param vbox        the VBox to write the parameters to
     * @param parameters  a Set containing the parameters to write to the VBox
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

    /**
     * This method disables all the TextFields in the given VBox.
     * It iterates over the children of the VBox, and if the child is a TextField,
     * it disables it.
     *
     * @param vbox the VBox whose TextFields should be disabled
     */
    @Override
    public void disableVBox(final VBox vbox) {
        for (javafx.scene.Node node : vbox.getChildren()) {
            if (node instanceof TextField) {
                TextField textField = (TextField) node;
                textField.setDisable(true);
            }
        }
    }

    /**
     * This method writes the simulation output data to a 2D map represented by a
     * GridPane.
     * It first clears the GridPane, then iterates over the data in the
     * SimulationOutputData object.
     * For each data entry, it creates a new TextField, sets its text to the agent's
     * name, and adds it to the GridPane at the position specified by the data
     * entry.
     *
     * @param item The SimulationOutputData object containing the data to be written
     *             to the map.
     * @param container The GridPane representing the 2D map.
     */
    @Override
    public void write2dMap(final Map<Pos, String> items, final GridPane container) {
        int lblFontSize = 20;
        container.getChildren().clear();
        items.forEach((pos, agent) -> {
            Label txt = new Label(agent);
            txt.setFont(Font.font(lblFontSize));
            container.add(txt, pos.getX(), pos.getY());
        });
        container.setGridLinesVisible(true);
    }


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
    @Override
    public void showErrorAndDisable(final String message, final Button btn) {
        Alert alert = new Alert(AlertType.ERROR);
        alert.setTitle("Errore");
        alert.setContentText(message);
        alert.showAndWait();
        btn.setDisable(true);
    }
}
