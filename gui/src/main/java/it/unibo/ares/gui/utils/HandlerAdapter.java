package it.unibo.ares.gui.utils;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;

/**
 * HandlerAdapter is a class that adapts a Runnable to an
 * EventHandler<ActionEvent>.
 */
public class HandlerAdapter implements EventHandler<ActionEvent> {
    private final Runnable method;

    /**
     * Constructor for the HandlerAdapter class.
     * 
     * @param method is a Runnable that will be adapted to an
     *               EventHandler<ActionEvent>
     */
    public HandlerAdapter(final Runnable method) {
        this.method = method;
    }

    /**
     * This method is called when the event is fired.
     * 
     * @param event is the event that is fired
     */
    @Override
    public void handle(final ActionEvent event) {
        method.run();
    }
}
