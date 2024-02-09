/*
 * Copyright (c) 2019, 2024 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.yasson.customization.polymorphism;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertInstanceOf;

import java.math.BigDecimal;
import java.math.BigInteger;

import org.eclipse.yasson.Jsonbs;
import org.junit.jupiter.api.Test;

import jakarta.json.Json;
import jakarta.json.JsonArray;
import jakarta.json.JsonObject;
import jakarta.json.JsonValue;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.annotation.JsonbSubtype;
import jakarta.json.bind.annotation.JsonbTypeInfo;

public class PolymorphismWithJsonValuesTest {

	/**
	 * This test shows that the default Jsonb implementation support polymorphism with JsonValues.
	 * See <a href="https://github.com/eclipse-ee4j/yasson/issues/625">GitHub issue</a>
	 */
	@Test
	public void testObjectWithJsonValue() {
		Jsonb jsonb = Jsonbs.defaultJsonb;
		var container = new PolyContainer.JsValueContainer();
		String containerSerialized = jsonb.toJson(container);
		assertEquals("{\"type\":\"jsv\",\"jsonArray\":[\"an array json string\"],\"jsonBigDecimalValue\":10,\"jsonBigIntegerValue\":10,"
				+ "\"jsonDoubleValue\":1.0,\"jsonIntValue\":1,\"jsonLongValue\":1,\"jsonObject\":{\"field\":\"an object json string\"},"
				+ "\"jsonStringValue\":\"a json string\"}", containerSerialized);

		var deserializedDirectly = jsonb.fromJson(containerSerialized, PolyContainer.JsValueContainer.class);//good
		assertInstanceOf(PolyContainer.JsValueContainer.class, deserializedDirectly);
		assertEquals(container.jsonStringValue, deserializedDirectly.jsonStringValue);
		assertEquals(container.jsonIntValue, deserializedDirectly.jsonIntValue);
		assertEquals(container.jsonLongValue, deserializedDirectly.jsonLongValue);
		assertEquals(container.jsonDoubleValue, deserializedDirectly.jsonDoubleValue);
		assertEquals(container.jsonBigDecimalValue, deserializedDirectly.jsonBigDecimalValue);
		assertEquals(container.jsonBigIntegerValue, deserializedDirectly.jsonBigIntegerValue);
		assertEquals(container.jsonArray, deserializedDirectly.jsonArray);
		assertEquals(container.jsonObject, deserializedDirectly.jsonObject);

		var deserializedFromPoly = jsonb.fromJson(containerSerialized, PolyContainer.class);//bad
		assertInstanceOf(PolyContainer.JsValueContainer.class, deserializedFromPoly);
		assertEquals(container.jsonStringValue, ((PolyContainer.JsValueContainer)deserializedFromPoly).jsonStringValue);
		assertEquals(container.jsonIntValue, ((PolyContainer.JsValueContainer)deserializedFromPoly).jsonIntValue);
		assertEquals(container.jsonLongValue, ((PolyContainer.JsValueContainer)deserializedFromPoly).jsonLongValue);
		assertEquals(container.jsonDoubleValue, ((PolyContainer.JsValueContainer)deserializedFromPoly).jsonDoubleValue);
		assertEquals(container.jsonBigDecimalValue, ((PolyContainer.JsValueContainer)deserializedFromPoly).jsonBigDecimalValue);
		assertEquals(container.jsonBigIntegerValue, ((PolyContainer.JsValueContainer)deserializedFromPoly).jsonBigIntegerValue);
		assertEquals(container.jsonArray, ((PolyContainer.JsValueContainer)deserializedFromPoly).jsonArray);
		assertEquals(container.jsonObject, ((PolyContainer.JsValueContainer)deserializedFromPoly).jsonObject);
	}

	@JsonbTypeInfo(
			key = "type",
			value = {@JsonbSubtype(alias = "jsv", type = PolyContainer.JsValueContainer.class)}
	)
	interface PolyContainer {
		class JsValueContainer implements PolyContainer {
			//fields should not be final, otherwise the test couldn't test
			public /*final*/ JsonValue jsonStringValue = Json.createValue("a json string");
			public /*final*/ JsonValue jsonIntValue = Json.createValue(1);
			public /*final*/ JsonValue jsonLongValue = Json.createValue(1L);
			public /*final*/ JsonValue jsonDoubleValue = Json.createValue(1d);
			public /*final*/ JsonValue jsonBigDecimalValue = Json.createValue(BigDecimal.TEN);
			public /*final*/ JsonValue jsonBigIntegerValue = Json.createValue(BigInteger.TEN);
			public /*final*/ JsonArray jsonArray = Json.createArrayBuilder().add("an array json string").build();
			public /*final*/ JsonObject jsonObject = Json.createObjectBuilder().add("field", "an object json string").build();
		}
	}
}