/*
 * Copyright (c) 2016, 2023 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.yasson.internal.cdi;

import org.junit.jupiter.api.*;

import static org.eclipse.yasson.Jsonbs.testWithJsonbBuilderCreate;
import static org.junit.jupiter.api.Assertions.*;

import org.eclipse.yasson.internal.components.JsonbComponentInstanceCreatorFactory;

import jakarta.enterprise.inject.spi.Bean;
import jakarta.enterprise.inject.spi.BeanManager;
import jakarta.enterprise.inject.spi.CDI;
import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.JsonbConfig;
import jakarta.json.bind.config.PropertyVisibilityStrategy;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * @author Roman Grigoriadi
 */
public class CdiInjectionTest {

    @Test
    public void testInjectionAndCleanup() throws Exception {
        WeldManager weldManager = new WeldManager();
        weldManager.startWeld(CalledMethods.class, CdiTestService.class, HelloService1.class, HelloService2.class);

        Jsonb jsonb = JsonbBuilder.create();
        final String result = jsonb.toJson(new AdaptedPojo());
        jsonb.close();
        assertEquals("{\"adaptedValue1\":1111,\"adaptedValue2\":1001,\"adaptedValue3\":1010}", result);

        //HelloService1 is @ApplicationScoped
        CalledMethods calledMethods = getCalledMethods();
        Map<String, Integer> results = calledMethods.getResults();

        assertTrue(results.containsKey(HelloService1.class.getName() + ".sayHello"));
        assertFalse(results.containsKey(HelloService1.class.getName() + ".preDestroy"));

        //HelloService2 is @Dependent
        assertTrue(results.containsKey(HelloService2.class.getName() + ".sayHello"));
        assertTrue(results.containsKey(HelloService2.class.getName() + ".preDestroy"));

        //CdiTestService is @ApplicationScoped
        assertTrue(results.containsKey(CdiTestService.class.getName() + ".runService"));
        assertFalse(results.containsKey(CdiTestService.class.getName() + ".preDestroy"));
//        getCalledMethods().printCalled();


        weldManager.shutdownWeld();

        assertTrue(results.containsKey(CdiTestService.class.getName() + ".preDestroy"));
        assertTrue(results.containsKey(HelloService1.class.getName() + ".preDestroy"));

//        getCalledMethods().printCalled();
    }

    @Test
    public void testInJndiEnvironment() throws NamingException {
        InitialContext context = new InitialContext();
        context.bind(JsonbComponentInstanceCreatorFactory.BEAN_MANAGER_NAME, new JndiBeanManager());

        try {
            testWithJsonbBuilderCreate(jsonb ->
                assertEquals("{\"adaptedValue1\":1111,\"adaptedValue2\":1001,\"adaptedValue3\":1010}", jsonb.toJson(new AdaptedPojo())));
        } finally {
            context.unbind(JsonbComponentInstanceCreatorFactory.BEAN_MANAGER_NAME);
        }
	}

    @Test
    public void testNonCdiEnvironment() {
        JsonbConfig config = new JsonbConfig()
        //allow only field with components that doesn't has cdi dependencies.
            .withPropertyVisibilityStrategy(new PropertyVisibilityStrategy() {
                @Override
                public boolean isVisible(Field field) {
                    return "adaptedValue3".equals(field.getName());
                }

                @Override
                public boolean isVisible(Method method) {
                    return false;
                }
        });
        testWithJsonbBuilderCreate(config, jsonb -> assertEquals("{\"adaptedValue3\":1010}", jsonb.toJson(new AdaptedPojo())));
    }

    private CalledMethods getCalledMethods() {
        final BeanManager beanManager = CDI.current().getBeanManager();
        final Bean<?> resolve = beanManager.resolve(beanManager.getBeans(CalledMethods.class));
        return (CalledMethods) beanManager.getReference(resolve, CalledMethods.class, beanManager.createCreationalContext(resolve));
    }


}
