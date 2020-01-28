/*
 * Copyright (c) 2015, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.yasson.defaultmapping.modifiers.model;

import java.util.function.Consumer;

/**
 * @author Roman Grigoriadi
 */
public class MethodModifiersClass {

    private Consumer<String> setterWithoutFieldConsumer = s -> {};

    public String publicFieldWithPrivateMethods;

    public String publicFieldWithoutMethods;

    public void setSetterWithoutFieldConsumer(Consumer<String> setterWithoutFieldConsumer) {
        this.setterWithoutFieldConsumer = setterWithoutFieldConsumer;
    }

    public String getGetterWithoutFieldValue() {
        return "GETTER_WITHOUT_FIELD";
    }

    public void setGetterWithoutFieldValue(String value) {
        setterWithoutFieldConsumer.accept(value);
    }

    private String getPublicFieldWithPrivateMethods() {
        return publicFieldWithPrivateMethods;
    }

    private void setPublicFieldWithPrivateMethods(String publicFieldWithPrivateMethods) {
        this.publicFieldWithPrivateMethods = publicFieldWithPrivateMethods;
    }
}
