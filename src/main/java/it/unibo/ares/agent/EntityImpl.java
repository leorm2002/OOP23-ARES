package it.unibo.ares.agent;

public class EntityImpl implements Entity {
    private String name;
    @Override
    public String getName() {
        if(this.name == null) {
            throw new IllegalStateException("Name not set");
        }
        return name;
    }

    @Override
    public void setName(String name) {
        if (name == null) {
            throw new IllegalArgumentException("Name cannot be null");
        }
        this.name = name;
    }

}
