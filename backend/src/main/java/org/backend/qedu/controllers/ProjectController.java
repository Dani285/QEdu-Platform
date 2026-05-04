package org.backend.qedu.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.backend.qedu.dto.ProjectRequest;
import org.backend.qedu.entities.QEduProject;
import org.backend.qedu.service.QEduService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/projects")
@RequiredArgsConstructor
public class ProjectController {

    private final QEduService qEduService;

    @GetMapping
    public List<QEduProject> list(Authentication authentication) {
        return qEduService.listProjects(qEduService.currentUser(authentication));
    }

    @PostMapping
    public QEduProject create(
            Authentication authentication,
            @Valid @RequestBody ProjectRequest request
    ) {
        return qEduService.createProject(qEduService.currentUser(authentication), request);
    }

    @PutMapping("/{id}")
    public QEduProject update(
            Authentication authentication,
            @PathVariable Long id,
            @Valid @RequestBody ProjectRequest request
    ) {
        return qEduService.updateProject(qEduService.currentUser(authentication), id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(Authentication authentication, @PathVariable Long id) {
        qEduService.deleteProject(qEduService.currentUser(authentication), id);
    }
}
