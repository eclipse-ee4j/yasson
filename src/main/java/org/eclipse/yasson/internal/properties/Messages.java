/*
 * Copyright (c) 2015, 2019 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.yasson.internal.properties;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.text.MessageFormat;
import java.util.Locale;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

/**
 * JSON-B messages.
 */
public class Messages {

    private static final String MESSAGE_BUNDLE = "yasson-messages";
    private static final String ENCODING = "UTF-8";

    private Messages() {
    }

    /**
     * Gets message by key. Default locale is used.
     *
     * @param key     Message key.
     * @param objects Message parameters.
     * @return Formatted message in string.
     */
    public static String getMessage(MessageKeys key, Object... objects) {
        return getMessage(key, Locale.getDefault(), objects);
    }

    /**
     * Gets message by key and locale.
     *
     * @param key     Message key.
     * @param locale  Locale.
     * @param objects Message parameters.
     * @return Formatted message in string.
     */
    public static String getMessage(MessageKeys key, Locale locale, Object... objects) {
        ResourceBundle messages = getResourceBundle(locale);
        MessageFormat formatter = new MessageFormat(messages.getString(key.getKey()));
        return formatter.format(objects);
    }

    /**
     * ResourceBundle.Control is not supported when loaded from JPMS native module.
     */
    private static ResourceBundle getResourceBundle(Locale locale) {
        try {
            return ResourceBundle.getBundle(MESSAGE_BUNDLE, locale, new UTF8Control());
        } catch (UnsupportedOperationException e) {
            return ResourceBundle.getBundle(MESSAGE_BUNDLE, locale);
        }
    }

    static class UTF8Control extends ResourceBundle.Control {
        public ResourceBundle newBundle(String baseName, Locale locale, String format, ClassLoader loader, boolean reload)
                throws IllegalAccessException, InstantiationException, IOException {
            // The below is a copy of the default implementation.
            String bundleName = toBundleName(baseName, locale);
            String resourceName = toResourceName(bundleName, "properties");
            ResourceBundle bundle = null;
            InputStream stream = null;
            if (reload) {
                URL url = loader.getResource(resourceName);
                if (url != null) {
                    URLConnection connection = url.openConnection();
                    if (connection != null) {
                        connection.setUseCaches(false);
                        stream = connection.getInputStream();
                    }
                }
            } else {
                stream = loader.getResourceAsStream(resourceName);
            }
            if (stream != null) {
                try {
                    // Only this line is changed to make it to read properties files as UTF-8.
                    bundle = new PropertyResourceBundle(new InputStreamReader(stream, ENCODING));
                } finally {
                    stream.close();
                }
            }
            return bundle;
        }
    }

}
