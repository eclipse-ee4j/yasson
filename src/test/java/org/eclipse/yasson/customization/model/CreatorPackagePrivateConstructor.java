package org.eclipse.yasson.customization.model;

import javax.json.bind.annotation.JsonbCreator;
import javax.json.bind.annotation.JsonbProperty;

/**
 * @author Roman Grigoriadi
 */
public class CreatorPackagePrivateConstructor {

    private String strVal;

    private final int intVal;

    @JsonbCreator
    CreatorPackagePrivateConstructor(@JsonbProperty("intVal") int intVal) {
        this.intVal = intVal;
    }

    public String getStrVal() {
        return strVal;
    }

    public void setStrVal(String strVal) {
        this.strVal = strVal;
    }

    public int getIntVal() {
        return intVal;
    }

}
