package it.unibo.ares.utils;

import javax.annotation.concurrent.Immutable;

@Immutable
public class Pair<T, U> {
    private final T first;
    private final U second;

    public Pair(T first, U second){
        this.first = first;
        this.second = second;
    }

    public T getFirst(){
        return first;
    }

    public U getSecond(){
        return second;
    }

    @Override
    public String toString(){
        return "(" + first + ", " + second + ")";
    }

    @Override
    public boolean equals(Object o){
        if (o == this)
            return true;
        if (!(o instanceof Pair))
            return false;
        Pair<?, ?> p = (Pair<?, ?>) o;
        return p.first.equals(first) && p.second.equals(second);
    }

    @Override
    public int hashCode(){
        int result = 17;
        result = 31 * result + first.hashCode();
        result = 31 * result + second.hashCode();
        return result;
    }
}
