package it.unibo.ares.core.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Implementation of the SimulationManager interface.
 * This class provides methods to save and load simulations.
 */
public class SimulationManagerImpl implements SimulationManager {

    private static final String DIRECTORY = "SavedSimulations/";

    /**
     * Generates a unique file name based on the current date and time.
     *
     * @return The generated file name.
     */
    private String getFileName() throws IOException {
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss");
        String formattedDate = now.format(formatter);

        // Ensure the directory exists
        File directory = new File(DIRECTORY);
        if (!directory.exists() && !directory.mkdirs()) {
            throw new IOException("Failed to create directory: " + DIRECTORY);
        }
        return DIRECTORY + "Simulation-" + formattedDate + ".out";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String save(final Simulation simulation) {
        try {
            String filePath = getFileName();
            try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filePath))) {
                oos.writeObject(simulation);
            }
            return filePath;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return "";
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Simulation load(final String filePath) {
        Simulation simulation = null;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
            simulation = (Simulation) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return simulation;
    }
}
