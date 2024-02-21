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

import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Stream;

import jakarta.json.bind.annotation.JsonbProperty;

public class EnumJsonbPropertyMaps<E extends Enum<E>> {
	private final Map<String, E> jsonToJavaMapping = new HashMap<>();
	private final EnumMap<E, String> javaToJsonMapping;

	public EnumJsonbPropertyMaps(Class<E> enumType) {
		super();

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

	public EnumMap<E, String> getJavaToJsonMapping() {
		return javaToJsonMapping;
	}

	public Map<String, E> getJsonToJavaMapping() {
		return jsonToJavaMapping;
	}
}
