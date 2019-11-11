package org.eclipse.yasson.adapters.model;

import java.util.LinkedHashMap;
import java.util.Map;

import javax.json.bind.adapter.JsonbAdapter;

public class ChainAdapter implements JsonbAdapter<Chain, Map<String, Object>>{

    @Override
    public Map<String, Object> adaptToJson(Chain obj) throws Exception {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("has", obj.getHas());
        map.put("linksTo", obj.getLinksTo());
        map.put("name", obj.getName());
        return map;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Chain adaptFromJson(Map<String, Object> obj) throws Exception {
        if(obj != null) {
            Chain chain = new Chain((String) obj.get("name"));
            chain.setHas((Foo) obj.get("has"));
            adaptFromJson((Map<String, Object>) obj.get("linksTo"));
            return chain;
        } else {
            return null;
        }
        
    }
    
}