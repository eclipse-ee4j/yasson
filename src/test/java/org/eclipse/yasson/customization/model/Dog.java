package org.eclipse.yasson.customization.model;

public class Dog implements Animal {

    private String dogProperty;

    public Dog() {
    }

    public Dog(String dogProperty) {
        this.dogProperty = dogProperty;
    }

    public String getDogProperty() {
        return dogProperty;
    }

    public void setDogProperty(String dogProperty) {
        this.dogProperty = dogProperty;
    }
}
