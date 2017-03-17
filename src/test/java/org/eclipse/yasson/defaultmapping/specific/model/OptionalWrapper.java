package org.eclipse.yasson.defaultmapping.specific.model;

import java.util.Optional;

/**
 * @author Roman Grigoriadi
 */
public class OptionalWrapper {

    private Optional<Street> streetOptional;

    public Optional<Street> getStreetOptional() {
        return streetOptional;
    }

    public void setStreetOptional(Optional<Street> streetOptional) {
        this.streetOptional = streetOptional;
    }
}
