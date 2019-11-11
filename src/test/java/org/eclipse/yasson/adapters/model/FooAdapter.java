package org.eclipse.yasson.adapters.model;

import java.util.HashMap;
import java.util.Map;

import javax.json.bind.adapter.JsonbAdapter;

public class FooAdapter implements JsonbAdapter<Foo, Map<String, String>>{

    @Override
    public Map<String, String> adaptToJson(Foo obj) throws Exception {
        Map<String, String> map = new HashMap<>();
        map.put("bar", obj.getBar());
        return map;
    }

    @Override
    public Foo adaptFromJson( Map<String, String> obj) throws Exception {
        return new Foo(obj.get("bar").toString());
    }
    
}
