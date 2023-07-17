/*
 * Copyright (c) 2023, 2023 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.yasson.records;

import jakarta.json.bind.annotation.JsonbProperty;
import jakarta.json.bind.annotation.JsonbTypeDeserializer;
import jakarta.json.bind.serializer.DeserializationContext;
import jakarta.json.bind.serializer.JsonbDeserializer;
import jakarta.json.stream.JsonParser;

import java.lang.reflect.Type;
import java.util.UUID;

public record CarWithUuidDeserializer(
        @JsonbProperty("car.id") @JsonbTypeDeserializer(CarUuidDeserializer.class) CarId carId,
        @JsonbProperty("colour") String colour
) {
    public static class CarUuidDeserializer implements JsonbDeserializer<CarId> {

        @Override
        public CarId deserialize(JsonParser parser, DeserializationContext ctx, Type rtType) {
            String givenString = parser.getString();
            var carUuid = UUID.fromString(givenString);

            return new CarId(carUuid);
        }
    }
}


