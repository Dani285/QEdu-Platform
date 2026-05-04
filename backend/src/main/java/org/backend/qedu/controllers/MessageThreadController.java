package org.backend.qedu.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.backend.qedu.dto.MessageThreadRequest;
import org.backend.qedu.entities.MessageThread;
import org.backend.qedu.service.QEduService;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/messages")
@RequiredArgsConstructor
public class MessageThreadController {

    private final QEduService qEduService;

    @GetMapping("/threads")
    public List<MessageThread> list(Authentication authentication) {
        return qEduService.listMessageThreads(qEduService.currentUser(authentication));
    }

    @PostMapping("/threads")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    public MessageThread create(
            Authentication authentication,
            @Valid @RequestBody MessageThreadRequest request
    ) {
        return qEduService.createMessageThread(qEduService.currentUser(authentication), request);
    }

    @PutMapping("/threads/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    public MessageThread update(
            Authentication authentication,
            @PathVariable Long id,
            @Valid @RequestBody MessageThreadRequest request
    ) {
        return qEduService.updateMessageThread(qEduService.currentUser(authentication), id, request);
    }

    @DeleteMapping("/threads/{id}")
    @PreAuthorize("hasAnyRole('ADMIN','TEACHER')")
    public void delete(Authentication authentication, @PathVariable Long id) {
        qEduService.deleteMessageThread(qEduService.currentUser(authentication), id);
    }
}
