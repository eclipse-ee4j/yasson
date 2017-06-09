package org.eclipse.yasson.adapters.model;

/**
 * Created by Roman Grigoriadi (roman.grigoriadi@oracle.com) on 08/06/2017.
 */
public class LocalTypeWrapper<E> {
    private String className;
    private E instance;

    /**
     * Gets class name.
     *
     * @return Class name.
     */
    public String getClassName() {
        return className;
    }

    /**
     * Sets class name.
     *
     * @param className Class name to set.
     */
    public void setClassName(String className) {
        this.className = className;
    }

    /**
     * Gets instance.
     *
     * @return Instance.
     */
    public E getInstance() {
        return instance;
    }

    /**
     * Sets instance.
     *
     * @param instance Instance to set.
     */
    public void setInstance(E instance) {
        this.instance = instance;
    }

}
