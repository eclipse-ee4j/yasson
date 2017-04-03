package org.eclipse.yasson.defaultmapping.inheritance.model;

/**
 * @author Roman Grigoriadi
 */
public class PropertyOrderZero {

    private String zeroOverriddenInSecond;

    private String zeroOverriddenInFirst;

    private String zero;

    public String getZero() {
        return zero;
    }

    public void setZero(String zero) {
        this.zero = zero;
    }

    public String getZeroOverriddenInSecond() {
        return zeroOverriddenInSecond;
    }

    public void setZeroOverriddenInSecond(String zeroOverriddenInSecond) {
        this.zeroOverriddenInSecond = zeroOverriddenInSecond;
    }

    public String getZeroOverriddenInFirst() {
        return zeroOverriddenInFirst;
    }

    public void setZeroOverriddenInFirst(String zeroOverriddenInFirst) {
        this.zeroOverriddenInFirst = zeroOverriddenInFirst;
    }
}
