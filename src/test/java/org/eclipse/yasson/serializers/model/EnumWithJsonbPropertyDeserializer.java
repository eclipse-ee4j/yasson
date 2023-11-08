/*
 * Copyright (c) 2023 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

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
		@SuppressWarnings("unchecked")
		Class<E> cast = Class.class.cast(ParameterizedType.class.cast(
						getClass().getGenericSuperclass())
				.getActualTypeArguments()[0]);
		return cast;
	}

	@Override
	public E deserialize(JsonParser parser, DeserializationContext ctx, Type rtType) {
		String key = parser.getString();

		assert key != null;

		return ofNullable(jsonToJavaMapping.get(key))
				.orElseThrow(() -> new IllegalArgumentException("Unknown enum value: '" + key + "'"));
	}
}
