package it.unibo.ares.cli;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Classe per leggere in maniera non bloccante da terminale.
 */
public class AsyncReader implements Runnable {
    private final Consumer<String> consumer;
    private final Supplier<Boolean> exitConition;

    /**
     * Creas un reader.
     * 
     * @param consumer     il consumer che deve accettare il valore una volta che
     *                     quest'ultimo è stato letto.
     * @param exitConition il supplier che ritoni se dobbiamo terminare
     */
    public AsyncReader(final Consumer<String> consumer, final Supplier<Boolean> exitConition) {
        this.consumer = consumer;
        this.exitConition = exitConition;
    }

    /**
     * Fa partire il processo di lettura.
     */
    @Override
    public void run() {
        String readed = null;
        while (Boolean.FALSE.equals(exitConition.get())) {
            readed = System.console().readLine();
            consumer.accept(readed);
        }
    }

}
