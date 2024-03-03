package it.unibo.ares.core.agent;

/**
 * Represents an entity in the system. An entity is an object tha can only be subject of actions.
 */
public interface Entity {
    /**
     * Gets the name of the entity.
     * 
     * @return the name of the entity
     */
    String getName();

    /**
     * Sets the name of the entity.
     * 
     * @param name the name to set
     */
    void setName(String name);
}
