package it.unibo.ares.core.controller;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class SimulationManagerImpl implements SimulationManager {

    private String getFileName() {
        LocalDateTime now = LocalDateTime.now();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss");

        String formattedDate = now.format(formatter);

        return "SavedSimulations/" + "Simulation-" + formattedDate + ".out";
    }

    @Override
    public String save(Simulation simulation) {
        String path = getFileName();
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream("output.data"))) {
            oos.writeObject(simulation);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return path;
    }

    @Override
    public Simulation load(String filePath) {
        Simulation simulation = null;
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filePath))) {
            simulation = (Simulation) ois.readObject();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return simulation;
    }
}
