package org.backend.qedu.controllers;

import lombok.RequiredArgsConstructor;
import org.backend.qedu.dto.UserDtos.UserDto;
import org.backend.qedu.service.QEduService;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/students")
@RequiredArgsConstructor
public class StudentDirectoryController {

    private final QEduService qEduService;

    @GetMapping
    public List<UserDto> list(
            Authentication authentication,
            @RequestParam(required = false) String classId,
            @RequestParam(required = false) String subjectId
    ) {
        return qEduService.listStudentsForDirectory(qEduService.currentUser(authentication), classId, subjectId);
    }
}
