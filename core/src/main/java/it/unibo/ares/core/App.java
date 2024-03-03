package it.unibo.ares.core;



/**
 * Main class, should not be instantiated.
 */
public final class App {
    /**
     * Main method, should not be called.
     * @param args
     */
    public static void main(final String[] args) {
    }

    private App() {
        throw new IllegalStateException("Utility class");
    }
}
