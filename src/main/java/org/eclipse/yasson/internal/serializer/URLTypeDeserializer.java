/*******************************************************************************
 * Copyright (c) 2016, 2018 Oracle and/or its affiliates. All rights reserved.
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v1.0 and Eclipse Distribution License v. 1.0
 * which accompanies this distribution.
 * The Eclipse Public License is available at http://www.eclipse.org/legal/epl-v10.html
 * and the Eclipse Distribution License is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * Contributors:
 * Roman Grigoriadi
 ******************************************************************************/

package org.eclipse.yasson.internal.serializer;


import org.eclipse.yasson.internal.Unmarshaller;
import org.eclipse.yasson.internal.model.customization.Customization;

import java.lang.reflect.Type;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Deserializer for {@link URL} type.
 * 
 * @author David Kral
 */
public class URLTypeDeserializer extends AbstractValueTypeDeserializer<URL> {

    /**
     * Creates a new instance.
     *
     * @param customization Model customization.
     */
    public URLTypeDeserializer(Customization customization) {
        super(URL.class, customization);
    }

    @Override
    protected URL deserialize(String jsonValue, Unmarshaller unmarshaller, Type rtType) {
        URL url = null;
        try {
            url = new URL(jsonValue);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }
}
