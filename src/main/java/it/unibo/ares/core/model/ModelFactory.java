package it.unibo.ares.core.model;

/**
 * Represents a factory for creating models.
 */
public interface ModelFactory {
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
