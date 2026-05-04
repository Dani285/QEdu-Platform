package org.backend.qedu.dto;

import jakarta.validation.constraints.NotBlank;

public record MessageThreadRequest(
        @NotBlank String title,
        @NotBlank String lastMessage,
        @NotBlank String audienceJson,

        String classTargetsJson
) {}
