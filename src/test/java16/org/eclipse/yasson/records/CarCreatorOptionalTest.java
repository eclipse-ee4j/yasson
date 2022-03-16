/*
 * Copyright (c) 2022 Oracle and/or its affiliates. All rights reserved.
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

import java.util.Optional;

import javax.json.bind.annotation.JsonbCreator;
import javax.json.bind.annotation.JsonbProperty;

public record CarCreatorOptionalTest(String type, int somePrimitive, String color) {

    @JsonbCreator
    public CarCreatorOptionalTest(@JsonbProperty("type") Optional<String> type,
                                  @JsonbProperty("somePrimitive") int somePrimitive,
                                  @JsonbProperty("color") String color) {
        this(type.orElse("typeDefaultValue"), somePrimitive, color);
    }
}
