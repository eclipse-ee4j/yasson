package org.eclipse.yasson.customization.model;

import javax.json.bind.annotation.JsonbCreator;
import javax.json.bind.annotation.JsonbProperty;

/**
 * @author Roman Grigoriadi
 */
public class CreatorWithoutJavabeanProperty {

    private String strField;

    @JsonbCreator
    public CreatorWithoutJavabeanProperty(@JsonbProperty("s1") String s1, @JsonbProperty("s2") String s2) {
        this.strField = s1 + s2;
    }

    public String getStrField() {
        return strField;
    }
}
