package it.unibo.ares.cli;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Classe per leggere in maniera non bloccante da terminale.
 */
public class AsyncReader implements Runnable {
    private final Consumer<String> consumer;
    private final Supplier<Boolean> exitConition;
    private final IOManager ioManager;

    /**
     * Creas un reader.
     * 
     * @param consumer     il consumer che deve accettare il valore una volta che
     *                     quest'ultimo è stato letto.
     * @param exitConition il supplier che ritoni se dobbiamo terminare
     * @param ioManager    l'ioManager da usare per leggere.
     */
    public AsyncReader(final Consumer<String> consumer, final Supplier<Boolean> exitConition,
            final IOManager ioManager) {
        this.consumer = consumer;
        this.exitConition = exitConition;
        this.ioManager = ioManager;
    }

    /**
     * Fa partire il processo di lettura.
     */
    @Override
    public void run() {
        String readed;
        while (Boolean.FALSE.equals(exitConition.get())) {
            readed = ioManager.read();
            consumer.accept(readed);
        }
    }

}
