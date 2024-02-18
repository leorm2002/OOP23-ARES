package it.unibo.ares.cli;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import it.unibo.ares.cli.utils.StringCaster;
import it.unibo.ares.core.controller.CalculatorSupplier;
import it.unibo.ares.core.utils.Pair;
import it.unibo.ares.core.utils.parameters.Parameter;
import it.unibo.ares.core.utils.parameters.Parameters;

public class CliInitializer {
    private String initializationId;

    private String selectModel() {
        Optional<String> selected = Optional.empty();
        System.out.println("Scegli un modello:");
        var models = CalculatorSupplier.getInstance().getModels().stream()
                .collect(Collectors.toList());
        List<Pair<Integer, String>> indexedModels = IntStream.range(0, models.size())
                .mapToObj(i -> new Pair<>(i, models.get(i)))
                .toList();
        int index = -1;
        do {
            System.out.println("Scegli un modello inserendo il numero associato e premendo invio:");
            indexedModels.forEach(p -> System.out.println(p.getFirst() + " - " + p.getSecond()));
            try {
                index = Integer.parseInt(System.console().readLine());
                if (index < 0 || index >= indexedModels.size()) {
                    System.out.println("Inserisci un numero valido");
                } else {
                    selected = Optional.of(models.get(index));
                    System.out.println("Hai selezionato il modello " + selected.get());
                }
            } catch (NumberFormatException e) {
                System.out.println("Inserisci un numero valido");
            }

        } while (!selected.isPresent());
        return selected.get();
    }

    private void parametrizzatoreGenerico(Parameters params, Optional<String> agentId) {
        for (Parameter<?> param : params.getParametersToset()) {
            System.out.println("\nInserisci il valore per il parametro " + param.getKey());
            param.getDomain().ifPresent(d -> System.out.println("Il parametro ha dominio: " + d.getDescription()));
            System.out.println("Il parametro ha tipo " + param.getType().getSimpleName());
            System.out.print("Inserisci il valore:");
            String value = System.console().readLine();
            try {
                if (agentId.isPresent()) {
                    CalculatorSupplier.getInstance()
                            .setAgentParameterSimplified(initializationId, agentId.get(), param.getKey(),
                                    value);
                } else {
                    CalculatorSupplier.getInstance()
                            .setModelParameter(initializationId, param.getKey(), value);
                }
            } catch (IllegalArgumentException e) {
                System.out.println("*********** Parametro non valido ***********");

                System.out.println(e.getMessage());
                System.out.println("*********** Parametro non valido ***********\n");

            }
        }
    }

    private void parametrizzaModello() {
        System.out.println("Parametrizzazione modello");
        Parameters params;
        do {

            params = CalculatorSupplier.getInstance()
                    .getModelParametersParameters(initializationId);
            parametrizzatoreGenerico(params, Optional.empty());
        } while (!params.areAllParametersSetted());
    }

    private void parametrizzaAgente(String agent) {
        System.out.println("Parametrizzazione agente " + agent);
        Parameters params;
        do {
            params = CalculatorSupplier.getInstance().getAgentParametersSimplified(initializationId,
                    agent);
            parametrizzatoreGenerico(params, Optional.of(agent));
        } while (!params.areAllParametersSetted());
    }

    private void parametrizzaAgenti(Set<String> agents) {
        System.out.println("Parametrizzazione agenti");
        do {
            for (String agent : agents) {
                parametrizzaAgente(agent);
            }
        } while (!areAgentiParametrizzati(agents));
    }

    private boolean areAgentiParametrizzati(Set<String> agents) {
        for (String agent : agents) {
            if (!CalculatorSupplier.getInstance().getAgentParametersSimplified(initializationId, agent)
                    .areAllParametersSetted()) {
                return false;
            }
        }
        return true;
    }

    public String startParametrization() {
        System.out.println("Inizio parametrizzazione");
        String model = selectModel();
        this.initializationId = CalculatorSupplier.getInstance().setModel(model);
        parametrizzaModello();
        Set<String> agents = CalculatorSupplier.getInstance().getAgentsSimplified(initializationId);

        parametrizzaAgenti(agents);

        System.out.println("Fine parametrizzazione");

        return initializationId;
    }
}