package it.unibo.ares.cli;

import it.unibo.ares.core.controller.CalculatorSupplier;
import it.unibo.ares.core.utils.Pair;
import it.unibo.ares.core.utils.StringCaster;
import it.unibo.ares.core.utils.parameters.Parameter;
import it.unibo.ares.core.utils.parameters.Parameters;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Class used to initialize simulation by using a CLI.
 */
public final class CliInitializer {
    private String initializationId;
    private final IOManager ioManager;

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
        final List<String> models = CalculatorSupplier.getInstance().getModels().stream()
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
                            CalculatorSupplier.getInstance()
                                    .setAgentParameterSimplified(initializationId, agentId.get(), param.getKey(),
                                            StringCaster.cast(value, param.getType()));
                        } else {
                            CalculatorSupplier.getInstance()
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

            params = CalculatorSupplier.getInstance()
                    .getModelParametersParameters(initializationId);
            parametrizzatoreGenerico(params, Optional.empty());
        } while (!params.areAllParametersSetted());
    }

    private void parametrizzaAgente(final String agent) {
        ioManager.print("Parametrizzazione agente " + agent);
        Parameters params;
        do {
            params = CalculatorSupplier.getInstance().getAgentParametersSimplified(initializationId,
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
            if (!CalculatorSupplier.getInstance().getAgentParametersSimplified(initializationId, agent)
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
        this.initializationId = CalculatorSupplier.getInstance().addNewModel(model);
        parametrizzaModello();
        final Set<String> agents = CalculatorSupplier.getInstance().getAgentsSimplified(initializationId);

        parametrizzaAgenti(agents);

        ioManager.print("Fine parametrizzazione");

        return initializationId;
    }
}
