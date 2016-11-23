package edu.asu.diging.gilesecosystem.util.store;

import java.util.function.Function;

import edu.asu.diging.gilesecosystem.util.exceptions.UnstorableObjectException;


public interface IDatabaseClient<T extends IStorableObject> {

    public abstract String generateId();

    public abstract String generateId(String prefix, Function<String, T> f);
    
    public abstract T store(T element) throws UnstorableObjectException;

    public abstract void delete(T element);

}