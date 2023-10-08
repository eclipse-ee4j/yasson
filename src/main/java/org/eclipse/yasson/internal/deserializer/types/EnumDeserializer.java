/*
 * Copyright (c) 2021, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.yasson.internal.deserializer.types;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import org.eclipse.yasson.internal.DeserializationContextImpl;
import org.eclipse.yasson.internal.model.ClassModel;
import org.eclipse.yasson.internal.model.PropertyModel;

/**
 * Deserializer of the {@link Enum}.
 */
class EnumDeserializer extends TypeDeserializer {

	private final Map<String, ? extends Enum<?>> nameToConstantMap;

	EnumDeserializer(TypeDeserializerBuilder builder) {
		super(builder);

		nameToConstantMap = createNameToConstantMap(builder);
	}

	private static <E extends Enum<E>> Map<String, E> createNameToConstantMap(TypeDeserializerBuilder builder) {
		Map<String, E> nameToConstantMap = null;
		Class<?> clazz = builder.getClazz();

		if (clazz.isEnum()) {
			try {
				@SuppressWarnings("unchecked")
				Class<E> enumClazz = (Class<E>) clazz;
				nameToConstantMap = new HashMap<>();
				ClassModel classModel = builder.getJsonbContext().getMappingContext().getOrCreateClassModel(clazz);

				for (E enumConstant : enumClazz.getEnumConstants()) {
					PropertyModel model = classModel.getPropertyModel(enumConstant.name());
					nameToConstantMap.put(model.getReadName(), enumConstant);
				}
			} catch (ClassCastException classCastException) {
				throw new IllegalArgumentException("EnumDeserializer can only be used with Enum types");
			}
		}
		return nameToConstantMap;
	}

	@SuppressWarnings({"unchecked", "rawtypes"})
	@Override
	Object deserializeStringValue(String value, DeserializationContextImpl context, Type rType) {
		return nameToConstantMap == null ? Enum.valueOf((Class<Enum>) rType, value) : nameToConstantMap.get(value);
	}
}
