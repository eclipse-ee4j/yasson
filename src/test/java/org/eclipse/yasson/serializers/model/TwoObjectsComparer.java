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

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

public class TwoObjectsComparer {
	public final String propertyName;
	public final Object firstObjectValue;
	public final Object secondObjectValue;

	public static <T> Optional<TwoObjectsComparer> getDifferentFieldInTwoObjects(T thisObject, T otherObject) {
		return otherObject == null ? Optional.empty() : Arrays.stream(thisObject.getClass().getFields()).map(f -> {
			try {
				return new TwoObjectsComparer(f.getName(), f.get(thisObject), f.get(otherObject));
			} catch (IllegalAccessException e) {
				throw new RuntimeException(e);
			}
		}).filter(o -> !Objects.equals(o.firstObjectValue, o.secondObjectValue)).findFirst();
	}

	private TwoObjectsComparer(String name, Object firstObjectValue, Object secondObjectValue) {
		this.propertyName = name;
		this.firstObjectValue = firstObjectValue;
		this.secondObjectValue = secondObjectValue;
	}

	@Override
	public String toString() {
		return "propertyName: " + propertyName + " firstObjectValue: " + firstObjectValue + " secondObjectValue: " + secondObjectValue;
	}
}
