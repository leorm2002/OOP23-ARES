package it.unibo.ares.core.controller;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import it.unibo.ares.core.model.ModelBuilderImpl;
import it.unibo.ares.core.model.ModelBuilder;

public class SimulationManagerImpl implements SimulationManager {

    @Override
    public String save(Simulation simulation) {
        String path = "object.data";
        Kryo kryo = new Kryo();
        kryo.register(Simulation.class);
        kryo.register(SimulationImpl.class);
        kryo.register(ModelBuilder.class);
        kryo.register(ModelBuilderImpl.class);

        Output output;
        try {
            output = new Output(new FileOutputStream(path));
            kryo.writeObject(output, simulation);
            output.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return path;
    }

    @Override
    public void load() {
    }
}
