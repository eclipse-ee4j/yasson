package org.eclipse.yasson.defaultmapping.lambda;

/**
 * Class used to control serialization of lambda expression generated from functional interfaces with defaults.
 */
public class Cat implements Pet {

    private String name;

    Cat(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }
}
