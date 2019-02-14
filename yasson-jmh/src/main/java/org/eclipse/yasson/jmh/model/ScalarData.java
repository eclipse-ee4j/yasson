package org.eclipse.yasson.jmh.model;

public class ScalarData {

    private String stringValue;

    private Integer integerValue;

    public ScalarData(String stringValue, Integer integerValue) {
        this.stringValue = stringValue;
        this.integerValue = integerValue;
    }

    public ScalarData() {
        this.stringValue = "Short string";
        this.integerValue = 10;
    }

    public String getStringValue() {
        return stringValue;
    }

    public void setStringValue(String stringValue) {
        this.stringValue = stringValue;
    }

    public Integer getIntegerValue() {
        return integerValue;
    }

    public void setIntegerValue(Integer integerValue) {
        this.integerValue = integerValue;
    }
}
