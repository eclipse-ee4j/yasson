/*
 * Copyright (c) 2018, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.yasson;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.eclipse.yasson.Jsonbs.*;

import org.eclipse.yasson.internal.JsonbContext;

import jakarta.json.bind.JsonbConfig;
import jakarta.json.bind.annotation.JsonbProperty;
import jakarta.json.spi.JsonProvider;
import java.lang.reflect.Method;

/**
 *
 * @author Maxence Laurent
 */
public class DefaultGetterInInterface {

    public static interface Defaulted {

        default public String getGetterA() {
            return "valueA";
        }
    }

    public static class PojoWithDefault implements Defaulted {
    }

    @Test
    public void testWithDefault() {
        PojoWithDefault pojo = new PojoWithDefault();
        String result = defaultJsonb.toJson(pojo);
        assertEquals("{\"getterA\":\"valueA\"}", result);
    }

    public static interface WithGetterI {

        @JsonbProperty("withGetterI")
        String getGetterI();
    }

    public static interface WithDefaultGetterI extends WithGetterI {

        @Override
        @JsonbProperty("default")
        default String getGetterI() {
            return "default";
        }
    }

    public static interface OtherWithDefaultGetterI extends WithGetterI {

        @Override
        @JsonbProperty("otherDefault")
        default String getGetterI() {
            return "otherDefault";
        }
    }

    public static class Pojo implements WithGetterI {

        @Override
        @JsonbProperty("implementation")
        public String getGetterI() {
            return "implementation";
        }
    }


    public static class PojoNoAnnotation implements WithGetterI {

        @Override
        public String getGetterI() {
            return "withGetterI";
        }
    }

    public static class PojoWithDefaultSuperImplementation extends Pojo implements WithDefaultGetterI {
    }

    public static class PojoWithDefaultImplementation implements WithDefaultGetterI {

        @Override
        @JsonbProperty("defaultImplementation")
        public String getGetterI() {
            return "defaultImplementation";
        }

    }

    public static class PojoWithDefaultOnly implements WithDefaultGetterI {
    }

    public static class PojoGetterDefaultedTwice extends PojoWithDefaultImplementation implements OtherWithDefaultGetterI {
    }

    @Test
    public void testWithInheritedAndDefault() throws NoSuchMethodException {
        JsonbContext jsonbContext = new JsonbContext(new JsonbConfig(), JsonProvider.provider());

        // direct implementation only (no default implementation)
        Method pojoGetter = jsonbContext.getMappingContext().getOrCreateClassModel(Pojo.class).getPropertyModel("getterI").getGetter();

        // default implementation
        Method pojoDefaultOnlyGetter = jsonbContext.getMappingContext().getOrCreateClassModel(PojoWithDefaultOnly.class).getPropertyModel("getterI").getGetter();

        // default implementation overriden by super class
        Method pojoDefaultSuperGetter = jsonbContext.getMappingContext().getOrCreateClassModel(PojoWithDefaultSuperImplementation.class).getPropertyModel("getterI").getGetter();

        // default implementation
        Method pojoDefaultImplementationGetter = jsonbContext.getMappingContext().getOrCreateClassModel(PojoWithDefaultImplementation.class).getPropertyModel("getterI").getGetter();

        // two default implementations
        Method pojoTwoDefault = jsonbContext.getMappingContext().getOrCreateClassModel(PojoGetterDefaultedTwice.class).getPropertyModel("getterI").getGetter();

        // assert getters selected by ClassParser
        assertEquals(Pojo.class.getMethod("getGetterI"), pojoGetter);
        assertEquals(WithDefaultGetterI.class.getMethod("getGetterI"), pojoDefaultOnlyGetter);
        assertEquals(Pojo.class.getMethod("getGetterI"), pojoDefaultSuperGetter);
        assertEquals(PojoWithDefaultImplementation.class.getMethod("getGetterI"), pojoDefaultImplementationGetter);
        assertEquals(PojoWithDefaultImplementation.class.getMethod("getGetterI"), pojoTwoDefault);

        // assert serialized json is correct, including property name as specified by JsonbProperty annotations
        assertJson(new Pojo(), "implementation");
        assertJson(new PojoNoAnnotation(), "withGetterI");
        assertJson(new PojoWithDefaultOnly(), "default");
        assertJson(new PojoWithDefaultSuperImplementation(), "implementation");
        assertJson(new PojoWithDefaultImplementation(), "defaultImplementation");
        assertJson(new PojoGetterDefaultedTwice(), "defaultImplementation");
    }

    private static void assertJson(WithGetterI pojo, String expected){
        assertEquals(expected, pojo.getGetterI());
        assertEquals("{\"" + expected + "\":\"" + pojo.getGetterI() + "\"}", defaultJsonb.toJson(pojo));
    }
}
