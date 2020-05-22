package org.eclipse.yasson.jmh;

import org.eclipse.yasson.jmh.model.ScalarData;
import org.openjdk.jmh.annotations.*;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import java.util.concurrent.TimeUnit;


/**
 * Tests for simple data, which does not have inner object references or collections.
 */
@BenchmarkMode(Mode.Throughput)
@Timeout(time = 20)
@State(Scope.Benchmark)
@Warmup(iterations = 3)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class ScalarDataTest {

    private Jsonb jsonb;

    private ScalarData data;

    private String json;

    @Setup(Level.Trial)
    public void setUp() {
        this.jsonb = JsonbBuilder.create();
        this.data = new ScalarData();
        this.json = "{\"integerValue\":10,\"stringValue\":\"Short string\"}";
    }

    @Benchmark
    public String testSerialize() {
        return jsonb.toJson(data);
    }

    @Benchmark
    public ScalarData testDeserialize() {
        return jsonb.fromJson(json, ScalarData.class);
    }

}
