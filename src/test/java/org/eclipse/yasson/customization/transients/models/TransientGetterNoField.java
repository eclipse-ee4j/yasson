package org.eclipse.yasson.customization.transients.models;

import javax.json.bind.annotation.JsonbTransient;

public class TransientGetterNoField {

    @JsonbTransient
    public String getFooString() {
        return "";
    }

}
