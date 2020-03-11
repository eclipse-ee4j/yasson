/*
 * Copyright (c) 2019, 2020 IBM and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */
package org.eclipse.yasson.internal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.lang.reflect.Field;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;

import org.eclipse.yasson.YassonConfig;
import org.eclipse.yasson.internal.model.ClassModel;
import org.junit.jupiter.api.Test;

public class JsonBindingTest {
    
    public static class EagerParseClass {
        public String foo;
    }
    
    @Test
    public void testEagerInit() throws Exception {
        Jsonb jsonb = JsonbBuilder.create(new YassonConfig()
                .withEagerParsing(EagerParseClass.class));
        assertNotNull(getClassModel(jsonb, EagerParseClass.class));
        
        EagerParseClass obj = new EagerParseClass();
        obj.foo = "foo";
        assertEquals("{\"foo\":\"foo\"}", jsonb.toJson(obj));
    }
    
    @Test
    public void testNoEagerInit() throws Exception {
        Jsonb jsonb = JsonbBuilder.create();
        assertNull(getClassModel(jsonb, EagerParseClass.class));
        
        EagerParseClass obj = new EagerParseClass();
        obj.foo = "foo";
        assertEquals("{\"foo\":\"foo\"}", jsonb.toJson(obj));
        
        assertNotNull(getClassModel(jsonb, EagerParseClass.class));
    }
    
    private ClassModel getClassModel(Jsonb jsonb, Class<?> clazz) throws Exception {
        // Do some hacks to ensure that the class had a ClassModel registered
        JsonBinding yasson = (JsonBinding) jsonb;
        Field jsonbContext = yasson.getClass().getDeclaredField("jsonbContext");
        jsonbContext.setAccessible(true);
        JsonbContext ctx = (JsonbContext) jsonbContext.get(yasson);
        return ctx.getMappingContext().getClassModel(clazz);
    }

}
