package com.swp391project.SWP391_QuitSmoking_BE.services;

import com.swp391project.SWP391_QuitSmoking_BE.config.ModelMapperConfig;
import com.swp391project.SWP391_QuitSmoking_BE.dto.AccountResponse;
import com.swp391project.SWP391_QuitSmoking_BE.dto.LoginRequest;
import com.swp391project.SWP391_QuitSmoking_BE.dto.RegisterRequest;
import com.swp391project.SWP391_QuitSmoking_BE.entity.User;
import com.swp391project.SWP391_QuitSmoking_BE.enums.Role;
import com.swp391project.SWP391_QuitSmoking_BE.repository.AuthenticationRepository;
import com.swp391project.SWP391_QuitSmoking_BE.util.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Lazy;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
public class AuthenticationService implements UserDetailsService {

    private final AuthenticationRepository authenticationRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final ModelMapper modelMapper;

    @Autowired
    public AuthenticationService(
            AuthenticationRepository authenticationRepository,
            PasswordEncoder passwordEncoder,
            @Lazy AuthenticationManager authenticationManager, // Đặt @Lazy ở đây!
            JwtUtil jwtUtil,
            ModelMapper modelMapper
    ) {
        this.authenticationRepository = authenticationRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.modelMapper = modelMapper;
    }


    public AccountResponse authenticateUser(LoginRequest loginRequest) {
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                    loginRequest.getIdentifier(), // Có thể là email hoặc username
                    loginRequest.getPassword()
                )
            );
            // Quan trọng: Set SecurityContextHolder chỉ khi xác thực thành công
            // Nếu không, khi có lỗi sẽ không có authentication để set vào context.
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Invalid email/username or password.");
        } catch (Exception e) {
            throw new RuntimeException("Authentication failed: " + e.getMessage());
        }

        User user = authenticationRepository.findByEmail(loginRequest.getIdentifier())
                .orElseGet(() -> authenticationRepository.findByUsername(loginRequest.getIdentifier())
                        .orElseThrow(() -> new UsernameNotFoundException("User not found after authentication.")));

        String jwtToken = jwtUtil.generateToken(user);


        // Ánh xạ User entity sang AccountResponse DTO
        AccountResponse accountResponse = modelMapper.map(user, AccountResponse.class);
        accountResponse.setToken(jwtToken); // Gán token vào DTO
        return accountResponse;
    }

    /**
     * Register a new user with encoded password.
     * param User details to register.
     * return The registered user details.
     */
    public AccountResponse registerUser(RegisterRequest registerRequest) {
    // 1. Kiem tra email
        if (authenticationRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
        }

        // 2. Kiem tra username
        if (authenticationRepository.findByUsername(registerRequest.getUsername()).isPresent()) {
            throw new RuntimeException("Username already exists");
        }

        User newUser = new User();
        newUser.setUsername(registerRequest.getUsername());
        newUser.setEmail(registerRequest.getEmail());
        newUser.setPasswordHash(passwordEncoder.encode(registerRequest.getPassword()));
        newUser.setCreatedAt(java.time.LocalDateTime.now());
        newUser.setRole(Role.NORMAL_MEMBER); // Mặc định là NORMAL_MEMBER, có thể thay đổi sau
        newUser.setActive(true); // Mặc định là active
        newUser.setProfilePicture(null); // Mặc định không có ảnh đại diện
        newUser.setNotificationSetting(new HashMap<>());

        User savedUser = authenticationRepository.save(newUser);
        String jwtToken = jwtUtil.generateToken(savedUser);

        // Chuyển đổi User sang AccountResponse để trả về
        AccountResponse accountResponse = modelMapper.map(savedUser, AccountResponse.class);
        accountResponse.setToken(jwtToken); // Gán token vào DTO
        return accountResponse;
    }

    @Override
    public UserDetails loadUserByUsername(String identify) throws UsernameNotFoundException {
        // identifier có thể là email hoặc username
        // Tìm theo email trước
        return authenticationRepository.findByEmail(identify)
                .map(user -> (UserDetails) user) // Chuyển đổi User sang UserDetails
                .orElseGet(() -> {
                    // Nếu ko tìm thấy theo email, tìm theo username
                    return authenticationRepository.findByUsername(identify)
                            .orElseThrow(() -> new UsernameNotFoundException("User not found with identifier: " + identify));
                });
    }
}
