/*
 * Copyright (c) 2022, 2024 Oracle and/or its affiliates. All rights reserved.
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

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;
import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.config.PropertyNamingStrategy;
import org.junit.jupiter.api.Test;

import static java.util.Objects.requireNonNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class CarWithCreateNamingStrategyTest {

    // camel case is intentional for this test case
    public record Car(String brandName, String colorName) {

        @JsonbCreator
        public Car {
            requireNonNull(brandName, "brandName");
            requireNonNull(colorName, "colorName");
        }
    }

    @Test
    public void testRecordJsonbCreatorWithNamingStrategy() {
        // given
        final JsonbConfig config = new JsonbConfig()
                .withPropertyNamingStrategy(PropertyNamingStrategy.LOWER_CASE_WITH_UNDERSCORES);
        final Jsonb jsonb = JsonbBuilder.create(config);

        var json = """
                {
                  "brand_name": "Volkswagen",
                  "color_name": "Piano black"
                }
                """;

        // when
        final Car car = jsonb.fromJson(json, Car.class);

        // then
        assertEquals("Volkswagen", car.brandName());
        assertEquals("Piano black", car.colorName());
    }
}
