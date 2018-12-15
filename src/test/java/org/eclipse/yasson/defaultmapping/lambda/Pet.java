package org.eclipse.yasson.defaultmapping.lambda;

/**
 * A functional interface with one default property.
 */
public interface Pet extends Addressable {

    /**
     * Public readable property with default value.
     *
     * @return pet's age in months
     */
    default int getAge() {
        return 0;
    }
}
