package it.unibo.ares.cli;

/**
 * The IOManager interface provides methods for input/output operations.
 */
public interface IOManager {

    /**
     * Prints the specified message to the output.
     *
     * @param message the message to be printed
     */
    void print(String message);

    /**
     * Prints the specified message to the output without a newline character.
     *
     * @param message the message to be printed
     */
    void printInLine(String message);

    /**
     * Reads a line of input from the user.
     *
     * @return the input line as a string
     */
    String read();

}
