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
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    private final LogoutService logoutService;

    @PostMapping("/register")
    public ResponseEntity<ApiResponse<AccountResponse>> register(@Valid @RequestBody RegisterRequest registerRequest) {
        AccountResponse newUser = authenticationService.registerUser(registerRequest);
        return ResponseEntity
                .status(201) // HTTP 201 Created is standard for successful resource creation
                .body(ApiResponse.success(newUser, "Registration successful."));
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse<AccountResponse>> login(@Valid @RequestBody LoginRequest loginRequest) {
        AccountResponse authentication = authenticationService.authenticateUser(loginRequest);
        log.info("User {} logged in successfully", authentication.getUsername());
        return ResponseEntity
                .status(200) // HTTP 200 OK is standard for successful login
                .body(ApiResponse.success(authentication, "Login successful."));
    }

    @PostMapping("/logout")
    @SecurityRequirement(name = "user_api")
    public ResponseEntity<ApiResponse<String>> logout(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        log.info("User with token {} is logging out", authHeader);
        logoutService.blacklistToken(authHeader);
        return ResponseEntity.ok(ApiResponse.success(null, "You have been logged out successfully."));
    }
}
