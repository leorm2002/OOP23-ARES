package it.unibo.ares.gui;

import java.io.IOException;

import it.unibo.ares.core.controller.SimulationInitializer;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * GuiStarter is a class that extends the Application class from JavaFX.
 * It is the entry point for the JavaFX application.
 */
public class GuiStarter extends Application {

    /**
     * The start method is the main entry point for all JavaFX applications.
     * It is called after the init method has returned, and after the system is
     * ready for the application to begin running.
     *
     * @param window the primary stage for this application, onto which the
     *               application scene can be set.
     */
    @Override
    public void start(final Stage window) {
        Parent root;
        try {
            root = FXMLLoader.load(ClassLoader.getSystemResource("prova.fxml"));
            Scene scene = new Scene(root);
            window.setTitle("ARES");
            window.setScene(scene);
            window.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * The main method is used to launch the JavaFX application.
     *
     * @param args the command line arguments
     */
    public static void main(final String[] args) {
        launch(args);
    }
}
