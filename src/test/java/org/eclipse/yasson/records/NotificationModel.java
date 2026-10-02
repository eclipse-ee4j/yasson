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

import jakarta.json.bind.adapter.JsonbAdapter;
import jakarta.json.bind.annotation.JsonbProperty;
import jakarta.json.bind.annotation.JsonbSubtype;
import jakarta.json.bind.annotation.JsonbTypeAdapter;
import jakarta.json.bind.annotation.JsonbTypeInfo;

/**
 * Model types for the issue #615 reproducer.
 * <p>
 * The bug: when a sealed interface carries {@code @JsonbTypeInfo} and one of its
 * abstract methods is annotated with {@code @JsonbTypeAdapter}, that adapter was
 * silently ignored for implementing records, leaving the field {@code null} after
 * deserialization.
 */
public final class NotificationModel {

    private NotificationModel() {}

    // ------------------------------------------------------------------
    // Reason enum
    // ------------------------------------------------------------------

    public enum NotificationReason {
        MENTION("mention"),
        FOLLOW("follow");

        private final String value;

        NotificationReason(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }

        public static NotificationReason fromString(String s) {
            for (NotificationReason r : values()) {
                if (r.value.equals(s)) {
                    return r;
                }
            }
            throw new IllegalArgumentException("Unknown reason: " + s);
        }
    }

    // ------------------------------------------------------------------
    // Adapter
    // ------------------------------------------------------------------

    public static class NotificationReasonAdapter implements JsonbAdapter<NotificationReason, String> {

        @Override
        public String adaptToJson(NotificationReason obj) throws Exception {
            return obj.getValue();
        }

        @Override
        public NotificationReason adaptFromJson(String obj) throws Exception {
            return NotificationReason.fromString(obj);
        }
    }

    // ------------------------------------------------------------------
    // Sealed interface with @JsonbTypeInfo + @JsonbTypeAdapter on reason()
    // ------------------------------------------------------------------

    @JsonbTypeInfo(
            key = "@reason",
            value = {
                    @JsonbSubtype(alias = "mention", type = MentionNotification.class),
                    @JsonbSubtype(alias = "follow",  type = FollowNotification.class)
            }
    )
    public sealed interface Notification permits MentionNotification, FollowNotification {

        @JsonbTypeAdapter(NotificationReasonAdapter.class)
        @JsonbProperty("reason")
        NotificationReason reason();
    }

    // ------------------------------------------------------------------
    // Record subtypes
    // ------------------------------------------------------------------

    public record MentionNotification(
            @JsonbProperty("uri") String uri,
            @JsonbTypeAdapter(NotificationReasonAdapter.class)
            @JsonbProperty("reason") NotificationReason reason
    ) implements Notification {}

    public record FollowNotification(
            @JsonbProperty("handle") String handle,
            @JsonbTypeAdapter(NotificationReasonAdapter.class)
            @JsonbProperty("reason") NotificationReason reason
    ) implements Notification {}
}
