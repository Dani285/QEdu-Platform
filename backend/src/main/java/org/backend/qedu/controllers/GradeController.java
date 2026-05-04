package org.backend.qedu.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.backend.qedu.dto.GradeCreateRequest;
import org.backend.qedu.dto.GradeResponse;
import org.backend.qedu.dto.GradeUpdateRequest;
import org.backend.qedu.service.QEduService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class GradeController {

    private final QEduService qEduService;

    @GetMapping("/api/grades")
    public List<GradeResponse> list(
            Authentication authentication,
            @RequestParam(required = false) String classGroup,
            @RequestParam(required = false) String subjectName,
            @RequestParam(required = false) String studentUsername
    ) {
        return qEduService.listGrades(qEduService.currentUser(authentication), classGroup, subjectName, studentUsername);
    }

    @PostMapping("/api/grades")
    public GradeResponse create(
            Authentication authentication,
            @Valid @RequestBody GradeCreateRequest request
    ) {
        return qEduService.createGrade(qEduService.currentUser(authentication), request);
    }

    @PatchMapping("/api/grades/{id}")
    public GradeResponse update(
            Authentication authentication,
            @PathVariable Long id,
            @Valid @RequestBody GradeUpdateRequest request
    ) {
        return qEduService.updateGrade(qEduService.currentUser(authentication), id, request);
    }

    @DeleteMapping("/api/grades/{id}")
    public void delete(Authentication authentication, @PathVariable Long id) {
        qEduService.deleteGrade(qEduService.currentUser(authentication), id);
    }
}
