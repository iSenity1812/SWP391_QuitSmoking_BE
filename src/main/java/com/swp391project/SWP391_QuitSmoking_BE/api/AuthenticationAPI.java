package com.swp391project.SWP391_QuitSmoking_BE.api;

import com.swp391project.SWP391_QuitSmoking_BE.dto.AccountResponse;
import com.swp391project.SWP391_QuitSmoking_BE.dto.LoginRequest;
import com.swp391project.SWP391_QuitSmoking_BE.dto.RegisterRequest;
import com.swp391project.SWP391_QuitSmoking_BE.entity.User;
import com.swp391project.SWP391_QuitSmoking_BE.services.AuthenticationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationAPI {
    @Autowired
    private AuthenticationService authenticationService;

    @PostMapping("/register")
    public ResponseEntity register(@Valid @RequestBody RegisterRequest registerRequest) {
        AccountResponse newUser = authenticationService.registerUser(registerRequest);
        return ResponseEntity.ok(newUser);
    }

    @PostMapping("/login")
    public ResponseEntity login(@Valid @RequestBody LoginRequest loginRequest) throws Exception {
        AccountResponse authentication = authenticationService.authenticateUser(loginRequest);
        return ResponseEntity.ok("Login successful: " + authentication.getUsername() + "\n" +
                "Token: " + authentication.getToken());
    }
}
