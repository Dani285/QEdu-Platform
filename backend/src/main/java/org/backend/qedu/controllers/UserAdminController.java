package org.backend.qedu.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.backend.qedu.dto.UserDtos.RegisterRequest;
import org.backend.qedu.dto.UserDtos.UserDto;
import org.backend.qedu.dto.UserPatchRequest;
import org.backend.qedu.service.QEduService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserAdminController {

    private final QEduService qEduService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public UserDto create(@Valid @RequestBody RegisterRequest request) {
        return qEduService.registerNewUser(request);
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public List<UserDto> list() {
        return qEduService.listAllUsers();
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public UserDto getOne(@PathVariable Long id) {
        return qEduService.getUserById(id);
    }

    @PatchMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public UserDto patch(@PathVariable Long id, @Valid @RequestBody UserPatchRequest request) {
        return qEduService.patchUser(id, request);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public void delete(@PathVariable Long id, Authentication authentication) {
        qEduService.deleteUser(id, qEduService.currentUser(authentication));
    }
}
