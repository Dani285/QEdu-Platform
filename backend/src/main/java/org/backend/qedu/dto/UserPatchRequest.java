package org.backend.qedu.dto;

import org.backend.qedu.model.Roles;

public record UserPatchRequest(
        Boolean enabled,
        Roles role,
        String classGroup
) {}
