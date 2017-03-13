/*******************************************************************************
 * Copyright (c) 2016 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 *  Ehsan Zaery Moghaddam (zaerymoghaddam@gmail.com)
 ******************************************************************************/

package org.eclipse.yasson.customization.transients.models;

import javax.json.bind.annotation.JsonbProperty;
import javax.json.bind.annotation.JsonbTransient;

/**
 * @author Ehsan Zaery Moghaddam (zaerymoghaddam@gmail.com)
 */
public class JsonbTransientAcceptableAnnotationCollision {

    @JsonbProperty("annotated_property_transient_getter")
    private String annotatedPropertyTransientGetter;

    @JsonbProperty("annotated_property_transient_setter")
    private String annotatedPropertyTransientSetter;

    @JsonbProperty("annotated_property_transient_getter_and_setter")
    private String annotatedPropertyTransientGetterAndSetter;



    @JsonbTransient
    public String getAnnotatedPropertyTransientGetter() {
        return annotatedPropertyTransientGetter;
    }

    public void setAnnotatedPropertyTransientGetter(String annotatedPropertyTransientGetter) {
        this.annotatedPropertyTransientGetter = annotatedPropertyTransientGetter;
    }



    public String getAnnotatedPropertyTransientSetter() {
        return annotatedPropertyTransientSetter;
    }

    @JsonbTransient
    public void setAnnotatedPropertyTransientSetter(String annotatedPropertyTransientSetter) {
        this.annotatedPropertyTransientSetter = annotatedPropertyTransientSetter;
    }


    @JsonbTransient
    public String getAnnotatedPropertyTransientGetterAndSetter() {
        return annotatedPropertyTransientGetterAndSetter;
    }

    @JsonbTransient
    public void setAnnotatedPropertyTransientGetterAndSetter(String annotatedPropertyTransientGetterAndSetter) {
        this.annotatedPropertyTransientGetterAndSetter = annotatedPropertyTransientGetterAndSetter;
    }
}
