/*
 * Copyright (c) 2026 IBM and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.yasson.internal.deserializer;

import org.junit.jupiter.api.Test;

import jakarta.json.stream.JsonParser;

import static org.junit.jupiter.api.Assertions.fail;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;


/**
 * The {@link jakarta.json.stream.JsonParser} interface has optional methods that 
 * throw default UnsupportedOperationExceptions, but that must be implemented in
 * order to pass the JSON-P TCK.  Since the YassonParser wraps a JsonParser we 
 * must ensure that we implement these default methods and we cannot rely on the
 * compiler to tell us if we missed one so this test will.
 */
public class YassonParserTest {

    @Test
    public void overrideDefaultMethodTest() {
        List<Method> expectedMethods = Arrays.asList(JsonParser.class.getMethods());
        
        for(Method expectedMethod : expectedMethods) {
            if(!expectedMethod.isDefault()) {
                continue; //compiler will catch if we fail to implement
            }

            try {
                YassonParser.class.getDeclaredMethod(expectedMethod.getName(), expectedMethod.getParameterTypes());
            } catch (NoSuchMethodException e) {
                fail("Expected YassonParser to override " + expectedMethod.getName() 
                        + " but instead got " + e.getMessage());
            }
        }
    }
}
