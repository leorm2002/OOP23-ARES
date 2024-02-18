package it.unibo.ares.core.utils.parameters;

import java.util.function.Predicate;

public class ParamterDomainImpl<T> implements ParameterDomain<T> {
    private final String description;
    private final Predicate<T> predicate;

    public ParamterDomainImpl(final String description, final Predicate<T> predicate) {
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
