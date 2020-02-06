/*
 * Copyright (c) 2017, 2020 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

module org.eclipse.yasson {
    requires jakarta.json;
    requires jakarta.json.bind;
    requires static java.naming;
    requires java.logging;
    requires java.sql;
    requires static java.desktop;
    
    exports org.eclipse.yasson;
    exports org.eclipse.yasson.spi;
    provides jakarta.json.bind.spi.JsonbProvider with org.eclipse.yasson.JsonBindingProvider;
    uses org.eclipse.yasson.spi.JsonbComponentInstanceCreator;
}
