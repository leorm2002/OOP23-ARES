package it.unibo.ares.gui;

import it.unibo.ares.gui.controller.GuiStarter;
import javafx.application.Application;

/** Main application entry-point's class. */

public final class App {
    private App() {
    }

    /**
     * Main application entry-point.
     * 
     * @param args
     */
    public static void main(final String[] args) {
        Application.launch(GuiStarter.class, args);
    }

    /**
     * Main application entry-point.
     * 
     * @param args
     * @return a thread with the main application entry-point
     */
    public static Thread mainLib(final String[] args) {
        return new Thread(() -> {
            Application.launch(GuiStarter.class, args);
        });
    }
}
