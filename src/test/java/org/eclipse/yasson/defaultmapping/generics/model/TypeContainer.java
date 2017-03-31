package org.eclipse.yasson.defaultmapping.generics.model;

public interface TypeContainer<T> {
    T getInstance();
    void setInstance(T instance);
}