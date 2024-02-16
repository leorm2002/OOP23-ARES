package it.unibo.ares.gui;

import java.util.HashMap;

import it.unibo.ares.core.utils.pos.Pos;
import javafx.scene.layout.GridPane;

/**
 * Write2dMap is an interface that provides methods to write a 2D map to a GUI.
 */
public interface Write2dMap {
    /**
     * The writeMap method writes a 2D map to the GUI.
     * It takes a 2D array representing the map and updates the GUI accordingly.
     *
     * @param positions an hashmap of positions and their respective strings (name of the agent)
     * @param rows      the number of rows of the map
     * @param columns   the number of columns of the map
     * @return the GridPane representing the 2D map
     */
    GridPane write2dMap(HashMap<Pos, String> positions, int rows, int columns);
}
