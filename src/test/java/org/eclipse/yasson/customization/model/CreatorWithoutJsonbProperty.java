package org.eclipse.yasson.customization.model;

import javax.json.bind.annotation.JsonbCreator;

/**
 * @author Roman Grigoriadi
 */
public class CreatorWithoutJsonbProperty {

    @JsonbCreator
    public CreatorWithoutJsonbProperty(String arg0) {

    }
}
