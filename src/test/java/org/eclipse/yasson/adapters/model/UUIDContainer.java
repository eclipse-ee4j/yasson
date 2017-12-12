package org.eclipse.yasson.adapters.model;

import javax.json.bind.annotation.JsonbTypeAdapter;
import java.util.UUID;


public class UUIDContainer {
    @JsonbTypeAdapter(UUIDMapperClsBased.class)
    private UUID uuidClsBased;

    @JsonbTypeAdapter(UUIDMapperClsBased.class)
    private UUID uuidIfcBased;

    public UUID getUuidClsBased() {
        return uuidClsBased;
    }

    public void setUuidClsBased(UUID uuidClsBased) {
        this.uuidClsBased = uuidClsBased;
    }

    public UUID getUuidIfcBased() {
        return uuidIfcBased;
    }

    public void setUuidIfcBased(UUID uuidIfcBased) {
        this.uuidIfcBased = uuidIfcBased;
    }
}
