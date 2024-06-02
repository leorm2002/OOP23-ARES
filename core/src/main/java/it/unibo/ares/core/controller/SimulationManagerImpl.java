package it.unibo.ares.core.controller;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;


public class SimulationManagerImpl implements SimulationManager {

    @Override
    public String save(Simulation simulation) {
        String path = "output.data";
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
    public void load() {
    }
}
