package org.eclipse.yasson.defaultmapping;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import org.eclipse.yasson.serializers.model.Crate;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.JsonbConfig;
import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.config.PropertyVisibilityStrategy;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.math.BigDecimal;

/**
 * Created by Roman Grigoriadi (roman.grigoriadi@oracle.com) on 28/04/2017.
 */
public class SecurityManagerTest {

    static final String classesDir = SecurityManagerTest.class.getProtectionDomain().getCodeSource().getLocation().getFile();

    @Before
    public void setUp() {
        System.setProperty("java.security.policy", classesDir + "test.policy");
        System.setProperty("java.security.debug", "failure");
        System.setSecurityManager(new SecurityManager());
    }

    @After
    public void tearDown() {
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
