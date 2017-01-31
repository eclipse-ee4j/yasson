package org.eclipse.yasson.model;

import org.eclipse.yasson.internal.JsonbContext;
import org.eclipse.yasson.internal.unmarshaller.CurrentItem;

import java.lang.reflect.Type;

/**
 * Wrapper for metadata of (de)serialized property.
 *
 * @author Roman Grigoriadi
 */
public class JsonbPropertyInfo {

    private JsonbContext context;

    private Type runtimeType;

    private ClassModel classModel;

    private JsonBindingModel jsonBindingModel;

    private CurrentItem<?> wrapper;

    public JsonbContext getContext() {
        return context;
    }

    public JsonbPropertyInfo setContext(JsonbContext context) {
        this.context = context;
        return this;
    }

    public Type getRuntimeType() {
        return runtimeType;
    }

    public JsonbPropertyInfo withRuntimeType(Type runtimeType) {
        this.runtimeType = runtimeType;
        return this;
    }

    public ClassModel getClassModel() {
        return classModel;
    }

    public JsonbPropertyInfo withClassModel(ClassModel classModel) {
        this.classModel = classModel;
        return this;
    }

    public JsonBindingModel getJsonBindingModel() {
        return jsonBindingModel;
    }

    public JsonbPropertyInfo withJsonBindingModel(JsonBindingModel jsonBindingModel) {
        this.jsonBindingModel = jsonBindingModel;
        return this;
    }

    public CurrentItem<?> getWrapper() {
        return wrapper;
    }

    public JsonbPropertyInfo withWrapper(CurrentItem<?> wrapper) {
        this.wrapper = wrapper;
        return this;
    }
}
