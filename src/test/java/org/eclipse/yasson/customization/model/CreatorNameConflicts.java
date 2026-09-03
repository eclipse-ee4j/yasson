/*
 * Copyright (c) 2026 Eclipse and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.yasson.customization.model;

import java.util.Locale;

import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsonbProperty;

/**
 * This class is used to test the behavior of @JsonbCreator when there are name conflicts between constructor parameters and class fields.
 * The constructor parameter "fromJson" is annotated with @JsonbProperty("fromJson"),
 * which should map the JSON property "fromJson" to the constructor parameter, and then assign it to the field "fromCreator".
 * 
 * The field "fromJson" should remain null since it is not assigned in the constructor.
 * The field "fromCreator" should be set to the value of "fromJson" from the JSON, converted to uppercase.
 * The field "notProvided" should remain null since it is not provided in the JSON and has no default value.
 * The field "notDeclared" is an int and should be set to its default value (0) since it is not provided in the JSON.
 * 
 * CreatorNameConflicts
 */
public class CreatorNameConflicts {
    public String fromJson;    
    public String fromCreator; 
    public String notProvided; 
    public int notDeclared;

    @JsonbCreator
    public CreatorNameConflicts(
          @JsonbProperty("fromJson") String fromJson,
          @JsonbProperty("notDeclared") int notDeclared) {
        this.fromCreator = fromJson.toUpperCase(Locale.ROOT);
        this.notDeclared = notDeclared;
    }
}
