package it.unibo.ares.cli;

import java.io.IOException;
import java.io.Reader;
import java.util.function.Consumer;

public class AsyncReader implements Runnable {
    Reader r;
    private final Consumer<String> consumer;
    private String readed;

    public AsyncReader(Consumer<String> consumer) {
        this.consumer = consumer;
    }

    @Override
    public void run() {
        String readed = null;
        while (true) {
            readed = System.console().readLine();

            consumer.accept(readed);
        }
    }

}
