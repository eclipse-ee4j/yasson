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

package org.eclipse.yasson.concurrency;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import javax.json.bind.Jsonb;
import javax.json.bind.JsonbBuilder;
import javax.json.bind.annotation.JsonbNumberFormat;

/**
 * java.text.NumberFormat from the JDK is not thread safe.
 * Ensure that serialization and deserialization actions correctly synchronize access
 * to ensure data integrity while sharing a singleton Jsonb instance.
 */
public class NumberFormatTest {
    private static Jsonb jsonb;

    @BeforeAll
    public static void setup() {
        jsonb = JsonbBuilder.create();
    }

    @AfterAll
    public static void tearDown() throws Exception {
        if (jsonb != null) {
            jsonb.close();
        }
    }

    // ---- Deserialization ----

    // Test model
    public static final class Amount {
        @JsonbNumberFormat(value = "#0", locale = "en-US")
        public long value;
    }

    // Test data
    private static final long VICTIM = 271828182845904L;
    private static final long[] PEER = {
            123456789012345L, 987654321009876L, 314159265358979L
    };

    @Test
    public void deserializationTest() throws Exception {
        // Prime jsonb singleton for deserialization
        jsonb.fromJson(json(VICTIM), Amount.class);

        // Setup thread pool
        ExecutorService pool = Executors.newFixedThreadPool(2);

        // Submit tasks to thread pool with latch
        CountDownLatch start = new CountDownLatch(1);
        Future<long[]> victim = pool.submit(() -> run(jsonb, start, true));
        Future<long[]> peer = pool.submit(() -> run(jsonb, start, false));

        // Release latch and collect data
        long[] v;
        long[] p;
        try {
            start.countDown();
            v = victim.get();
            p = peer.get();
        } finally {
            // Teardown thread pool
            pool.shutdownNow();
        }

        // Verify no errors
        assertAll("Verify no concurrent deserialization errors",
            () -> assertEquals(0, v[0], "Victim thread deserialized an incorrect value"),
            () -> assertEquals(0, v[1], "Victim thread deserialized a value belonging to the peer thread"),
            () -> assertEquals(0, v[2], "Victim thread threw an exception during deserialization"),
            () -> assertEquals(0, p[0], "Peer thread deserialized an incorrect value"),
            () -> assertEquals(0, p[2], "Peer thread threw an exception during deserialization")
        );
    }

    /**
     * Repeatedly deserialize values with the shared {@link Jsonb} instance and count failures.
     *
     * @param jsonb  shared Jsonb instance
     * @param start  latch used to start all threads at the same time
     * @param victim {@code true} to deserialize the victim value, {@code false} to cycle through peer values
     * @return array of counters: {wrong values, victim values that match a peer value, runtime exceptions}
     * @throws InterruptedException if interrupted while waiting for the start latch
     */
    private static long[] run(Jsonb jsonb, CountDownLatch start, boolean victim)
            throws InterruptedException {

        long wrong = 0; // Deserialization caused victim to have wrong value
        long peer = 0; // Deserialization caused victim to have a peer value
        long errors = 0; // Deserialization resulted in runtime exception

        // Await latch
        start.await();

        // Run 100,000 iterations of deserialization
        for (int i = 0; i < 100_000; i++) {

            // Determine expected value victim vs peer
            long expected = victim ? VICTIM : PEER[i % PEER.length];

            try {
                long actual = jsonb.fromJson(json(expected), Amount.class).value;
                if (actual != expected) {
                    wrong++;
                    if (victim && isPeer(actual)) {
                        peer++;
                    }
                }
            } catch (RuntimeException error) {
                errors++;
            }
        }

        return new long[] { wrong, peer, errors };
    }

    private static boolean isPeer(long value) {
        for (long candidate : PEER) {
            if (candidate == value) {
                return true;
            }
        }
        return false;
    }

    private static String json(long value) {
        return "{\"value\":\"" + value + "\"}";
    }

    // ---- Serialization ----
    // NOTE: some JDK implementations of DecimalFormat.format() effectively do synchronization but
    // that behavior is not mandated so we should test it.

    // Test model
    public static final class DecimalAmount {
        @JsonbNumberFormat(value = "#,##0.00", locale = "en-US")
        public double value;
    }

    // Test data
    private static final double DECIMAL_VICTIM = 2718281.83;
    private static final double[] DECIMAL_PEER = {
            1234567.89, 9876543.21, 3141592.65
    };

    @Test
    public void serializationTest() throws Exception {
        // Prime jsonb singleton for serialization
        jsonb.toJson(decimalAmount(DECIMAL_VICTIM));

        // Setup thread pool
        ExecutorService pool = Executors.newFixedThreadPool(2);

        // Submit tasks to thread pool with latch
        CountDownLatch start = new CountDownLatch(1);
        Future<long[]> victim = pool.submit(() -> runSerialization(jsonb, start, true));
        Future<long[]> peer = pool.submit(() -> runSerialization(jsonb, start, false));

        // Release latch and collect data
        long[] v;
        long[] p;
        try {
            start.countDown();
            v = victim.get();
            p = peer.get();
        } finally {
            // Teardown thread pool
            pool.shutdownNow();
        }

        // Verify no errors
        assertAll("Verify no concurrent serialization errors",
            () -> assertEquals(0, v[0], "Victim thread serialized an incorrect value"),
            () -> assertEquals(0, v[1], "Victim thread serialized a value belonging to the peer thread"),
            () -> assertEquals(0, v[2], "Victim thread threw an exception during serialization"),
            () -> assertEquals(0, p[0], "Peer thread serialized an incorrect value"),
            () -> assertEquals(0, p[2], "Peer thread threw an exception during serialization")
        );
    }

    /**
     * Repeatedly serialize values with the shared {@link Jsonb} instance and count failures.
     *
     * @param jsonb  shared Jsonb instance
     * @param start  latch used to start all threads at the same time
     * @param victim {@code true} to serialize the victim value, {@code false} to cycle through peer values
     * @return array of counters: {wrong values, victim values that match a peer value, runtime exceptions}
     * @throws InterruptedException if interrupted while waiting for the start latch
     */
    private static long[] runSerialization(Jsonb jsonb, CountDownLatch start, boolean victim)
            throws InterruptedException {

        long wrong = 0; // Serialization caused victim to have wrong value
        long peer = 0; // Serialization caused victim to have a peer value
        long errors = 0; // Serialization resulted in runtime exception

        // Await latch
        start.await();

        // Run 100,000 iterations of serialization
        for (int i = 0; i < 100_000; i++) {

            // Determine expected value victim vs peer
            double expected = victim ? DECIMAL_VICTIM : DECIMAL_PEER[i % DECIMAL_PEER.length];
            String expectedJson = decimalJson(expected);

            try {
                String actual = jsonb.toJson(decimalAmount(expected));
                if (!expectedJson.equals(actual)) {
                    wrong++;
                    if (victim && isPeerJson(actual)) {
                        peer++;
                    }
                }
            } catch (RuntimeException error) {
                errors++;
            }
        }

        return new long[] { wrong, peer, errors };
    }

    private static boolean isPeerJson(String value) {
        for (double candidate : DECIMAL_PEER) {
            if (decimalJson(candidate).equals(value)) {
                return true;
            }
        }
        return false;
    }

    private static DecimalAmount decimalAmount(double value) {
        DecimalAmount amount = new DecimalAmount();
        amount.value = value;
        return amount;
    }

    private static String decimalJson(double value) {
        // Local formatter so the expected value is not computed with the shared formatter under test
        DecimalFormat expected = new DecimalFormat("#,##0.00", DecimalFormatSymbols.getInstance(Locale.US));
        return "{\"value\":\"" + expected.format(value) + "\"}";
    }
}
