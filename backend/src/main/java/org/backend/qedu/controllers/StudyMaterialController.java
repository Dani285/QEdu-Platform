package org.backend.qedu.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.backend.qedu.dto.StudyMaterialRequest;
import org.backend.qedu.service.QEduService;
import org.backend.qedu.entities.StudyMaterial;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/materials")
@RequiredArgsConstructor
public class StudyMaterialController {

    private final QEduService qEduService;

    @GetMapping
    public List<StudyMaterial> list(Authentication authentication) {
        return qEduService.listMaterials(qEduService.currentUser(authentication));
    }

    @PostMapping
    public StudyMaterial create(
            Authentication authentication,
            @Valid @RequestBody StudyMaterialRequest request
    ) {
        return qEduService.createMaterial(qEduService.currentUser(authentication), request);
    }

    @PutMapping("/{id}")
    public StudyMaterial update(
            Authentication authentication,
            @PathVariable Long id,
            @Valid @RequestBody StudyMaterialRequest request
    ) {
        return qEduService.updateMaterial(qEduService.currentUser(authentication), id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(Authentication authentication, @PathVariable Long id) {
        qEduService.deleteMaterial(qEduService.currentUser(authentication), id);
    }
}
