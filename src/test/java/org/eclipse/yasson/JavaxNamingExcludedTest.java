/*******************************************************************************
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 ******************************************************************************/
package org.eclipse.yasson;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import org.eclipse.yasson.internal.cdi.NonCdiAdapter;
import org.eclipse.yasson.internal.components.JsonbComponentInstanceCreatorFactory;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.annotation.JsonbTypeAdapter;

/**
 * Requires --limit-modules java.base,java.logging,java.sql (to exclude java.naming) to work.
 * See pom.xml surefire plugin configuration.
 */
public class JavaxNamingExcludedTest {

    @Test
    public void testNoJavaxNamingModule() {
        try {
            Class.forName(JsonbComponentInstanceCreatorFactory.INITIAL_CONTEXT_CLASS);
            fail("Class [" + JsonbComponentInstanceCreatorFactory.INITIAL_CONTEXT_CLASS
                    + "] should not be available for this test.");
        } catch (ClassNotFoundException e) {
            //OK, java.naming is not observable
        }

        Jsonb jsonb = JsonbBuilder.create();
        final String result = jsonb.toJson(new AdaptedPojo());
        assertEquals("{\"adaptedValue1\":1111,\"adaptedValue2\":1001,\"adaptedValue3\":1010}", result);

    }

    public static final class AdaptedPojo {
        @JsonbTypeAdapter(NonCdiAdapter.class)
        public String adaptedValue1 = "1111";

        @JsonbTypeAdapter(NonCdiAdapter.class)
        public String adaptedValue2 = "1001";

        @JsonbTypeAdapter(NonCdiAdapter.class)
        public String adaptedValue3 = "1010";

    }
}
