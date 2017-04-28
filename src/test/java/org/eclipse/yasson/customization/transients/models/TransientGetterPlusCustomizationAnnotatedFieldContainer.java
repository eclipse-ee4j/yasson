package org.eclipse.yasson.customization.transients.models;

import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbTransient;

public class TransientGetterPlusCustomizationAnnotatedFieldContainer {
    @JsonbProperty("instance")
    private String instance;

    @JsonbTransient
    public String getInstance() {
        return instance;
    }

    public void setInstance(String instance) {
        this.instance = instance;
    }
}