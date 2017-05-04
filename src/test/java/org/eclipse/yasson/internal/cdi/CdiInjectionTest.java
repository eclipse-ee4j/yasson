/*******************************************************************************
 * Copyright (c) 2016 Oracle and/or its affiliates. All rights reserved.
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

package org.eclipse.yasson.internal.cdi;

import org.junit.Test;

import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.enterprise.inject.spi.CDI;
import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import javax.json.bind.config.PropertyVisibilityStrategy;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Map;

import static org.junit.Assert.*;

/**
 * @author Roman Grigoriadi
 */
public class CdiInjectionTest {

    private CalledMethods calledMethods;

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
    public void testNonCdiEnvironment() {
        JsonbConfig config = new JsonbConfig();
        //allow only field with adapter that doesn't has cdi dependencies.
        config.withPropertyVisibilityStrategy(new PropertyVisibilityStrategy() {
            @Override
            public boolean isVisible(Field field) {
                return "adaptedValue3".equals(field.getName());
            }

            @Override
            public boolean isVisible(Method method) {
                return false;
            }
        });
        Jsonb jsonb = JsonbBuilder.create(config);
        final String result = jsonb.toJson(new AdaptedPojo());
        assertEquals("{\"adaptedValue3\":1010}", result);
    }

    private CalledMethods getCalledMethods() {
        final BeanManager beanManager = CDI.current().getBeanManager();
        final Bean<?> resolve = beanManager.resolve(beanManager.getBeans(CalledMethods.class));
        return (CalledMethods) beanManager.getReference(resolve, CalledMethods.class, beanManager.createCreationalContext(resolve));
    }

}
