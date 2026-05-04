package org.backend.qedu.dto;

import jakarta.validation.constraints.NotBlank;

public record MessageThreadRequest(
        @NotBlank String title,
        @NotBlank String lastMessage,
        /** JSON array of role strings, e.g. [\"STUDENT\",\"TEACHER\"] */
        @NotBlank String audienceJson,
        /** JSON array of class ids or empty */
        String classTargetsJson
) {}
