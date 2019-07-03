package org.eclipse.yasson.jmh.model;

import java.util.List;
import java.util.Set;

public class CollectionsData {

    private List<ScalarData> listData;

    private Set<ScalarData> setData;

    public List<ScalarData> getListData() {
        return listData;
    }

    public void setListData(List<ScalarData> listData) {
        this.listData = listData;
    }

    public Set<ScalarData> getSetData() {
        return setData;
    }

    public void setSetData(Set<ScalarData> setData) {
        this.setData = setData;
    }
}
