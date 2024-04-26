package it.unibo.ares.core.agent;

/**
 * Implementation of the {@link Entity} interface.
 * Represents an entity with a name.
 */
final class EntityImpl implements Entity {
    private String name;

    /*
     * {@inheritDoc}
     */
    @Override
    public String getName() {
        if (this.name == null) {
            throw new IllegalStateException("Name not set");
        }
        return name;
    }

    /*
     * {@inheritDoc}
     */
    @Override
    public void setName(final String name) {
        if (name == null) {
            throw new IllegalArgumentException("Name cannot be null");
        }
        this.name = name;
    }

}
