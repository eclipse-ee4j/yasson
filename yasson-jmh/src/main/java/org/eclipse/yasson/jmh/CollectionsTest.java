package org.eclipse.yasson.jmh;

import org.eclipse.yasson.jmh.model.CollectionsData;
import org.eclipse.yasson.jmh.model.ScalarData;
import org.openjdk.jmh.annotations.*;

import jakarta.json.bind.Jsonb;
import jakarta.json.bind.JsonbBuilder;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.concurrent.TimeUnit;

/**
 * Tests for collections processing performance.
 */
@BenchmarkMode(Mode.Throughput)
@Timeout(time = 20)
@State(Scope.Benchmark)
@Warmup(iterations = 3)
@OutputTimeUnit(TimeUnit.MILLISECONDS)
public class CollectionsTest {

    private CollectionsData data;

    private Jsonb jsonb;

    private String json;

    @Setup(Level.Trial)
    public void setUp() {
        data = new CollectionsData();
        data.setListData(new ArrayList<>());
        data.setSetData(new HashSet<>());
        for (int i = 0; i < 50; i++) {
            data.getListData().add(new ScalarData("strValue" + i, i));
            data.getSetData().add(new ScalarData("strValue" + i, i));
        }
        jsonb = JsonbBuilder.create();
        json = "{\"listData\":[{\"integerValue\":0,\"stringValue\":\"strValue0\"},{\"integerValue\":1,\"stringValue\":\"strValue1\"},{\"integerValue\":2,\"stringValue\":\"strValue2\"},{\"integerValue\":3,\"stringValue\":\"strValue3\"},{\"integerValue\":4,\"stringValue\":\"strValue4\"},{\"integerValue\":5,\"stringValue\":\"strValue5\"},{\"integerValue\":6,\"stringValue\":\"strValue6\"},{\"integerValue\":7,\"stringValue\":\"strValue7\"},{\"integerValue\":8,\"stringValue\":\"strValue8\"},{\"integerValue\":9,\"stringValue\":\"strValue9\"},{\"integerValue\":10,\"stringValue\":\"strValue10\"},{\"integerValue\":11,\"stringValue\":\"strValue11\"},{\"integerValue\":12,\"stringValue\":\"strValue12\"},{\"integerValue\":13,\"stringValue\":\"strValue13\"},{\"integerValue\":14,\"stringValue\":\"strValue14\"},{\"integerValue\":15,\"stringValue\":\"strValue15\"},{\"integerValue\":16,\"stringValue\":\"strValue16\"},{\"integerValue\":17,\"stringValue\":\"strValue17\"},{\"integerValue\":18,\"stringValue\":\"strValue18\"},{\"integerValue\":19,\"stringValue\":\"strValue19\"},{\"integerValue\":20,\"stringValue\":\"strValue20\"},{\"integerValue\":21,\"stringValue\":\"strValue21\"},{\"integerValue\":22,\"stringValue\":\"strValue22\"},{\"integerValue\":23,\"stringValue\":\"strValue23\"},{\"integerValue\":24,\"stringValue\":\"strValue24\"},{\"integerValue\":25,\"stringValue\":\"strValue25\"},{\"integerValue\":26,\"stringValue\":\"strValue26\"},{\"integerValue\":27,\"stringValue\":\"strValue27\"},{\"integerValue\":28,\"stringValue\":\"strValue28\"},{\"integerValue\":29,\"stringValue\":\"strValue29\"},{\"integerValue\":30,\"stringValue\":\"strValue30\"},{\"integerValue\":31,\"stringValue\":\"strValue31\"},{\"integerValue\":32,\"stringValue\":\"strValue32\"},{\"integerValue\":33,\"stringValue\":\"strValue33\"},{\"integerValue\":34,\"stringValue\":\"strValue34\"},{\"integerValue\":35,\"stringValue\":\"strValue35\"},{\"integerValue\":36,\"stringValue\":\"strValue36\"},{\"integerValue\":37,\"stringValue\":\"strValue37\"},{\"integerValue\":38,\"stringValue\":\"strValue38\"},{\"integerValue\":39,\"stringValue\":\"strValue39\"},{\"integerValue\":40,\"stringValue\":\"strValue40\"},{\"integerValue\":41,\"stringValue\":\"strValue41\"},{\"integerValue\":42,\"stringValue\":\"strValue42\"},{\"integerValue\":43,\"stringValue\":\"strValue43\"},{\"integerValue\":44,\"stringValue\":\"strValue44\"},{\"integerValue\":45,\"stringValue\":\"strValue45\"},{\"integerValue\":46,\"stringValue\":\"strValue46\"},{\"integerValue\":47,\"stringValue\":\"strValue47\"},{\"integerValue\":48,\"stringValue\":\"strValue48\"},{\"integerValue\":49,\"stringValue\":\"strValue49\"}],\"setData\":[{\"integerValue\":36,\"stringValue\":\"strValue36\"},{\"integerValue\":21,\"stringValue\":\"strValue21\"},{\"integerValue\":38,\"stringValue\":\"strValue38\"},{\"integerValue\":25,\"stringValue\":\"strValue25\"},{\"integerValue\":32,\"stringValue\":\"strValue32\"},{\"integerValue\":9,\"stringValue\":\"strValue9\"},{\"integerValue\":42,\"stringValue\":\"strValue42\"},{\"integerValue\":35,\"stringValue\":\"strValue35\"},{\"integerValue\":6,\"stringValue\":\"strValue6\"},{\"integerValue\":43,\"stringValue\":\"strValue43\"},{\"integerValue\":37,\"stringValue\":\"strValue37\"},{\"integerValue\":27,\"stringValue\":\"strValue27\"},{\"integerValue\":2,\"stringValue\":\"strValue2\"},{\"integerValue\":1,\"stringValue\":\"strValue1\"},{\"integerValue\":24,\"stringValue\":\"strValue24\"},{\"integerValue\":28,\"stringValue\":\"strValue28\"},{\"integerValue\":29,\"stringValue\":\"strValue29\"},{\"integerValue\":23,\"stringValue\":\"strValue23\"},{\"integerValue\":12,\"stringValue\":\"strValue12\"},{\"integerValue\":22,\"stringValue\":\"strValue22\"},{\"integerValue\":49,\"stringValue\":\"strValue49\"},{\"integerValue\":5,\"stringValue\":\"strValue5\"},{\"integerValue\":44,\"stringValue\":\"strValue44\"},{\"integerValue\":41,\"stringValue\":\"strValue41\"},{\"integerValue\":45,\"stringValue\":\"strValue45\"},{\"integerValue\":0,\"stringValue\":\"strValue0\"},{\"integerValue\":18,\"stringValue\":\"strValue18\"},{\"integerValue\":8,\"stringValue\":\"strValue8\"},{\"integerValue\":10,\"stringValue\":\"strValue10\"},{\"integerValue\":33,\"stringValue\":\"strValue33\"},{\"integerValue\":30,\"stringValue\":\"strValue30\"},{\"integerValue\":13,\"stringValue\":\"strValue13\"},{\"integerValue\":16,\"stringValue\":\"strValue16\"},{\"integerValue\":48,\"stringValue\":\"strValue48\"},{\"integerValue\":3,\"stringValue\":\"strValue3\"},{\"integerValue\":31,\"stringValue\":\"strValue31\"},{\"integerValue\":40,\"stringValue\":\"strValue40\"},{\"integerValue\":15,\"stringValue\":\"strValue15\"},{\"integerValue\":39,\"stringValue\":\"strValue39\"},{\"integerValue\":14,\"stringValue\":\"strValue14\"},{\"integerValue\":46,\"stringValue\":\"strValue46\"},{\"integerValue\":34,\"stringValue\":\"strValue34\"},{\"integerValue\":17,\"stringValue\":\"strValue17\"},{\"integerValue\":7,\"stringValue\":\"strValue7\"},{\"integerValue\":20,\"stringValue\":\"strValue20\"},{\"integerValue\":4,\"stringValue\":\"strValue4\"},{\"integerValue\":11,\"stringValue\":\"strValue11\"},{\"integerValue\":19,\"stringValue\":\"strValue19\"},{\"integerValue\":47,\"stringValue\":\"strValue47\"},{\"integerValue\":26,\"stringValue\":\"strValue26\"}]}";
    }

    @Benchmark
    public String testSerialize() {
        return jsonb.toJson(data);
    }

    @Benchmark
    public CollectionsData testDeserialize() {
        return jsonb.fromJson(json, CollectionsData.class);
    }
}
