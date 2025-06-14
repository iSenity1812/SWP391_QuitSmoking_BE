package com.swp391project.SWP391_QuitSmoking_BE.service;

import com.swp391project.SWP391_QuitSmoking_BE.dto.LoginRequest;
import com.swp391project.SWP391_QuitSmoking_BE.dto.LoginResponse;
import com.swp391project.SWP391_QuitSmoking_BE.entity.User;
import com.swp391project.SWP391_QuitSmoking_BE.exception.InvalidCredentialsException;
import com.swp391project.SWP391_QuitSmoking_BE.exception.UserNotActiveException;
import com.swp391project.SWP391_QuitSmoking_BE.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthenticationService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private JwtService jwtService;

    public LoginResponse authenticateUser(LoginRequest loginRequest) {
        // Step 1: Find user by username or email
        Optional<User> userOptional = userRepository.findByUsername(loginRequest.getUsername());
        if (userOptional.isEmpty()) {
            userOptional = userRepository.findByEmail(loginRequest.getUsername());
        }

        if (userOptional.isEmpty()) {
            throw new InvalidCredentialsException("User not found");
        }

        User user = userOptional.get();

        // Step 2: Check if user is active
        if (!user.isActive()) {
            throw new UserNotActiveException("Account is not active");
        }

        // Step 3: Verify password
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPasswordHash())) {
            throw new InvalidCredentialsException("Invalid password");
        }

        // Step 4: Generate JWT token
        String token = jwtService.generateToken(user);

        // Step 5: Return login response
        return new LoginResponse(
                token,
                user.getUserId(),
                user.getUsername(),
                user.getEmail(),
                user.getRole().name(), // <-- ĐÃ SỬA: dùng .name() thay vì .getRoleName()
                user.isActive()
        );
    }

    public void logout(String token) {
        // Add token to blacklist
        jwtService.blacklistToken(token);
    }
}
