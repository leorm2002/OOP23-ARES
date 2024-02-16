package it.unibo.ares.gui;

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
}
