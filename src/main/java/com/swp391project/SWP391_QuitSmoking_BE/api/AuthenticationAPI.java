package com.swp391project.SWP391_QuitSmoking_BE.api;

import com.swp391project.SWP391_QuitSmoking_BE.dto.LoginRequest;
import com.swp391project.SWP391_QuitSmoking_BE.dto.LoginResponse;
import com.swp391project.SWP391_QuitSmoking_BE.exception.InvalidCredentialsException;
import com.swp391project.SWP391_QuitSmoking_BE.exception.UserNotActiveException;
import com.swp391project.SWP391_QuitSmoking_BE.service.AuthenticationService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin(origins = "*")
public class AuthenticationAPI {

    @Autowired
    private AuthenticationService authenticationService;

    @PostMapping("/login")
    public ResponseEntity<?> login(@Valid @RequestBody LoginRequest loginRequest) {
        try {
            LoginResponse response = authenticationService.authenticateUser(loginRequest);
            return ResponseEntity.ok(response);
        } catch (InvalidCredentialsException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Invalid username or password"));
        } catch (UserNotActiveException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(new ErrorResponse("Account is not active"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Login failed: " + e.getMessage()));
        }
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestHeader("Authorization") String token) {
        try {
            authenticationService.logout(token);
            return ResponseEntity.ok(new SuccessResponse("Logout successful"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new ErrorResponse("Logout failed"));
        }
    }

    // Helper classes for responses
    public static class ErrorResponse {
        private String message;
        private String status = "error";

        public ErrorResponse(String message) {
            this.message = message;
        }

        public String getMessage() { return message; }
        public String getStatus() { return status; }
    }

    public static class SuccessResponse {
        private String message;
        private String status = "success";

        public SuccessResponse(String message) {
            this.message = message;
        }

        public String getMessage() { return message; }
        public String getStatus() { return status; }
    }
}
