/*
 * Copyright (c) 2026 Contributors to the Eclipse Foundation
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License v. 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0,
 * or the Eclipse Distribution License v. 1.0 which is available at
 * http://www.eclipse.org/org/documents/edl-v10.php.
 *
 * SPDX-License-Identifier: EPL-2.0 OR BSD-3-Clause
 */

package org.eclipse.yasson.records;

import java.time.Instant;
import java.time.LocalDate;

import jakarta.json.bind.annotation.JsonbDateFormat;

public record RecordWithDateFormats(@JsonbDateFormat(JsonbDateFormat.TIME_IN_MILLIS) Instant millis,
                                    @JsonbDateFormat(locale = "de") LocalDate localeOnly,
                                    Instant iso) {
}
