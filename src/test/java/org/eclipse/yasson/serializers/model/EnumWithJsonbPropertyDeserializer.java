package org.eclipse.yasson.serializers.model;

import static java.util.Optional.ofNullable;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import jakarta.json.bind.annotation.JsonbProperty;
import jakarta.json.bind.serializer.DeserializationContext;
import jakarta.json.bind.serializer.JsonbDeserializer;
import jakarta.json.stream.JsonParser;

public class EnumWithJsonbPropertyDeserializer<E extends Enum<E>> implements JsonbDeserializer<E> {

	private final Map<String, E> jsonToJavaMapping;

	public EnumWithJsonbPropertyDeserializer() {
		super();

		Class<E> enumType = getEnumType();
		jsonToJavaMapping = new HashMap<>();

		Stream.of(enumType.getEnumConstants()).forEach(constant -> {
			final String asString;
			try {
				asString = ofNullable(
						constant.getClass()
								.getDeclaredField(constant.name())
								.getAnnotation(JsonbProperty.class))
						.map(JsonbProperty::value)
						.orElseGet(constant::name);
			} catch (final NoSuchFieldException e) {
				throw new IllegalArgumentException(e);
			}
			jsonToJavaMapping.put(asString, constant);
		});
	}

	private Class<E> getEnumType() {
		return Class.class.cast(ParameterizedType.class.cast(
						getClass().getGenericSuperclass())
				.getActualTypeArguments()[0]);
	}

	@Override
	public E deserialize(JsonParser parser, DeserializationContext ctx, Type rtType) {
		String key = parser.getString();

		assert key != null;

		return ofNullable(jsonToJavaMapping.get(key))
				.orElseThrow(() -> new IllegalArgumentException("Unknown enum value: '" + key + "'"));
	}
}
