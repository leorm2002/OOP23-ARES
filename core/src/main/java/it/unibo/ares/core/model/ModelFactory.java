package it.unibo.ares.core.model;

import java.io.Serializable;

/**
 * Represents a factory for creating models.
 */
public interface ModelFactory extends Serializable {
    /**
     * Retrieves the id of the model.
     * 
     * @return the id of the model.
     */
    String getModelId();

    /**
     * Creates a new model.
     *
     * @return the new model.
     */
    Model getModel();
}
