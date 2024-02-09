/*
 * Copyright (c) 2024 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.yasson.adapters.model;

import static java.util.Optional.ofNullable;

import java.lang.reflect.ParameterizedType;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import jakarta.json.bind.adapter.JsonbAdapter;
import jakarta.json.bind.annotation.JsonbProperty;

public class EnumWithJsonbPropertyAdapter<E extends Enum<E>> implements JsonbAdapter<E, String> {
	private final Map<String, E> jsonToJavaMapping = new HashMap<>();
	private final EnumMap<E, String> javaToJsonMapping;

	public EnumWithJsonbPropertyAdapter() {
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
			jsonToJavaMapping.put(asString, constant);
		});
	}

	private Class<E> getEnumType() {
		@SuppressWarnings("unchecked")
		Class<E> cast = Class.class.cast(((ParameterizedType) getClass().getGenericSuperclass())
				.getActualTypeArguments()[0]);
		return cast;
	}

	@Override
	public String adaptToJson(final E obj) {
		return javaToJsonMapping.get(obj);
	}

	@Override
	public E adaptFromJson(final String obj) {
		return ofNullable(jsonToJavaMapping.get(obj))
				.orElseThrow(() -> new IllegalArgumentException("Unknown enum value: '" + obj + "'"));
	}
}
