/*******************************************************************************
 * Copyright (c) 2019 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * Jorge Bescos Gascon
 ******************************************************************************/

package org.eclipse.yasson.defaultmapping.specific;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.Arrays;
import java.util.List;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import javax.json.bind.JsonbException;

import org.eclipse.yasson.Jsonbs;
import org.eclipse.yasson.adapters.model.Chain;
import org.eclipse.yasson.adapters.model.ChainAdapter;
import org.eclipse.yasson.adapters.model.ChainSerializer;
import org.eclipse.yasson.adapters.model.Foo;
import org.eclipse.yasson.adapters.model.FooAdapter;
import org.eclipse.yasson.adapters.model.FooSerializer;
import org.junit.jupiter.api.Test;

public class RecursiveReferenceTest {

    private static final Jsonb userSerializerJsonb = JsonbBuilder.create(new JsonbConfig()
            .withSerializers(new ChainSerializer(), new FooSerializer()));
    private static final Jsonb adapterSerializerJsonb = JsonbBuilder.create(new JsonbConfig()
            .withAdapters(new ChainAdapter(), new FooAdapter()));
    private final static List<Jsonb> testInstances = Arrays.asList(Jsonbs.defaultJsonb, adapterSerializerJsonb, userSerializerJsonb);
    
    @Test
    public void testSerializeRecursiveReference() {
        Chain recursive = new Chain("test");
        recursive.setLinksTo(recursive);
        try {
            Jsonbs.defaultJsonb.toJson(recursive);
            fail("Exception should be caught");
        } catch (JsonbException e) {
            assertEquals(
                    "Unable to serialize property 'linksTo' from org.eclipse.yasson.adapters.model.Chain",
                    e.getMessage());
            assertEquals(
                    "Recursive reference has been found in class class org.eclipse.yasson.adapters.model.Chain.",
                    e.getCause().getMessage());
        }
    }
    
    @Test
    public void testSerializeRecursiveReferenceCustomAdapter() {
        Chain recursive = new Chain("test");
        recursive.setLinksTo(recursive);
        try {
            adapterSerializerJsonb.toJson(recursive);
            fail("Exception should be caught");
        } catch (JsonbException e) {
            assertEquals(
                    "Problem adapting object of type class org.eclipse.yasson.adapters.model.Chain to java.util.Map<java.lang.String, java.lang.Object> in class class org.eclipse.yasson.adapters.model.ChainAdapter",
                    e.getMessage());
        }
    }
    
    @Test
    public void testSerializeRecursiveReferenceCustomSerializer() {
        Chain recursive = new Chain("test");
        recursive.setLinksTo(recursive);
        try {
            userSerializerJsonb.toJson(recursive);
            fail("Exception should be caught");
        } catch (JsonbException e) {
            assertEquals("Recursive reference has been found in class class org.eclipse.yasson.adapters.model.Chain.", e.getMessage());
        }
    }

    @Test
    public void testSerializeRepeatedInstance() {
        testInstances.forEach(jsonb -> checkSerializeRepeatedInstance(jsonb));
    }
    
    private void checkSerializeRepeatedInstance(Jsonb jsonb) {
        Chain recursive = new Chain("test");
        recursive.setLinksTo(new Chain("test"));
        String result = jsonb.toJson(Arrays.asList(recursive, recursive));
        assertEquals("[{\"linksTo\":{\"name\":\"test\"},\"name\":\"test\"},{\"linksTo\":{\"name\":\"test\"},\"name\":\"test\"}]", result);
    }

    @Test
    public void testDeserializeRecursiveReference() {
        testInstances.forEach(jsonb -> checkDeserializeRecursiveReference(jsonb));
    }
    
    private void checkDeserializeRecursiveReference(Jsonb jsonb) {
        Chain recursive = jsonb.fromJson("{\"linksTo\":{\"name\":\"test\"},\"name\":\"test\"}", Chain.class);
        assertNotEquals(recursive.getLinksTo(), recursive);
    }

    @Test
    public void testDeserialize2ReferencesSameObject() {
        A a = new A();
        Foo b = new Foo("foo");
        a.ref1 = b;
        a.ref2 = b;
        String result = Jsonbs.defaultJsonb.toJson(a);
        assertEquals("{\"ref1\":{\"bar\":\"foo\"},\"ref2\":{\"bar\":\"foo\"}}", result);
    }
    
    @Test
    public void testChain() {
        testInstances.forEach(jsonb -> checkChain(jsonb));
    }
    
    private void checkChain(Jsonb jsonb) {
        Foo foo = new Foo("foo");
        Chain c1 = new Chain("c1");
        Chain c2 = new Chain("c2");
        c1.setLinksTo(c2);
        c1.setHas(foo);
        c2.setHas(foo);
        String result = jsonb.toJson(c1);
        assertEquals("{\"has\":{\"bar\":\"foo\"},\"linksTo\":{\"has\":{\"bar\":\"foo\"},\"name\":\"c2\"},\"name\":\"c1\"}", result);
    }
    
    @Test
    public void testDeeperChain() {
        testInstances.forEach(jsonb -> checkDeeperChain(jsonb));
    }
    
    private void checkDeeperChain(Jsonb jsonb) {
        Foo foo = new Foo("foo");
        Chain c1 = new Chain("c1");
        Chain c2 = new Chain("c2");
        Chain c3 = new Chain("c3");
        c1.setLinksTo(c2);
        c1.setHas(foo);
        c2.setHas(foo);
        c2.setLinksTo(c3);
        String result = jsonb.toJson(c1);
        assertEquals("{\"has\":{\"bar\":\"foo\"},\"linksTo\":{\"has\":{\"bar\":\"foo\"},\"linksTo\":{\"name\":\"c3\"},\"name\":\"c2\"},\"name\":\"c1\"}", result);
    }

    public static class A {
        public Foo ref1;
        public Foo ref2;
    }

}
