/*******************************************************************************
 * Copyright (c) 2015 Oracle and/or its affiliates. All rights reserved.
 * Copyright (c) 2019 Payara Foundation and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * Roman Grigoriadi
 ******************************************************************************/

package org.eclipse.yasson.defaultmapping.specific;

import org.eclipse.yasson.TestTypeToken;
import org.eclipse.yasson.defaultmapping.specific.model.Street;
import org.eclipse.yasson.internal.JsonBindingBuilder;
import org.junit.Before;
import org.junit.Test;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertNull;

/**
 * @author Roman Grigoriadi
 */
public class NullTest {

    private Jsonb jsonb;

    @Before
    public void before() {
        jsonb = JsonbBuilder.create();
    }

    @Test
    public void testSetsNullIntoFields() {
        String json = "{\"name\":null,\"number\":null}";

        Street result = jsonb.fromJson(json, Street.class);
        //these have default initialization value
        assertNull(result.getName());
        assertNull(result.getNumber());
    }

    @Test
    public void testDeserializeNull() {
        assertNull(jsonb.fromJson("null", Object.class));
    }

    @Test
    public void testDeserializeNullPojo() {
        assertNull(jsonb.fromJson("null", Street.class));
    }

    @Test
    public void testDeserializeNullList() {
        assertNull(jsonb.fromJson("null", new TestTypeToken<List<Integer>>() {}.getType()));
    }

    @Test
    public void testDeserializeNullMap() {
        assertNull(jsonb.fromJson("null", new TestTypeToken<Map<String, Street>>() {}.getType()));
    }

}
