/*******************************************************************************
 * Copyright (c) 2017, 2018 Oracle and/or its affiliates. All rights reserved.
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
module org.eclipse.yasson {
    requires java.json;
    requires java.json.bind;
    requires static java.naming;
    requires java.desktop;
    requires java.logging;
    requires java.sql;

    exports org.eclipse.yasson;
    provides javax.json.bind.spi.JsonbProvider with org.eclipse.yasson.JsonBindingProvider;
}