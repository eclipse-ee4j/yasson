package org.eclipse.yasson.serializers.model;

import jakarta.json.bind.annotation.JsonbProperty;
import jakarta.json.bind.annotation.JsonbTypeDeserializer;
import jakarta.json.bind.annotation.JsonbTypeSerializer;

@JsonbTypeSerializer(ColorsSerializer.class)
@JsonbTypeDeserializer(ColorsDeserializer.class)
public enum Colors {
	@JsonbProperty("Red")
	RED,
	@JsonbProperty("Green")
	GREEN
}

