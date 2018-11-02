/*******************************************************************************
 * Copyright (c) 2018 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *      Maxence Laurent
 ******************************************************************************/
package org.eclipse.yasson;

import org.eclipse.yasson.internal.JsonbContext;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import javax.json.bind.annotation.JsonbProperty;
import javax.json.spi.JsonProvider;
import java.lang.reflect.Method;

import static org.junit.Assert.assertEquals;

/**
 *
 * @author Maxence Laurent
 */
public class DefaultGetterInInterface {

    private Jsonb jsonb;

    @Before
    public void before() {
        jsonb = JsonbBuilder.create();
    }

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
        String result = jsonb.toJson(pojo);
        Assert.assertEquals("{\"getterA\":\"valueA\"}", result);
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
        Method pojoGetter = jsonbContext.getMappingContext().getOrCreateClassModel(Pojo.class).getPropertyModel("getterI").getPropagation().getGetter();

        // default implementation
        Method pojoDefaultOnlyGetter = jsonbContext.getMappingContext().getOrCreateClassModel(PojoWithDefaultOnly.class).getPropertyModel("getterI").getPropagation().getGetter();

        // default implementation overriden by super class
        Method pojoDefaultSuperGetter = jsonbContext.getMappingContext().getOrCreateClassModel(PojoWithDefaultSuperImplementation.class).getPropertyModel("getterI").getPropagation().getGetter();

        // default implementation
        Method pojoDefaultImplementationGetter = jsonbContext.getMappingContext().getOrCreateClassModel(PojoWithDefaultImplementation.class).getPropertyModel("getterI").getPropagation().getGetter();

        // two default implementations
        Method pojoTwoDefault = jsonbContext.getMappingContext().getOrCreateClassModel(PojoGetterDefaultedTwice.class).getPropertyModel("getterI").getPropagation().getGetter();

        // assert getters selected by ClassParser
        assertEquals(Pojo.class.getMethod("getGetterI"), pojoGetter);
        assertEquals(WithDefaultGetterI.class.getMethod("getGetterI"), pojoDefaultOnlyGetter);
        assertEquals(Pojo.class.getMethod("getGetterI"), pojoDefaultSuperGetter);
        assertEquals(PojoWithDefaultImplementation.class.getMethod("getGetterI"), pojoDefaultImplementationGetter);
        assertEquals(PojoWithDefaultImplementation.class.getMethod("getGetterI"), pojoTwoDefault);

        // assert serialized json is correct, including property name as specified by JsonbProperty annotations
        this.assertJson(new Pojo(), "implementation");
        this.assertJson(new PojoNoAnnotation(), "withGetterI");
        this.assertJson(new PojoWithDefaultOnly(), "default");
        this.assertJson(new PojoWithDefaultSuperImplementation(), "implementation");
        this.assertJson(new PojoWithDefaultImplementation(), "defaultImplementation");
        this.assertJson(new PojoGetterDefaultedTwice(), "defaultImplementation");
    }

    private void assertJson(WithGetterI pojo, String expected){
        assertEquals(expected, pojo.getGetterI());
        assertEquals("{\"" + expected + "\":\"" + pojo.getGetterI() + "\"}", jsonb.toJson(pojo));
    }
}
