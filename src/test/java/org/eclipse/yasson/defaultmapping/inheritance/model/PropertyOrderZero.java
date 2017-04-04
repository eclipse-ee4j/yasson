package org.eclipse.yasson.defaultmapping.inheritance.model;

/**
 * @author Roman Grigoriadi
 */
public class PropertyOrderZero {

    private String zeroOverriddenInSecond;

    //only setter is overridden
    private String zeroPartiallyOverriddenInFirst;

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

    public String getZeroPartiallyOverriddenInFirst() {
        return zeroPartiallyOverriddenInFirst;
    }

    public void setZeroPartiallyOverriddenInFirst(String zeroPartiallyOverriddenInFirst) {
        this.zeroPartiallyOverriddenInFirst = zeroPartiallyOverriddenInFirst;
    }
}
