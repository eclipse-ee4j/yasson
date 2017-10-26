package org.eclipse.yasson.adapters.model;

import javax.json.Json;
import javax.json.JsonValue;
import javax.json.bind.adapter.JsonbAdapter;

public class FirstNameAdapter implements JsonbAdapter<String, JsonValue> {
    @Override
    public JsonValue adaptToJson(String firstName) {
        return Json.createValue(firstName.subSequence(0,1).toString());
    }
    @Override
    public String adaptFromJson(JsonValue json) {
        return json.toString();
    }
}
