/*
 * Copyright (c) 2019, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.yasson.defaultmapping;

import org.junit.jupiter.api.*;

import org.eclipse.yasson.serializers.model.Crate;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;
import jakarta.json.bind.annotation.JsonbProperty;
import jakarta.json.bind.config.PropertyVisibilityStrategy;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;

/**
 * Created by Roman Grigoriadi (roman.grigoriadi@oracle.com) on 28/04/2017.
 */
public class SecurityManagerTest {

    static final String classesDir = SecurityManagerTest.class.getProtectionDomain().getCodeSource().getLocation().getFile();

    @BeforeAll
    public static void setUp() {
        System.setProperty("java.security.policy", classesDir + "test.policy");
        System.setProperty("java.security.debug", "failure");
        System.setSecurityManager(new SecurityManager());
    }

    @AfterAll
    public static void tearDown() {
        System.setSecurityManager(null);
    }

    @Test
    public void testWithSecurityManager() {
        Jsonb jsonb = JsonbBuilder.create(new JsonbConfig().withPropertyVisibilityStrategy(new PropertyVisibilityStrategy() {
            @Override
            public boolean isVisible(Field field) {
                return Modifier.isPublic(field.getModifiers()) || field.getName().equals("privateProperty");
            }

            @Override
            public boolean isVisible(Method method) {
                return Modifier.isPublic(method.getModifiers());
            }
        }));

        Pojo pojo = new Pojo();
        pojo.setStrProperty("string propery");
        Crate crate = new Crate();
        crate.crateBigDec = BigDecimal.TEN;
        crate.crateStr = "crate string";
        pojo.setCrate(crate);

        String result = jsonb.toJson(pojo);
    }



    public static class Pojo {

        //causes .setAccessible(true) in combination with custom visibility strategy
        private String privateProperty;

        @JsonbProperty("property1")
        private String strProperty;

        @JsonbProperty("property2")
        private Crate crate;

        public String getStrProperty() {
            return strProperty;
        }

        public void setStrProperty(String strProperty) {
            this.strProperty = strProperty;
        }

        public Crate getCrate() {
            return crate;
        }

        public void setCrate(Crate crate) {
            this.crate = crate;
        }
    }
}
