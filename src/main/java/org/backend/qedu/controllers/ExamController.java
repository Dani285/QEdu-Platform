package org.backend.qedu.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.backend.qedu.dto.ExamRequest;
import org.backend.qedu.entities.QEduExam;
import org.backend.qedu.service.QEduService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/exams")
@RequiredArgsConstructor
public class ExamController {

    private final QEduService qEduService;

    @GetMapping
    public List<QEduExam> list(Authentication authentication) {
        return qEduService.listExams(qEduService.currentUser(authentication));
    }

    @PostMapping
    public QEduExam create(
            Authentication authentication,
            @Valid @RequestBody ExamRequest request
    ) {
        return qEduService.createExam(qEduService.currentUser(authentication), request);
    }

    @PutMapping("/{id}")
    public QEduExam update(
            Authentication authentication,
            @PathVariable Long id,
            @Valid @RequestBody ExamRequest request
    ) {
        return qEduService.updateExam(qEduService.currentUser(authentication), id, request);
    }

    @DeleteMapping("/{id}")
    public void delete(Authentication authentication, @PathVariable Long id) {
        qEduService.deleteExam(qEduService.currentUser(authentication), id);
    }
}
