/*
 * Copyright (c) 2026 Contributors to the Eclipse Foundation
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.yasson.customization.polymorphism;

import java.util.List;
import java.util.Set;

import jakarta.json.bind.annotation.JsonbCreator;
import jakarta.json.bind.annotation.JsonbSubtype;
import jakarta.json.bind.annotation.JsonbTypeInfo;
import org.junit.jupiter.api.Test;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import static org.eclipse.yasson.Jsonbs.defaultJsonb;

/**
 * Regression tests for https://github.com/eclipse-ee4j/yasson/issues/775
 *
 * The root cause was a stale key left in the serialization context after the
 * polymorphism discriminator lambda wrote its key+value pair directly to the
 * generator (bypassing KeyWriter and its context.setKey(null) cleanup).
 *
 * The stale key caused the outer KeyWriter — wrapping the per-element object
 * serializer chain — to call generator.writeKey() while the generator was in
 * IN_ARRAY state when processing any element after the first.
 *
 * Three conditions are required to trigger the bug:
 *   1. A type annotated with @JsonbTypeInfo (adds the raw discriminator lambda).
 *   2. The subtype has NO other serializable properties (so no subsequent
 *      KeyWriter-wrapped property serializer clears the stale key).
 *   3. More than one instance appears consecutively in a collection or array
 *      (so the outer KeyWriter fires again on the second element).
 */
public class SubtypeCollectionTest {

    // =========================================================================
    // Scenario 1 – sealed interface + record subtypes (original report, #775)
    // =========================================================================

    @JsonbTypeInfo(key = "type", value = {
            @JsonbSubtype(alias = "optionA", type = OptionA.class),
            @JsonbSubtype(alias = "optionB", type = OptionB.class),
    })
    public sealed interface TestJsonbInterface permits OptionA, OptionB {
    }

    public record OptionA() implements TestJsonbInterface {
        @JsonbCreator
        public OptionA {
        }
    }

    public record OptionB() implements TestJsonbInterface {
        @JsonbCreator
        public OptionB {
        }
    }

    public record TestJsonbSetRecord(Set<TestJsonbInterface> interfaceSet) {
        @JsonbCreator
        public TestJsonbSetRecord {
        }
    }

    public record TestJsonbListRecord(List<TestJsonbInterface> interfaceList) {
        @JsonbCreator
        public TestJsonbListRecord {
        }
    }

    @Test
    public void testSingleElementSetSerialization() {
        String json = defaultJsonb.toJson(new TestJsonbSetRecord(Set.of(new OptionA())));
        assertThat(json, containsString("\"type\":\"optionA\""));
    }

    /** Reproduces issue #775. */
    @Test
    public void testMultipleElementSetSerialization() {
        String json = defaultJsonb.toJson(new TestJsonbSetRecord(Set.of(new OptionA(), new OptionB())));
        assertThat(json, containsString("\"type\":\"optionA\""));
        assertThat(json, containsString("\"type\":\"optionB\""));
    }

    @Test
    public void testMultipleElementListSerialization() {
        String json = defaultJsonb.toJson(new TestJsonbListRecord(List.of(new OptionA(), new OptionB(), new OptionA())));
        assertThat(json, is("{\"interfaceList\":[{\"type\":\"optionA\"},{\"type\":\"optionB\"},{\"type\":\"optionA\"}]}"));
    }

    // =========================================================================
    // Scenario 2 – plain classes, interface with @JsonbTypeInfo
    // =========================================================================

    @JsonbTypeInfo(key = "kind", value = {
            @JsonbSubtype(alias = "alpha", type = Alpha.class),
            @JsonbSubtype(alias = "beta",  type = Beta.class),
    })
    public interface PlainInterface {
    }

    /** Plain class – no fields, no record. */
    public static class Alpha implements PlainInterface {
    }

    /** Plain class – no fields, no record. */
    public static class Beta implements PlainInterface {
    }

    public static class PlainWrapper {
        public List<PlainInterface> items;

        public PlainWrapper(List<PlainInterface> items) {
            this.items = items;
        }
    }

    @Test
    public void testMultipleElementListPlainClassSerialization() {
        String json = defaultJsonb.toJson(new PlainWrapper(List.of(new Alpha(), new Beta(), new Alpha())));
        assertThat(json, is("{\"items\":[{\"kind\":\"alpha\"},{\"kind\":\"beta\"},{\"kind\":\"alpha\"}]}"));
    }

    // =========================================================================
    // Scenario 3 – array instead of collection
    // =========================================================================

    @Test
    public void testMultipleElementArraySerialization() {
        PlainInterface[] arr = { new Alpha(), new Beta(), new Alpha() };
        String json = defaultJsonb.toJson(arr);
        assertThat(json, is("[{\"kind\":\"alpha\"},{\"kind\":\"beta\"},{\"kind\":\"alpha\"}]"));
    }

    // =========================================================================
    // Scenario 4 – repeated same subtype in a collection
    // =========================================================================

    @Test
    public void testRepeatedSameSubtypeInListSerialization() {
        String json = defaultJsonb.toJson(new PlainWrapper(List.of(new Alpha(), new Alpha())));
        assertThat(json, is("{\"items\":[{\"kind\":\"alpha\"},{\"kind\":\"alpha\"}]}"));
    }
}
