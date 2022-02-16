/*
 * Copyright (c) 2021, 2022 Oracle and/or its affiliates. All rights reserved.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.yasson.internal.model;

import java.lang.invoke.MethodHandles;

/**
 * Why is this class here?.
 *
 * This class is here to replace existing classes in test-runtime:
 * - src/main/java/org/eclipse/yasson/internal/model/ModulesUtil.java
 * - src/main/java9/org/eclipse/yasson/internal/model/ModulesUtil.java
 *
 * When tests are executed with maven-surefire-plugin the content of
 * 'classes' is in a different module-path than 'test-classes'.
 *
 * This causes the MethodHandles#publicLookup to fail. The reason is that
 * test classes to serialize/deserialize are coming from module 'test-classes'
 * and the module 'classes' has no access to it. The 'publicLookup' makes some
 * validations and because of this different modules, it fails.
 *
 * It should work if 'classes' and 'test-classes' are merged in one unique module.
 *
 */
class ModulesUtil {


    private ModulesUtil() {
    }

    static MethodHandles.Lookup lookup(){
        return MethodHandles.lookup();
    }
}
