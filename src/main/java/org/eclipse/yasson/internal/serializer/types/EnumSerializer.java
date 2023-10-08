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

package org.eclipse.yasson.internal.serializer.types;

import java.util.EnumMap;

import org.eclipse.yasson.internal.SerializationContextImpl;
import org.eclipse.yasson.internal.model.ClassModel;
import org.eclipse.yasson.internal.model.PropertyModel;

import jakarta.json.stream.JsonGenerator;

/**
 * Serializer of the {@link Enum} types.
 */
class EnumSerializer extends TypeSerializer<Enum<?>> {

	private final EnumMap<? extends Enum<?>, String> constantToNameMap;

	EnumSerializer(TypeSerializerBuilder serializerBuilder) {
		super(serializerBuilder);

		constantToNameMap = createConstantToNameMap(serializerBuilder);
	}

	private static <E extends Enum<E>> EnumMap<E, String> createConstantToNameMap(TypeSerializerBuilder serializerBuilder) {
		EnumMap<E, String> constantToNameMap = null;
		Class<?> clazz = serializerBuilder.getClazz();

		if (clazz.isEnum()) {
			try{
				@SuppressWarnings("unchecked")
				Class<E> enumClazz = (Class<E>) clazz;
				constantToNameMap = new EnumMap<>(enumClazz);
				ClassModel classModel = serializerBuilder.getJsonbContext().getMappingContext().getOrCreateClassModel(clazz);

				for (E enumConstant : enumClazz.getEnumConstants()) {
					PropertyModel model = classModel.getPropertyModel(enumConstant.name());
					constantToNameMap.put(enumConstant, model.getWriteName());
				}
			} catch (ClassCastException classCastException) {
				throw new IllegalArgumentException("EnumSerializer can only be used with Enum types");
			}
		}
		return constantToNameMap;
	}

	@Override
	void serializeValue(Enum<?> value, JsonGenerator generator, SerializationContextImpl context) {
		generator.write(constantToNameMap == null ? value.name() : constantToNameMap.get(value));
	}

	@Override
	void serializeKey(Enum<?> key, JsonGenerator generator, SerializationContextImpl context) {
		generator.writeKey(constantToNameMap == null ? key.name() : constantToNameMap.get(key));
	}
}
