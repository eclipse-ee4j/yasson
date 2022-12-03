package org.eclipse.yasson.defaultmapping.generics.model;

import java.util.Collection;

public class LowerBoundTypeVariableWithCollectionAttributeClass<T extends Shape> {
    
    private Collection<AnotherGenericTestClass<Integer, T>> value;

    public Collection<AnotherGenericTestClass<Integer, T>> getValue() {
        return value;
    }

    public void setValue(Collection<AnotherGenericTestClass<Integer, T>> value) {
        this.value = value;
    }
    
}
