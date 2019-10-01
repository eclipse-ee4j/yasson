/*******************************************************************************
 * Copyright (c) 2016, 2019 Oracle and/or its affiliates. All rights reserved.
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

package org.eclipse.yasson.adapters;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import org.eclipse.yasson.TestTypeToken;
import org.eclipse.yasson.adapters.model.*;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbException;
import javax.json.bind.adapter.JsonbAdapter;
import javax.json.bind.annotation.JsonbTypeAdapter;

/**
 * @author Roman Grigoriadi
 */
public class JsonbTypeAdapterTest {

    private Jsonb jsonb;

    public static class BoxToStringAdapter implements JsonbAdapter<Box, String> {

        @Override
        public Box adaptFromJson(String obj) throws Exception {
            String[] strings = obj.split(":");
            return new Box(strings[0], Integer.parseInt(strings[1]));
        }

        @Override
        public String adaptToJson(Box obj) throws Exception {
            return obj.getBoxStrField()+":"+obj.getBoxIntegerField();
        }
    }

    public static class IncompatibleAdapterPojo<T,X> {

        @JsonbTypeAdapter(BoxToStringAdapter.class)
        public String str;

    }

    public static class AnnotatedPojo<T,X> {

        @JsonbTypeAdapter(BoxToStringAdapter.class)
        public Box box;
        @JsonbTypeAdapter(BoxToCratePropagatedIntegerStringAdapter.class)
        public GenericBox<T> tBox;
        @JsonbTypeAdapter(BoxToCratePropagatedIntegerStringAdapter.class)
        public GenericBox<X> xBox;
    }

    @Before
    public void setUp() throws Exception {
        jsonb = JsonbBuilder.create();
    }

    @Test
    public void testIncompatibleAdapter() throws Exception {

        IncompatibleAdapterPojo incompatibleAdapterFieldPojo = new IncompatibleAdapterPojo();
        incompatibleAdapterFieldPojo.str = "STR";
        try {
            jsonb.toJson(incompatibleAdapterFieldPojo);
            fail();
        } catch (JsonbException e) {
            assertTrue(e.getMessage().startsWith("Adapter of runtime type class"));
            assertTrue(e.getMessage().contains("does not match property type "));
        }

    }

    @Test
    public void testGenericFieldsMatch() {
        AnnotatedPojo<Integer, String> annotatedPojo = new AnnotatedPojo<>();
        annotatedPojo.tBox = new GenericBox<>("T_BOX", 110);
        String marshalledJson = jsonb.toJson(annotatedPojo, new TestTypeToken<AnnotatedPojo<Integer, String>>(){}.getType());
        assertEquals("{\"tBox\":{\"adaptedT\":{\"x\":[\"110\"]},\"crateStrField\":\"T_BOX\"}}", marshalledJson);

        AnnotatedPojo<Integer,String> result = jsonb.fromJson("{\"tBox\":{\"crateStrField\":\"T_BOX\",\"adaptedT\":{\"x\":[\"110\"]}}}", new TestTypeToken<AnnotatedPojo<Integer,String>>(){}.getType());
        assertEquals("T_BOX", result.tBox.getStrField());
        assertEquals(Integer.valueOf(110), result.tBox.getX());
    }

    @Test
    public void testAnnotatedTbox() throws Exception {

        AnnotatedPojo pojo = new AnnotatedPojo();
        pojo.box = new Box("STR", 101);
        String marshalledJson = jsonb.toJson(pojo);
        assertEquals("{\"box\":\"STR:101\"}", marshalledJson);

        AnnotatedPojo<?, ?> result = jsonb.fromJson("{\"box\":\"STR:110\"}", AnnotatedPojo.class);
        assertEquals("STR", result.box.getBoxStrField());
        assertEquals(Integer.valueOf(110), result.box.getBoxIntegerField());
    }

    @Test
    public void testBoxWithTypeAdapter() {
        BoxWithAdapter boxWithAdapter = new BoxWithAdapter("STR", 101);
        String marshalledJson = jsonb.toJson(boxWithAdapter);
        assertEquals("{\"boxInteger\":101,\"boxStr\":\"STR\"}", marshalledJson);

        BoxWithAdapter result = jsonb.fromJson("{\"boxInteger\":101,\"boxStr\":\"STR\"}", BoxWithAdapter.class);
        assertEquals("STR", result.getBoxStrField());
        assertEquals(Integer.valueOf(101), result.getBoxIntegerField());
    }

    @Test
    public void testBoxWithTypeSerializer() {
        BoxWithSerializer boxWithSerializer = new BoxWithSerializer("STR", 101);
        String marshalledJson = jsonb.toJson(boxWithSerializer);
        assertEquals("{\"boxInteger\":101,\"boxStr\":\"STR\"}", marshalledJson);
    }

    @Test
    public void testBoxWithTypeDeserializer() {
        BoxWithDeserializer result = jsonb.fromJson("{\"boxInteger\":101,\"boxStr\":\"STR\"}", BoxWithDeserializer.class);
        assertEquals("STR", result.getBoxStrField());
        assertEquals(Integer.valueOf(101), result.getBoxIntegerField());
    }

}
