package com.swp391project.SWP391_QuitSmoking_BE.service;

import com.swp391project.SWP391_QuitSmoking_BE.dto.email.EmailDetail;
import com.swp391project.SWP391_QuitSmoking_BE.dto.response.AccountResponse;
import com.swp391project.SWP391_QuitSmoking_BE.dto.request.LoginRequest;
import com.swp391project.SWP391_QuitSmoking_BE.dto.request.RegisterRequest;
import com.swp391project.SWP391_QuitSmoking_BE.entity.User;
import com.swp391project.SWP391_QuitSmoking_BE.enums.Role;
import com.swp391project.SWP391_QuitSmoking_BE.repository.AuthenticationRepository;
import com.swp391project.SWP391_QuitSmoking_BE.util.JwtUtil;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthenticationService implements UserDetailsService {

    private final AuthenticationRepository authenticationRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final ModelMapper modelMapper;
    private final MemberService memberService;
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(AuthenticationService.class);

    @Autowired
    private EmailService emailService;


    @Autowired
    public AuthenticationService(
            AuthenticationRepository authenticationRepository,
            PasswordEncoder passwordEncoder,
            @Lazy AuthenticationManager authenticationManager, // Đặt @Lazy ở đây!
            JwtUtil jwtUtil,
            ModelMapper modelMapper,
            MemberService memberService
    ) {
        this.authenticationRepository = authenticationRepository;
        this.passwordEncoder = passwordEncoder;
        this.authenticationManager = authenticationManager;
        this.jwtUtil = jwtUtil;
        this.modelMapper = modelMapper;
        this.memberService = memberService;
    }


    public AccountResponse authenticateUser(LoginRequest loginRequest) {
        Authentication authentication;
        try {
            log.debug("Attempting to authenticate user with email: {}", loginRequest.getEmail());
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            loginRequest.getEmail(),
                            loginRequest.getPassword()
                    )
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
            log.info("User {} authenticated successfully.", loginRequest.getEmail());
        } catch (BadCredentialsException e) {
            log.warn("Authentication failed for user {}: Invalid credentials.", loginRequest.getEmail());
            throw new BadCredentialsException("Invalid email/username or password.");
        } catch (Exception e) {
            log.error("Authentication failed for user {}: {}", loginRequest.getEmail(), e.getMessage(), e);
            throw new RuntimeException("Authentication failed: " + e.getMessage(), e);
        }

        User user = authenticationRepository.findByEmail(loginRequest.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + loginRequest.getEmail()));

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

    @Transactional
    public AccountResponse registerUser(RegisterRequest registerRequest) {
        // 1. Kiem tra email
        log.info("Attempting to register new user with email: {}", registerRequest.getEmail());
        if (authenticationRepository.findByEmail(registerRequest.getEmail()).isPresent()) {
            throw new RuntimeException("Email already exists");
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
        log.info("User {} registered successfully with ID: {}", savedUser.getUsername(), savedUser.getUserId());

        log.info("Creating member record for user ID: {}", savedUser.getUserId());
        memberService.createMemberForUser(savedUser); // Tạo Member cho User mới
        log.info("Member record created for user ID: {}", savedUser.getUserId());

        savedUser = authenticationRepository.findById(savedUser.getUserId()).orElse(savedUser);


        String jwtToken = jwtUtil.generateToken(savedUser);

        // Gửi mail chào mừng khi register  thành công
//        try {
//            String recipient = savedUser.getEmail();
//            String subject = "Chào mừng bạn đã đến với ứng dụng QuitTogether!";
//            Map<String, Object> templateVariables = new HashMap<>();
//
//            // Thêm các biến cần thiết vào templateVariables
//            templateVariables.put("name", savedUser.getUsername());
//            templateVariables.put("link", "http://localhost:5173/login"); // Ví dụ về link đến trang chào mừng
//            templateVariables.put("buttonText", "Bắt đầu ngay"); // Văn bản nút trong email
//            templateVariables.put("websiteUrl", "http://localhost:5173"); // URL của trang web
//            templateVariables.put("supportUrl", "http://localhost:5173/support"); // URL hỗ trợ
//
//            String body = null;
//            String templateName = "welcomeTemplate"; // Tên template email chào mừng
//            EmailDetail emailDetail = new EmailDetail(recipient, subject, body, templateName, templateVariables);
//            emailService.sendEmail(emailDetail);
//            log.info("Welcome email sent successfully to: {}", recipient);
//        } catch (Exception e) {
//            log.error("Failed to send welcome email to {}: {}", savedUser.getEmail(), e.getMessage(), e);
//            throw new RuntimeException("Failed to send welcome email: " + e.getMessage());
//        }

        // Chuyển đổi User sang AccountResponse để trả về
        AccountResponse accountResponse = modelMapper.map(savedUser, AccountResponse.class);
        accountResponse.setToken(jwtToken); // Gán token vào DTO
        return accountResponse;
    }


    // Phương thức loadUserByUsername sẽ được gọi bởi Spring Security
    // trước khi xác thực mật khẩu. Vì vậy, chúng ta sẽ kiểm tra isActive ở đây.
    @Override
    public UserDetails loadUserByUsername(String identify) throws UsernameNotFoundException {
        // Tìm kiếm người dùng theo email
        log.debug("Attempting to load user by username/email: {}", identify);
        User user = authenticationRepository.findByEmail(identify)
                .orElseThrow(() -> {
                    log.warn("User not found with email: {}", identify);
                    return new UsernameNotFoundException("User not found with email: " + identify);
                });
        if (!user.isActive()) {
            log.warn("Account for user {} is locked.", identify);
            throw new UsernameNotFoundException("This account with " + identify + " is locked");
        }
        log.info("User {} loaded successfully.", identify);
        return user;
    }
}
