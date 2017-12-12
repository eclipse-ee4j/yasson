package org.eclipse.yasson.adapters.model;

import javax.json.bind.adapter.JsonbAdapter;
import java.io.Serializable;

interface MultiinterfaceAdapter<X, T> extends Serializable, JsonbAdapter<X, T> {


}
