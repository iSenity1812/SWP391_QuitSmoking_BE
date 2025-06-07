package com.swp391project.SWP391_QuitSmoking_BE.api;

import com.swp391project.SWP391_QuitSmoking_BE.dto.response.AccountResponse;
import com.swp391project.SWP391_QuitSmoking_BE.dto.request.LoginRequest;
import com.swp391project.SWP391_QuitSmoking_BE.dto.request.RegisterRequest;
import com.swp391project.SWP391_QuitSmoking_BE.response.ApiResponse;
import com.swp391project.SWP391_QuitSmoking_BE.service.AuthenticationService;
import com.swp391project.SWP391_QuitSmoking_BE.service.LogoutService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    @Autowired
    private AuthenticationService authenticationService;

    private final LogoutService logoutService;

    @PostMapping("/register")
    public ResponseEntity<AccountResponse> register(@Valid @RequestBody RegisterRequest registerRequest) {
        AccountResponse newUser = authenticationService.registerUser(registerRequest);
        return ResponseEntity.ok(newUser);
    }

    @PostMapping("/login")
    public ResponseEntity<AccountResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        AccountResponse authentication = authenticationService.authenticateUser(loginRequest);
        System.out.println("Authentication successful: " + authentication.getUsername());
        return ResponseEntity.ok(authentication);
    }

    @PostMapping("/logout")
    @SecurityRequirement(name = "user_api")
    public ResponseEntity<ApiResponse<String>> logout(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        System.out.println("📋 Authorization header: " + authHeader);
        logoutService.blacklistToken(authHeader);
        return ResponseEntity.ok(ApiResponse.success(null, "You have been logged out successfully."));
    }
}
