package org.eclipse.yasson.defaultmapping.inheritance.model;

/**
 * @author Roman Grigoriadi
 */
public class PropertyOrderFirst extends PropertyOrderZero {

    private String first;

    public String getFirst() {
        return first;
    }

    public void setFirst(String first) {
        this.first = first;
    }

    @Override
    public void setZeroPartiallyOverriddenInFirst(String zeroPartiallyOverriddenInFirst) {
        super.setZeroPartiallyOverriddenInFirst(zeroPartiallyOverriddenInFirst);
    }

}
