package org.eclipse.yasson.adapters.model;

import javax.json.bind.adapter.JsonbAdapter;
import java.util.stream.Stream;

/**
 * Created by Roman Grigoriadi (roman.grigoriadi@oracle.com) on 08/06/2017.
 */
public class LocalPolymorphicAdapter<T> implements JsonbAdapter<T, LocalTypeWrapper<T>> {

    private final String[] allowedClasses;

    /**
     * Create new instance.
     *
     * @param allowedClasses allowed classes for loading by name
     */
    public LocalPolymorphicAdapter(final Class... allowedClasses) {
        this.allowedClasses = Stream.of(allowedClasses).map(Class::getName).toArray(value -> new String[allowedClasses.length]);
    }

    /**
     * Returns all classes which are allowed for loading.
     *
     * @return allowed classes for loading by name
     */
    public String[] getAllowedClasses() {
        return allowedClasses;
    }

    @Override
    public LocalTypeWrapper<T> adaptToJson(T obj) throws Exception {
        System.out.println("AdaptingToJson: " + obj);
        LocalTypeWrapper<T> wrapper = new LocalTypeWrapper<>();
        wrapper.setClassName(obj.getClass().getName());
        wrapper.setInstance(obj);
        return wrapper;
    }

    @Override
    public T adaptFromJson(LocalTypeWrapper<T> obj) throws Exception {
        System.out.println("ADaptingFromJson: " + obj);
        return obj.getInstance();
    }

}
