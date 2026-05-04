package org.backend.qedu.controllers;

import org.backend.qedu.dto.UserDtos.UserDto;
import org.backend.qedu.dto.UserDtos.LoginRequests;
import org.backend.qedu.dto.UserDtos.RegisterRequest;
import org.backend.qedu.dto.UserDtos.Message;
import org.backend.qedu.entities.User;
import org.backend.qedu.repo.UserRepo;
import org.backend.qedu.service.QEduService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.*;
import org.springframework.security.web.context.SecurityContextRepository;
import org.springframework.web.bind.annotation.*;
import org.springframework.stereotype.Service;

@RestController
@Service
@RequestMapping("/api/auth")
@RequiredArgsConstructor

public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final SecurityContextRepository securityContextRepository;
    private final UserRepo userRepo;
    private final QEduService qEduService;

    @PostMapping("/register")
    public UserDto register(@Valid @RequestBody RegisterRequest request) {
        return qEduService.registerNewUser(request);
    }

    @PostMapping("/login")
    public UserDto login(
            @Valid @RequestBody LoginRequests request,
            HttpServletRequest httpRequest,
            HttpServletResponse httpResponse
            ){
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(request.userName(),request.Password()));

        SecurityContext context = SecurityContextHolder.createEmptyContext();
        context.setAuthentication(authentication);
        SecurityContextHolder.setContext(context);

        securityContextRepository.saveContext(context,httpRequest,httpResponse);

        User user = userRepo.findByUserName(authentication.getName()).orElseThrow();
        return UserDto.from(user);
    }
    @GetMapping("/me")
    public UserDto me(Authentication authentication){
        return UserDto.from(qEduService.currentUser(authentication));
    }
    @PostMapping("/logout")
    public Message logout(HttpServletRequest request){

        var session = request.getSession(false);
        if(session != null){
            session.invalidate();
        }
        SecurityContextHolder.clearContext();;
        return new Message("Logged out from QEdu");
    }
}
