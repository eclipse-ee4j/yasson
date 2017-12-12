package org.eclipse.yasson.adapters.model;

import java.util.Optional;
import java.util.UUID;

public class UUIDMapperClsBased extends MultilevelAdapterClass<UUID, String> {

    @Override
    public String adaptToJson(UUID obj) throws Exception {
        return Optional.ofNullable(obj).map(UUID::toString).orElse(null);
    }

    @Override
    public UUID adaptFromJson(String obj) throws Exception {
        return Optional.ofNullable(obj).map(UUID::fromString).orElse(null);
    }
}