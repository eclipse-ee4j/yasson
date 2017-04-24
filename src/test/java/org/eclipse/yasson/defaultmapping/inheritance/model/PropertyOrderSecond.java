package org.eclipse.yasson.defaultmapping.inheritance.model;

/**
 * @author Roman Grigoriadi
 */
public class PropertyOrderSecond extends PropertyOrderFirst {

    private String zeroOverridden;

    private String second;

    public String getSecond() {
        return second;
    }

    public void setSecond(String second) {
        this.second = second;
    }

    @Override
    public String getZeroOverriddenInSecond() {
        return zeroOverridden;
    }

    @Override
    public void setZeroOverriddenInSecond(String zeroOverriddenInSecond) {
        this.zeroOverridden = zeroOverriddenInSecond;
    }

}
