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

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

import jakarta.json.JsonObject;
import jakarta.json.JsonString;
import jakarta.json.bind.JsonbException;
import jakarta.json.bind.serializer.DeserializationContext;
import jakarta.json.bind.serializer.JsonbDeserializer;
import jakarta.json.stream.JsonParser;

import org.eclipse.yasson.internal.DeserializationContextImpl;

import static org.eclipse.yasson.Jsonbs.defaultJsonb;

public class JsonParserTestDeserializers {

	public static class JsonParserTestNoSuchElementExceptionDeserializer implements JsonbDeserializer<JsonParserTestPojo> {
		@Override
		public JsonParserTestPojo deserialize(JsonParser parser, DeserializationContext ctx, Type rtType) {
			try {
				parser.next();
			} catch (NoSuchElementException e) {
				throw new JsonbException("Level below zero", e);
			}
			return new JsonParserTestPojo();
		}
	}

	public static class JsonParserTestValueStreamDeserializer implements JsonbDeserializer<JsonParserTestPojo> {
		@Override
		public JsonParserTestPojo deserialize(JsonParser parser, DeserializationContext ctx, Type rtType) {
			JsonObject value = parser.getValueStream().findFirst().orElseThrow().asJsonObject();
			return defaultJsonb.fromJson(value.toString(), JsonParserTestPojo.class);
		}
	}

	public static class JsonParserTestGetObjectDeserializer implements JsonbDeserializer<JsonParserTestPojo> {

		@Override
		public JsonParserTestPojo deserialize(JsonParser parser, DeserializationContext ctx, Type rtType) {
			parser.getObject();
			return new JsonParserTestPojo();
		}
	}

	public static class JsonParserTestGetArrayDeserializer implements JsonbDeserializer<JsonParserTestPojo> {
		@Override
		public JsonParserTestPojo deserialize(JsonParser parser, DeserializationContext ctx, Type rtType) {
			parser.getArray();
			return new JsonParserTestPojo();
		}
	}

	public static class JsonParserTestEndOfObjectDeserializer implements JsonbDeserializer<JsonParserTestPojo> {
		@Override
		public JsonParserTestPojo deserialize(JsonParser parser, DeserializationContext ctx, Type rtType) {
			//un-comment as soon as Parsson bug #112 is fixed
//			parser.next();
			parser.skipObject();
			parser.getValue();
			return new JsonParserTestPojo();
		}
	}

	public static class JsonParserTestEndOfArrayDeserializer implements JsonbDeserializer<JsonParserTestPojo> {
		@Override
		public JsonParserTestPojo deserialize(JsonParser parser, DeserializationContext ctx, Type rtType) {
			parser.next();
			parser.next();
			parser.getString();
			parser.next();
			parser.skipArray();
			parser.getValue();
			return new JsonParserTestPojo();
		}
	}

	public static class JsonParserTestObjectDeserializer implements JsonbDeserializer<JsonParserTestPojo> {

		private boolean integralNumber;
		private String location;
		private final List<String> keyNames = new ArrayList<>();
		private final List<JsonParser.Event> parserEvents = new ArrayList<>();
		private final List<JsonParser.Event> contextEvents = new ArrayList<>();

		@Override
		public JsonParserTestPojo deserialize(JsonParser parser, DeserializationContext ctx, Type rtType) {
			JsonParserTestPojo result = new JsonParserTestPojo();
			if (ctx instanceof DeserializationContextImpl) {
				DeserializationContextImpl deserializationContext = (DeserializationContextImpl) ctx;
				skipKey(parser, deserializationContext);
				parser.skipArray();
				skipKey(parser, deserializationContext);
				parser.skipObject();
				skipKey(parser, deserializationContext);
				integralNumber = parser.isIntegralNumber();
				location = parser.getLocation().toString();
				result.bigDecimal = parser.getBigDecimal();
				skipKey(parser, deserializationContext);
				result.integer = parser.getInt();
				skipKey(parser, deserializationContext);
				result.longValue = parser.getLong();
				skipKey(parser, deserializationContext);
				result.string = parser.getString();
				skipKey(parser, deserializationContext);
				result.stringList = parser.getArray().stream().map(value -> ((JsonString) value).getString()).collect(Collectors.toList());
				skipKey(parser, deserializationContext);
				result.stringList_getStream = parser.getArrayStream().map(value -> ((JsonString) value).getString()).collect(Collectors.toList());
				skipKey(parser, deserializationContext);
				result.stringList_getValue =
						parser.getValue().asJsonArray().stream().map(value -> ((JsonString) value).getString()).collect(Collectors.toList());
				skipKey(parser, deserializationContext);
				result.string_getValue = ((JsonString) parser.getValue()).getString();
				skipKey(parser, deserializationContext);
				result.subPojo = new JsonParserTestPojo.JsonParserTestSubPojo(parser.getObject().getString("name"));
				skipKey(parser, deserializationContext);
				result.subPojo_getStream = parser.getObjectStream()
						.map(o -> new JsonParserTestPojo.JsonParserTestSubPojo(((JsonString) o.getValue()).getString()))
						.collect(Collectors.toList()).get(0);
				//following can't be tested before Parsson bug #112 is fixed.
						/*.findFirst().orElse(null);
				parser.skipObject();*/
				skipKey(parser, deserializationContext);
				result.subPojo_getValue = new JsonParserTestPojo.JsonParserTestSubPojo(parser.getValue().asJsonObject().getString("name"));
			}
			return result;
		}

		private void skipKey(JsonParser parser, DeserializationContextImpl deserializationContext) {
			parser.hasNext();
			parserEvents.add(parser.currentEvent());
			contextEvents.add(deserializationContext.getLastValueEvent());
			parser.next();
			String key = parser.getString();
			keyNames.add(key);
			parser.next();
		}

		public boolean isIntegralNumber() {
			return integralNumber;
		}

		public String getLocation() {
			return location;
		}

		public List<String> getKeyNames() {
			return Collections.unmodifiableList(keyNames);
		}

		public List<JsonParser.Event> getParserEvents() {
			return Collections.unmodifiableList(parserEvents);
		}

		public List<JsonParser.Event> getContextEvents() {
			return Collections.unmodifiableList(contextEvents);
		}
	}
}
