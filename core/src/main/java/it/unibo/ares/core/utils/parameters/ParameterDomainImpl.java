package it.unibo.ares.core.utils.parameters;

import java.io.Serial;
import java.io.Serializable;
import java.util.function.Predicate;

import it.unibo.ares.core.utils.lambda.SerializablePredicate;

/**
 * Implementazione di un dominio di un parametro.
 * 
 * @param <T> Il tipo di valore del dominio
 */
public final class ParameterDomainImpl<T extends Serializable> implements ParameterDomain<T> {

    private static final long serialVersionUID = 1L;
    private final String description;
    private final SerializablePredicate<T> predicate;

    /**
     * Crea un nuovo dominio.
     * 
     * @param description la decrizione del dominio in linguaggio naturale
     * @param predicate   Il predicato per testare che un valore sia nel dominio
     */
    public ParameterDomainImpl(final String description, final SerializablePredicate<T> predicate) {
        this.description = description;
        this.predicate = predicate;
    }

    @Override
    public String getDescription() {
        return this.description;
    }

    @Override
    public boolean isValueValid(final T value) {
        return this.predicate.test(value);
    }

}
