package org.eclipse.yasson.defaultmapping.inheritance.model;

/**
 * Bias property is not readable by putting field and getter in other class than setter.
 *
 * @author Roman Grigoriadi
 */
public class PartialOverride extends PartialOverrideBase {

    @Override
    public void setIntValue(int intValue) {
        super.setIntValue(intValue);
    }

    @Override
    public void setStrValue(String strValue) {
        super.setStrValue(strValue);
    }
}
