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

import java.math.BigDecimal;
import java.util.List;

public class JsonParserTestPojo {
	public String string;
	public Integer integer;
	public Long longValue;
	public BigDecimal bigDecimal;
	public JsonParserTestSubPojo subPojo;
	public List<String> stringList;
	public JsonParserTestSubPojo subPojo_getValue;
	public String string_getValue;
	public List<String> stringList_getValue;
	public List<String> stringList_getStream;
	public JsonParserTestSubPojo subPojo_getStream;

	public JsonParserTestPojo() {
	}

	public JsonParserTestPojo init() {
		string = "string";
		integer = 1;
		longValue = 2L;
		bigDecimal = BigDecimal.TEN;
		subPojo = new JsonParserTestSubPojo("subPojo");
		stringList = List.of("string1", "string2");
		subPojo_getValue = new JsonParserTestSubPojo("subPojo_getValue");
		string_getValue = "string_getValue";
		stringList_getValue = List.of("string3", "string4");
		stringList_getStream = List.of("string5", "string6");
		subPojo_getStream = new JsonParserTestSubPojo("subPojo_getStream");
		return this;
	}

	@Override
	public boolean equals(Object obj) {
		return obj != null &&
				getClass().isAssignableFrom(obj.getClass()) &&
				TwoObjectsComparer.getDifferentFieldInTwoObjects(this, obj).isEmpty();
	}

	public static class JsonParserTestSubPojo {

		public String name;

		protected JsonParserTestSubPojo() {
		}

		JsonParserTestSubPojo(String name) {
			this.name = name;
		}

		@Override
		public boolean equals(Object obj) {
			return obj != null &&
					getClass().isAssignableFrom(obj.getClass()) &&
					TwoObjectsComparer.getDifferentFieldInTwoObjects(this, obj).isEmpty();
		}
	}
}
