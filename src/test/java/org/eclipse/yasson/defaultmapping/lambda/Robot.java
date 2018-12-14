package org.eclipse.yasson.defaultmapping.lambda;

/**
 * Class used to control serialization of lambda expression generated from functional interfaces with no defaults.
 */
public class Robot implements Addressable {

    private String name;

    Robot(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return this.name;
    }
}
