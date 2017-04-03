package org.eclipse.yasson.defaultmapping.inheritance.model;

/**
 * @author Roman Grigoriadi
 */
public class PropertyOrderSecond extends PropertyOrderFirst {

    private String zeroOverriden;

    private String second;

    public String getSecond() {
        return second;
    }

    public void setSecond(String second) {
        this.second = second;
    }

    @Override
    public String getZeroOverriddenInSecond() {
        return zeroOverriden;
    }

    @Override
    public void setZeroOverriddenInSecond(String zeroOverriddenInSecond) {
        this.zeroOverriden = zeroOverriddenInSecond;
    }

}
