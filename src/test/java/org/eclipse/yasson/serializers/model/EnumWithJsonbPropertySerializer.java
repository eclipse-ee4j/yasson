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

package org.eclipse.yasson.serializers.model;

import java.lang.reflect.ParameterizedType;
import java.util.EnumMap;

import org.eclipse.yasson.adapters.model.EnumJsonbPropertyMaps;

import jakarta.json.bind.serializer.JsonbSerializer;
import jakarta.json.bind.serializer.SerializationContext;
import jakarta.json.stream.JsonGenerator;

public class EnumWithJsonbPropertySerializer<E extends Enum<E>> implements JsonbSerializer<E> {
	private final EnumMap<E, String> javaToJsonMapping;

	public EnumWithJsonbPropertySerializer() {
		super();

		EnumJsonbPropertyMaps<E> enumMappingMaps = new EnumJsonbPropertyMaps<>(getEnumType());
		javaToJsonMapping = enumMappingMaps.getJavaToJsonMapping();
	}

	private Class<E> getEnumType() {
		@SuppressWarnings("unchecked")
		Class<E> cast = Class.class.cast(((ParameterizedType) getClass().getGenericSuperclass())
				.getActualTypeArguments()[0]);
		return cast;
	}

	@Override public void serialize(E obj, JsonGenerator generator, SerializationContext ctx) {
		generator.write(javaToJsonMapping.get(obj));
	}
}
