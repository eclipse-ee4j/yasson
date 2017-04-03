package org.eclipse.yasson.defaultmapping.inheritance.model;

/**
 * @author Roman Grigoriadi
 */
public class PropertyOrderFirst extends PropertyOrderZero {

    private String zeroOverriddenInFirst;

    private String first;

    public String getFirst() {
        return first;
    }

    public void setFirst(String first) {
        this.first = first;
    }

    @Override
    public String getZeroOverriddenInFirst() {
        return zeroOverriddenInFirst;
    }

    @Override
    public void setZeroOverriddenInFirst(String zeroOverriddenInFirst) {
        this.zeroOverriddenInFirst = zeroOverriddenInFirst;
    }

}
