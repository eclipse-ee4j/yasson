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

package org.eclipse.yasson.defaultmapping.properties;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import org.eclipse.yasson.internal.properties.MessageKeys;
import org.eclipse.yasson.internal.properties.Messages;

import java.io.IOException;
import java.util.Locale;

/**
 * This class contains properties tests
 *
 * @author David Kral
 */
public class PropertiesTest {

    @Test
    public void testPropertiesWithoutLocale() throws IOException {
        String template = "Process class: {0} from json using converter: {1}";
        String message = Messages.getMessage(MessageKeys.PROCESS_FROM_JSON);

        assertEquals(template, message);
    }

    @Test
    public void testPropertiesWithLocale() throws IOException {
        String templateCS = "Zpracovávám třídu: {0} do jsonu za použití convertoru: {1}";
        String messageCS = Messages.getMessage(MessageKeys.PROCESS_TO_JSON, new Locale("cs"));
        String templateEN = "Process class: {0} to json using converter: {1}";
        String messageEN = Messages.getMessage(MessageKeys.PROCESS_TO_JSON, new Locale("en"));

        assertEquals(templateCS, messageCS);
        assertEquals(templateEN, messageEN);
    }

    @Test
    public void testPropertiesAttributeSetting() throws IOException {
        String template = "Zpracovávám třídu: Test do jsonu za použití convertoru: Test1";
        String message = Messages.getMessage(MessageKeys.PROCESS_TO_JSON, new Locale("cs"), "Test", "Test1");

        assertEquals(template, message);
    }

}

