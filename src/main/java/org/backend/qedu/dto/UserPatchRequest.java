package org.backend.qedu.dto;

import org.backend.qedu.model.Roles;

public record UserPatchRequest(
        Boolean enabled,
        Roles role,
        /** Null = ne valtozzon; ures vagy "-" = nincs osztaly (null a DB-ben). */
        String classGroup
) {}
