/*******************************************************************************
 * Copyright (c) 2019 Payara Services and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *  Payara Services - initial implementation
 ******************************************************************************/
package org.eclipse.yasson.defaultmapping.recursive;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import javax.json.bind.Jsonb;

import org.eclipse.yasson.internal.JsonBindingBuilder;
import org.eclipse.yasson.serializers.model.RecursiveObject;
import org.junit.Test;


/**
 * Testing for recursive object mapping
 *
 * @author Matt Gill
 */
public class RecursionTest {

    private static final RecursiveObject OBJECT = RecursiveObject.construct(5);
    private static final String OBJECT_STRING = "{\"child\":{\"child\":{\"child\":{\"child\":{\"child\":{\"id\":1}}}}}}";

    private final Jsonb jsonb = (new JsonBindingBuilder()).build();

    @Test
    public void testRecursiveObjectSerialisation() throws IOException {
        assertEquals(OBJECT_STRING, jsonb.toJson(OBJECT));
    }

    @Test
    public void testRecursiveObjectDeserialisation() throws IOException {
        assertEquals(OBJECT, jsonb.fromJson(OBJECT_STRING, RecursiveObject.class));
    }

}

