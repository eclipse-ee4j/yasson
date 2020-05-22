package org.eclipse.yasson.jmh;

import org.eclipse.yasson.jmh.model.TenPropertyData;
import org.openjdk.jmh.annotations.*;

import jakarta.json.bind.*;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Test for serializing a pojo with 10 properties
 */
@BenchmarkMode(Mode.Throughput)
@Timeout(time = 20)
@State(Scope.Benchmark)
@Warmup(iterations = 3)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class TenPropertySerializationTest {
	private Jsonb json;
	private TenPropertyData testData;
	
	@Setup
	public void setup() {
		json = JsonbBuilder.create();
		testData = new TenPropertyData("prop1", "prop2", 42, 3.14, new String[] {"props", "propsss", "psssss"}, 
									   List.of(5, 1, 3), "prop7", 800, new int[] {5555, 6666, 7777, 8888}, Long.MAX_VALUE);
	}
	
	@Benchmark
	public String testSerialize() {
		return json.toJson(testData);
	}
}