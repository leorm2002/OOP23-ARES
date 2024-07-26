package it.unibo.ares.cli;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import it.unibo.ares.core.controller.AresSupplier;
import it.unibo.ares.core.utils.Pair;
import it.unibo.ares.core.utils.StringCaster;
import it.unibo.ares.core.utils.parameters.Parameter;
import it.unibo.ares.core.utils.parameters.Parameters;

/**
 * Class used to initialize simulation by using a CLI.
 */
public final class CliInitializer {
    private String initializationId;
    private final IOManager ioManager;
    private static String folderPath = "ParametersData/";

    /**
     * Creates a new CliInitializer object.
     * 
     * @param ioManager the IOManager object to use for input/output
     */
    public CliInitializer(final IOManager ioManager) {
        this.ioManager = ioManager;
    }

    private String selectModel() {
        Optional<String> selected = Optional.empty();
        ioManager.print("Scegli un modello:");
        final List<String> models = AresSupplier.getInstance().getModels().stream()
                .collect(Collectors.toList());

        final List<Pair<Integer, String>> indexedModels = IntStream.range(0, models.size())
                .mapToObj(i -> new Pair<>(i, models.get(i)))
                .toList();
        int index;
        do {
            ioManager.print("Scegli un modello inserendo il numero associato e premendo invio:");
            indexedModels.forEach(p -> ioManager.print(p.getFirst() + " - " + p.getSecond()));
            try {
                index = Integer.parseInt(ioManager.read());
                if (index < 0 || index >= indexedModels.size()) {
                    ioManager.print("Inserisci un numero valido");
                } else {
                    selected = Optional.of(models.get(index));
                    ioManager.print("Hai selezionato il modello " + selected.get());
                }
            } catch (NumberFormatException e) {
                ioManager.print("Inserisci un numero valido");
            }

        } while (!selected.isPresent());
        return selected.get();
    }

    private void parametrizzatoreGenerico(final Parameters params, final Optional<String> agentId) {
        params.getParametersToset().stream()
                .filter(Parameter::userSettable)
                .sorted(Comparator.comparing(p -> ((Parameter<?>) p).getKey()))
                .forEachOrdered(param -> {
                    ioManager.print("\nInserisci il valore per il parametro " + param.getKey());
                    param.getDomain()
                            .ifPresent(d -> ioManager.print("Il parametro ha dominio: " + d.getDescription()));
                    ioManager.print("Il parametro ha tipo " + param.getType().getSimpleName());
                    ioManager.printInLine("Inserisci il valore:");
                    final String value = ioManager.read();
                    try {
                        if (agentId.isPresent()) {
                            AresSupplier.getInstance()
                                    .setAgentParameterSimplified(initializationId, agentId.get(), param.getKey(),
                                            StringCaster.cast(value, param.getType()));
                        } else {
                            AresSupplier.getInstance()
                                    .setModelParameter(initializationId, param.getKey(),
                                            StringCaster.cast(value, param.getType()));
                        }
                    } catch (IllegalArgumentException e) {
                        ioManager.print("*********** Parametro non valido ***********");

                        ioManager.print(e.getMessage());
                        ioManager.print("*********** Parametro non valido ***********\n");

                    }
                });
    }

    private void parametrizzaModello() {
        ioManager.print("Parametrizzazione modello");
        Parameters params;
        do {

            params = AresSupplier.getInstance()
                    .getModelParametersParameters(initializationId);
            parametrizzatoreGenerico(params, Optional.empty());
        } while (!params.areAllParametersSetted());
    }

    private void parametrizzaAgente(final String agent) {
        ioManager.print("Parametrizzazione agente " + agent);
        Parameters params;
        do {
            params = AresSupplier.getInstance().getAgentParametersSimplified(initializationId,
                    agent);
            parametrizzatoreGenerico(params, Optional.of(agent));
        } while (!params.areAllParametersSetted());
    }

    private void parametrizzaAgenti(final Set<String> agents) {
        ioManager.print("Parametrizzazione agenti");
        do {
            for (final String agent : agents) {
                parametrizzaAgente(agent);
            }
        } while (!areAgentiParametrizzati(agents));
    }

    private boolean areAgentiParametrizzati(final Set<String> agents) {
        for (final String agent : agents) {
            if (!AresSupplier.getInstance().getAgentParametersSimplified(initializationId, agent)
                    .areAllParametersSetted()) {
                return false;
            }
        }
        return true;
    }

    /**
     * Questo metodo è l'entry point per il processo di parametrizzazione.
     * 
     * @return ritorna l'i'd con cui accedere alla simulazione
     */
    public String startParametrization() {
        ioManager.print("Inizio parametrizzazione");
        final String model = selectModel();
        this.initializationId = AresSupplier.getInstance().addNewModel(model);
        parametrizzaModello();
        final Set<String> agents = AresSupplier.getInstance().getAgentsSimplified(initializationId);

        parametrizzaAgenti(agents);

        ioManager.print("Fine parametrizzazione");

        return initializationId;
    }

    /**
     * Esporta i dati dei parametri del modello e degli agenti in un file di testo
     * formattato.
     * 
     * @throws IOException se si verifica un errore durante la creazione delle
     *                     directory o la scrittura del file.
     */
    public void exportParametersData() {
        final Set<String> agents = AresSupplier.getInstance().getAgentsSimplified(initializationId);
        final Parameters modelParams = AresSupplier.getInstance().getInitializedModelParameters(initializationId);

        if (agents == null || modelParams == null) {
            ioManager.print("Errore: parametri del modello o agenti non inizializzati correttamente.");
            return;
        }

        final String filePath = folderPath + initializationId + ".txt";
        final Path path = Paths.get(filePath);
        final Path parentPath = path.getParent();

        if (parentPath != null) {
            try {
                Files.createDirectories(parentPath);
            } catch (IOException e) {
                ioManager.print("Errore nella creazione delle directory: " + e.getMessage());
                return;
            }
        }

        final StringBuilder dataBuilder = new StringBuilder(128);

        dataBuilder.append("Parametri del Modello:\n");
        modelParams
                .getParameters()
                .stream()
                .filter(Parameter::userSettable)
                .forEach(
                        p -> dataBuilder.append('\t').append(p.getKey()).append(": ").append(p.getValue())
                                .append('\n'));

        dataBuilder.append("Parametri degli Agenti:\n");
        agents.forEach(agent -> {
            dataBuilder.append('\t').append(agent).append(":\n");
            AresSupplier.getInstance()
                    .getAgentParametersSimplified(initializationId, agent)
                    .getParameters()
                    .stream()
                    .filter(Parameter::userSettable)
                    .forEach(p -> dataBuilder.append("\t\t").append(p.getKey()).append(": ").append(p.getValue())
                            .append('\n'));
        });

        try (FileWriter fileWriter = new FileWriter(filePath, StandardCharsets.UTF_8)) {
            fileWriter.write(dataBuilder.toString());
            ioManager.print("Esportazione dei parametri avvenuta con successo.");
            ioManager.print("File salvato in: " + filePath);
        } catch (IOException e) {
            ioManager.print("Errore nell'esportazione dei parametri: " + e.getMessage());
        }
    }
}
