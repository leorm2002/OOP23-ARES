package it.unibo.ares.cli;

/**
 * Implementation of the IOManager interface that provides methods for printing
 * messages and reading input via java console.
 */
@SuppressWarnings("PMD.SystemPrintln") // E UN PROGRAMMA CLI
public class IOManagerImpl implements IOManager {

    /**
     * Prints the specified message to the console.
     *
     * @param message the message to be printed
     */
    @Override
    public void print(final String message) {
        System.out.println(message);
    }

    /**
     * Reads a line of text from the console.
     *
     * @return the line of text read from the console
     */
    @Override
    public String read() {
        return System.console().readLine();
    }

    /**
     * Prints the specified message in the same line, without appending a new line
     * character.
     *
     * @param message the message to be printed
     */
    @Override
    public void printInLine(final String message) {
        System.out.print(message);
    }
}
