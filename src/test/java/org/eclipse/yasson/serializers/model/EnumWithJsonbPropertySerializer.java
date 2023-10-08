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
import java.util.EnumMap;
import java.util.stream.Stream;

import jakarta.json.bind.annotation.JsonbProperty;
import jakarta.json.bind.serializer.JsonbSerializer;
import jakarta.json.bind.serializer.SerializationContext;
import jakarta.json.stream.JsonGenerator;

public class EnumWithJsonbPropertySerializer<E extends Enum<E>> implements JsonbSerializer<E> {
	private final EnumMap<E, String> javaToJsonMapping;

	public EnumWithJsonbPropertySerializer() {
		super();

		Class<E> enumType = getEnumType();
		javaToJsonMapping = new EnumMap<>(enumType);

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
			javaToJsonMapping.put(constant, asString);
		});
	}

	private Class<E> getEnumType() {
		return Class.class.cast(ParameterizedType.class.cast(
						getClass().getGenericSuperclass())
				.getActualTypeArguments()[0]);
	}

	@Override public void serialize(E obj, JsonGenerator generator, SerializationContext ctx) {
		generator.write(javaToJsonMapping.get(obj));
	}
}
