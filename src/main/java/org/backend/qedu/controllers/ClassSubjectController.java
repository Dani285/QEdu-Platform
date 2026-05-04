package org.backend.qedu.controllers;

import lombok.RequiredArgsConstructor;
import jakarta.validation.Valid;
import org.backend.qedu.dto.ClassSubjectRequest;
import org.backend.qedu.dto.ClassSubjectResponse;
import org.backend.qedu.dto.EnrollmentStudentIdsRequest;
import org.backend.qedu.dto.UserDtos.UserDto;
import org.backend.qedu.service.QEduService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/class-subjects")
@RequiredArgsConstructor
public class ClassSubjectController {

    private final QEduService qEduService;

    @GetMapping
    public List<ClassSubjectResponse> list(Authentication authentication) {
        qEduService.currentUser(authentication);
        return qEduService.listClassSubjects();
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ClassSubjectResponse create(@Valid @RequestBody ClassSubjectRequest request) {
        return qEduService.createClassSubject(request);
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ClassSubjectResponse update(@PathVariable Long id, @Valid @RequestBody ClassSubjectRequest request) {
        return qEduService.updateClassSubject(id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable Long id) {
        qEduService.deleteClassSubject(id);
    }

    @GetMapping("/{id}/enrollments")
    public List<UserDto> listEnrollments(@PathVariable Long id, Authentication authentication) {
        return qEduService.listEnrolledStudents(id, qEduService.currentUser(authentication));
    }

    @PutMapping("/{id}/enrollments")
    @PreAuthorize("hasRole('ADMIN')")
    public void replaceEnrollments(@PathVariable Long id, @Valid @RequestBody EnrollmentStudentIdsRequest body) {
        qEduService.replaceEnrollments(id, body.studentUserIds());
    }
}
