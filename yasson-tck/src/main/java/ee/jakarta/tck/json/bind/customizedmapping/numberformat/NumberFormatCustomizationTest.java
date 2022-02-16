/*
 * Copyright (c) 2017, 2022 Oracle and/or its affiliates. All rights reserved.
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

package ee.jakarta.tck.json.bind.customizedmapping.numberformat;

import java.text.DecimalFormatSymbols;
import java.util.Locale;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;

import ee.jakarta.tck.json.bind.customizedmapping.numberformat.model.AccessorCustomizedDoubleContainer;
import ee.jakarta.tck.json.bind.customizedmapping.numberformat.model.FieldCustomizedDoubleContainer;
import ee.jakarta.tck.json.bind.customizedmapping.numberformat.model.TypeCustomizedDoubleContainer;
import ee.jakarta.tck.json.bind.customizedmapping.numberformat.model.TypeCustomizedFieldOverriddenDoubleContainer;
import ee.jakarta.tck.json.bind.customizedmapping.numberformat.model.customized.PackageCustomizedDoubleContainer;
import ee.jakarta.tck.json.bind.customizedmapping.numberformat.model.customized.PackageCustomizedTypeOverriddenDoubleContainer;
import ee.jakarta.tck.json.bind.customizedmapping.numberformat.model.customized.PackageCustomizedTypeOverriddenFieldOverriddenDoubleContainer;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.matchesPattern;

/**
 * @test
 * @sources NumberFormatCustomizationTest.java
 * @executeClass com.sun.ts.tests.jsonb.customizedmapping.numberformat.NumberFormatCustomizationTest
 **/
public class NumberFormatCustomizationTest {

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
        String jsonString = jsonb.toJson(new PackageCustomizedDoubleContainer() {{
            setInstance(123456.789);
        }});
        assertThat("Failed to correctly customize number format during marshalling using JsonbNumberFormat annotation on "
                           + "package.",
                   jsonString, matchesPattern("\\{\\s*\"instance\"\\s*:\\s*\"123.456,8\"\\s*\\}"));

        PackageCustomizedDoubleContainer unmarshalledObject = jsonb.fromJson("{ \"instance\" : \"123.456,789\" }",
                                                                             PackageCustomizedDoubleContainer.class);

        assertThat(
                "Failed to correctly customize number format during unmarshalling using JsonbNumberFormat annotation on package.",
                unmarshalledObject.getInstance(),
                is(123456.789));
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
        String jsonString = jsonb.toJson(new TypeCustomizedDoubleContainer() {{
            setInstance(123456.789);
        }});
        assertThat("Failed to correctly customize number format during marshalling using JsonbNumberFormat annotation on type.",
                   jsonString, matchesPattern("\\{\\s*\"instance\"\\s*:\\s*\"123,456.79\"\\s*\\}"));

        TypeCustomizedDoubleContainer unmarshalledObject = jsonb.fromJson("{ \"instance\" : \"123,456.789\" }",
                                                                          TypeCustomizedDoubleContainer.class);
        assertThat("Failed to correctly customize number format during unmarshalling using JsonbNumberFormat annotation on type.",
                   unmarshalledObject.getInstance(), is(123456.789));

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
        char separator = DecimalFormatSymbols.getInstance(Locale.FRENCH).getGroupingSeparator();
        String jsonString = jsonb.toJson(new FieldCustomizedDoubleContainer() {{
            setInstance(123456.789);
        }});
        assertThat("Failed to correctly customize number format during marshalling using JsonbNumberFormat annotation on field.",
                   jsonString, matchesPattern("\\{\\s*\"instance\"\\s*:\\s*\"123" + separator + "456,789\"\\s*\\}"));

        FieldCustomizedDoubleContainer unmarshalledObject = jsonb.fromJson("{ \"instance\" : " + FRENCH_NUMBER + " }",
                                                                           FieldCustomizedDoubleContainer.class);
        assertThat("Failed to correctly customize number format during unmarshalling using JsonbNumberFormat annotation on "
                           + "field.",
                   unmarshalledObject.getInstance(), is(123456.789));
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
        String jsonString = jsonb.toJson(new AccessorCustomizedDoubleContainer() {{
            setInstance(123456.789);
        }});
        assertThat("Failed to correctly customize number format during marshalling using JsonbNumberFormat annotation on getter.",
                   jsonString, matchesPattern("\\{\\s*\"instance\"\\s*:\\s*\"123,456.79\"\\s*\\}"));

        AccessorCustomizedDoubleContainer unmarshalledObject = jsonb.fromJson("{ \"instance\" : " + FRENCH_NUMBER + " }",
                                                                              AccessorCustomizedDoubleContainer.class);
        assertThat(
                "Failed to correctly customize number format during unmarshalling using JsonbNumberFormat annotation on setter.",
                unmarshalledObject.getInstance(),
                is(123456.789));
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
        String jsonString = jsonb.toJson(new PackageCustomizedTypeOverriddenDoubleContainer() {{
            setInstance(123456.789);
        }});
        assertThat("Failed to correctly override number format customization using JsonbNumberFormat annotation on "
                           + "package during marshalling using JsonbNumberFormat annotation on type.",
                   jsonString, matchesPattern("\\{\\s*\"instance\"\\s*:\\s*\"123,456.79\"\\s*\\}"));

        PackageCustomizedTypeOverriddenDoubleContainer unmarshalledObject = jsonb.fromJson("{ \"instance\" : \"123,456.789\" }",
                                                                                           PackageCustomizedTypeOverriddenDoubleContainer.class);
        assertThat("Failed to correctly override number format customization using JsonbNumberFormat annotation on "
                           + "package during unmarshalling using JsonbNumberFormat annotation on type.",
                   unmarshalledObject.getInstance(), is(123456.789));
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
        String jsonString = jsonb.toJson(new TypeCustomizedFieldOverriddenDoubleContainer() {{
            setInstance(123456.789);
        }});
        assertThat("Failed to correctly customize number format during marshalling using JsonbNumberFormat annotation on type.",
                   jsonString, matchesPattern("\\{\\s*\"instance\"\\s*:\\s*\"123,456.8\"\\s*\\}"));

        TypeCustomizedFieldOverriddenDoubleContainer unmarshalledObject = jsonb.fromJson("{ \"instance\" : \"123,456.789\" }",
                                                                                         TypeCustomizedFieldOverriddenDoubleContainer.class);
        assertThat("Failed to correctly customize number format during unmarshalling using JsonbNumberFormat annotation on type.",
                   unmarshalledObject.getInstance(), is(123456.789));
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
        String jsonString = jsonb.toJson(new PackageCustomizedTypeOverriddenFieldOverriddenDoubleContainer() {{
            setInstance(123456.789);
        }});
        assertThat("Failed to correctly override number format customization using JsonbNumberFormat annotation on "
                           + "package during marshalling using JsonbNumberFormat annotation on type.",
                   jsonString, matchesPattern("\\{\\s*\"instance\"\\s*:\\s*\"123.456,789\"\\s*\\}"));

        PackageCustomizedTypeOverriddenFieldOverriddenDoubleContainer unmarshalledObject =
                jsonb.fromJson("{ \"instance\" : \"123.456,789\" }",
                               PackageCustomizedTypeOverriddenFieldOverriddenDoubleContainer.class);
        assertThat("Failed to correctly override number format customization using JsonbNumberFormat annotation on "
                           + "package during unmarshalling using JsonbNumberFormat annotation on type.",
                   unmarshalledObject.getInstance(), is(123456.789));
    }

}
