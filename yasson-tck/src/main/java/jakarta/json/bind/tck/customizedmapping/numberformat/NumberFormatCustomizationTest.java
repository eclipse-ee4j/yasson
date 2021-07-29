/*
 * Copyright (c) 2017, 2018 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0, which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the
 * Eclipse Public License v. 2.0 are satisfied: GNU General Public License,
 * version 2 with the GNU Classpath Exception, which is available at
 * https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 */

/*
 * $Id$
 */

package jakarta.json.bind.tck.customizedmapping.numberformat;

import static org.junit.Assert.fail;

import java.lang.invoke.MethodHandles;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

import org.jboss.arquillian.container.test.api.Deployment;
import org.jboss.arquillian.junit.Arquillian;
import org.jboss.shrinkwrap.api.ShrinkWrap;
import org.jboss.shrinkwrap.api.spec.WebArchive;
import org.junit.Test;
import org.junit.runner.RunWith;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import jakarta.json.bind.tck.customizedmapping.numberformat.model.AccessorCustomizedDoubleContainer;
import jakarta.json.bind.tck.customizedmapping.numberformat.model.FieldCustomizedDoubleContainer;
import jakarta.json.bind.tck.customizedmapping.numberformat.model.TypeCustomizedDoubleContainer;
import jakarta.json.bind.tck.customizedmapping.numberformat.model.TypeCustomizedFieldOverriddenDoubleContainer;
import jakarta.json.bind.tck.customizedmapping.numberformat.model.customized.PackageCustomizedDoubleContainer;
import jakarta.json.bind.tck.customizedmapping.numberformat.model.customized.PackageCustomizedTypeOverriddenDoubleContainer;
import jakarta.json.bind.tck.customizedmapping.numberformat.model.customized.PackageCustomizedTypeOverriddenFieldOverriddenDoubleContainer;

/**
 * This is just temporal work around for failing test testNumberFormatField.
 *
 * It is failing due to changed FR number format separator.
 **/
@RunWith(Arquillian.class)
public class NumberFormatCustomizationTest {

    @Deployment
    public static WebArchive createTestArchive() {
        return ShrinkWrap.create(WebArchive.class)
                .addPackages(true, MethodHandles.lookup().lookupClass().getPackage().getName());
    }

    private static final String FRENCH_NUMBER = "\"123\\u00a0456,789\"";

    private final Jsonb jsonb = JsonbBuilder.create();

    /*
     * @testName: testNumberFormatPackage
     *
     * @assertion_ids: JSONB:SPEC:JSB-4.9-1
     *
     * @test_Strategy: Assert that package annotation with JsonbNumberFormat is
     * correctly applied
     */
    @Test
    public void testNumberFormatPackage() {
        String jsonString = jsonb.toJson(new PackageCustomizedDoubleContainer() {
            {
                setInstance(123456.789);
            }
        });
        if (!jsonString
                .matches("\\{\\s*\"instance\"\\s*:\\s*\"123.456,8\"\\s*\\}")) {
            fail(
                    "Failed to correctly customize number format during marshalling using JsonbNumberFormat annotation on package.");
        }

        PackageCustomizedDoubleContainer unmarshalledObject = jsonb.fromJson(
                "{ \"instance\" : \"123.456,789\" }",
                PackageCustomizedDoubleContainer.class);
        if (unmarshalledObject.getInstance() != 123456.789) {
            fail(
                    "Failed to correctly customize number format during unmarshalling using JsonbNumberFormat annotation on package.");
        }

        return; // passed
    }

    /*
     * @testName: testNumberFormatType
     *
     * @assertion_ids: JSONB:SPEC:JSB-4.9-1
     *
     * @test_Strategy: Assert that type annotation with JsonbNumberFormat is
     * correctly applied
     */
    @Test
    public void testNumberFormatType() {
        String jsonString = jsonb.toJson(new TypeCustomizedDoubleContainer() {
            {
                setInstance(123456.789);
            }
        });
        if (!jsonString
                .matches("\\{\\s*\"instance\"\\s*:\\s*\"123,456.79\"\\s*\\}")) {
            fail(
                    "Failed to correctly customize number format during marshalling using JsonbNumberFormat annotation on type.");
        }

        TypeCustomizedDoubleContainer unmarshalledObject = jsonb.fromJson(
                "{ \"instance\" : \"123,456.789\" }",
                TypeCustomizedDoubleContainer.class);
        if (unmarshalledObject.getInstance() != 123456.789) {
            fail(
                    "Failed to correctly customize number format during unmarshalling using JsonbNumberFormat annotation on type.");
        }

        return; // passed
    }

    /*
     * @testName: testNumberFormatField
     *
     * @assertion_ids: JSONB:SPEC:JSB-4.9-1
     *
     * @test_Strategy: Assert that field annotation with JsonbNumberFormat is
     * correctly applied
     */
    @Test
    public void testNumberFormatField() {
        //Franch group separator has been changed in JDK 13 and it is now backwords incompatible.
        char frenchGroupingSeparator = DecimalFormatSymbols.getInstance(Locale.FRENCH).getGroupingSeparator();
        String jsonString = jsonb.toJson(new FieldCustomizedDoubleContainer() {
            {
                setInstance(123456.789);
            }
        });
        if (!jsonString
                .matches("\\{\\s*\"instance\"\\s*:\\s*\"123"+frenchGroupingSeparator+"456,789\"\\s*\\}")) {
            fail(
                    "Failed to correctly customize number format during marshalling using JsonbNumberFormat annotation on field.");
        }

        FieldCustomizedDoubleContainer unmarshalledObject = jsonb.fromJson(
                "{ \"instance\" : " + FRENCH_NUMBER + " }",
                FieldCustomizedDoubleContainer.class);
        if (unmarshalledObject.getInstance() != 123456.789) {
            fail(
                    "Failed to correctly customize number format during unmarshalling using JsonbNumberFormat annotation on field.");
        }

        return; // passed
    }

    /*
     * @testName: testNumberFormatAccessors
     *
     * @assertion_ids: JSONB:SPEC:JSB-4.9-1
     *
     * @test_Strategy: Assert that accessor annotation with JsonbNumberFormat is
     * correctly individually applied for marshalling and unmarshalling
     */
    @Test
    public void testNumberFormatAccessors() {
        String jsonString = jsonb.toJson(new AccessorCustomizedDoubleContainer() {
            {
                setInstance(123456.789);
            }
        });
        if (!jsonString
                .matches("\\{\\s*\"instance\"\\s*:\\s*\"123,456.79\"\\s*\\}")) {
            fail(
                    "Failed to correctly customize number format during marshalling using JsonbNumberFormat annotation on getter.");
        }

        AccessorCustomizedDoubleContainer unmarshalledObject = jsonb.fromJson(
                "{ \"instance\" : " + FRENCH_NUMBER + " }",
                AccessorCustomizedDoubleContainer.class);
        if (unmarshalledObject.getInstance() != 123456.789) {
            fail(
                    "Failed to correctly customize number format during unmarshalling using JsonbNumberFormat annotation on setter.");
        }

        return; // passed
    }

    /*
     * @testName: testNumberFormatPackageTypeOverride
     *
     * @assertion_ids: JSONB:SPEC:JSB-4.9-1; JSONB:SPEC:JSB-4.9-2
     *
     * @test_Strategy: Assert that package annotation with JsonbNumberFormat is
     * correctly overridden by type annotation with JsonbNumberFormat
     */
    @Test
    public void testNumberFormatPackageTypeOverride() {
        String jsonString = jsonb
                .toJson(new PackageCustomizedTypeOverriddenDoubleContainer() {
                    {
                        setInstance(123456.789);
                    }
                });
        if (!jsonString
                .matches("\\{\\s*\"instance\"\\s*:\\s*\"123,456.79\"\\s*\\}")) {
            fail(
                    "Failed to correctly override number format customization using JsonbNumberFormat annotation on package during marshalling using JsonbNumberFormat annotation on type.");
        }

        PackageCustomizedTypeOverriddenDoubleContainer unmarshalledObject = jsonb
                .fromJson("{ \"instance\" : \"123,456.789\" }",
                          PackageCustomizedTypeOverriddenDoubleContainer.class);
        if (unmarshalledObject.getInstance() != 123456.789) {
            fail(
                    "Failed to correctly override number format customization using JsonbNumberFormat annotation on package during unmarshalling using JsonbNumberFormat annotation on type.");
        }

        return; // passed
    }

    /*
     * @testName: testNumberFormatTypeFieldOverride
     *
     * @assertion_ids: JSONB:SPEC:JSB-4.9-1; JSONB:SPEC:JSB-4.9-2
     *
     * @test_Strategy: Assert that type annotation with JsonbNumberFormat is
     * correctly overridden by field annotation with JsonbNumberFormat
     */
    @Test
    public void testNumberFormatTypeFieldOverride() {
        String jsonString = jsonb
                .toJson(new TypeCustomizedFieldOverriddenDoubleContainer() {
                    {
                        setInstance(123456.789);
                    }
                });
        if (!jsonString
                .matches("\\{\\s*\"instance\"\\s*:\\s*\"123,456.8\"\\s*\\}")) {
            fail(
                    "Failed to correctly customize number format during marshalling using JsonbNumberFormat annotation on type.");
        }

        TypeCustomizedFieldOverriddenDoubleContainer unmarshalledObject = jsonb
                .fromJson("{ \"instance\" : \"123,456.789\" }",
                          TypeCustomizedFieldOverriddenDoubleContainer.class);
        if (unmarshalledObject.getInstance() != 123456.789) {
            fail(
                    "Failed to correctly customize number format during unmarshalling using JsonbNumberFormat annotation on type.");
        }

        return; // passed
    }

    /*
     * @testName: testNumberFormatPackageTypeOverrideFieldOverride
     *
     * @assertion_ids: JSONB:SPEC:JSB-4.9-1; JSONB:SPEC:JSB-4.9-2
     *
     * @test_Strategy: Assert that package and type annotation with
     * JsonbNumberFormat is correctly overridden by field annotation with
     * JsonbNumberFormat
     */
    @Test
    public void testNumberFormatPackageTypeOverrideFieldOverride() {
        String jsonString = jsonb.toJson(
                new PackageCustomizedTypeOverriddenFieldOverriddenDoubleContainer() {
                    {
                        setInstance(123456.789);
                    }
                });
        if (!jsonString
                .matches("\\{\\s*\"instance\"\\s*:\\s*\"123.456,789\"\\s*\\}")) {
            fail(
                    "Failed to correctly override number format customization using JsonbNumberFormat annotation on package during marshalling using JsonbNumberFormat annotation on type.");
        }

        PackageCustomizedTypeOverriddenFieldOverriddenDoubleContainer unmarshalledObject = jsonb
                .fromJson("{ \"instance\" : \"123.456,789\" }",
                          PackageCustomizedTypeOverriddenFieldOverriddenDoubleContainer.class);
        if (unmarshalledObject.getInstance() != 123456.789) {
            fail(
                    "Failed to correctly override number format customization using JsonbNumberFormat annotation on package during unmarshalling using JsonbNumberFormat annotation on type.");
        }

        return; // passed
    }
}
