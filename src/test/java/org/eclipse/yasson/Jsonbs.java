/*
 * Copyright (c) 2019, 2023 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.yasson;

import java.util.function.Consumer;
import java.util.function.Supplier;

import jakarta.json.bind.*;
import org.eclipse.yasson.internal.*;

public class Jsonbs {
	public static final Jsonb defaultJsonb = JsonbBuilder.create();
	public static final Jsonb bindingJsonb = new JsonBindingBuilder().build();
	public static final Jsonb formattingJsonb = JsonbBuilder.create(new JsonbConfig().withFormatting(Boolean.TRUE));
	public static final Jsonb nullableJsonb = JsonbBuilder.create(new JsonbConfig().withNullValues(Boolean.TRUE));
    public static final YassonJsonb yassonJsonb = (YassonJsonb) JsonbBuilder.create();
	public static final YassonJsonb bindingYassonJsonb = (YassonJsonb) new JsonBindingProvider().create().build();

	private static void testWithJsonb(Supplier<Jsonb> supplier, Consumer<Jsonb> consumer){
		try (Jsonb jsonb = supplier.get()) {
			consumer.accept(jsonb);
		} catch (InterruptedException ie) {
			throw new RuntimeException("InterruptedException was thrown", ie);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static void testWithJsonbBuilderNewBuilder(JsonbConfig jsonbConfig, Consumer<Jsonb> consumer){
		testWithJsonb(() -> jsonbConfig == null ? JsonbBuilder.newBuilder().build() : JsonbBuilder.newBuilder().withConfig(jsonbConfig).build(), consumer);
	}

	public static void testWithJsonbBuilderNewBuilder(Consumer<Jsonb> consumer){
		testWithJsonbBuilderNewBuilder(null, consumer);
	}

	public static void testWithJsonbBuilderCreate(JsonbConfig jsonbConfig, Consumer<Jsonb> consumer){
		testWithJsonb(() -> jsonbConfig == null ? JsonbBuilder.create() : JsonbBuilder.create(jsonbConfig), consumer);
	}

	public static void testWithJsonbBuilderCreate(Consumer<Jsonb> consumer) {
		testWithJsonbBuilderCreate(null, consumer);
	}

	private Jsonbs() {}
}
