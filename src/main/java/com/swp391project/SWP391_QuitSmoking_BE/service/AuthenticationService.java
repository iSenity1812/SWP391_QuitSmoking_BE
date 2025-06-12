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
import java.util.Map;

@Service
public class AuthenticationService implements UserDetailsService {

    private final AuthenticationRepository authenticationRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final ModelMapper modelMapper;


    @Autowired
    private EmailService emailService;

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
                    loginRequest.getEmail(), // Có thể là email hoặc username
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
    public AccountResponse registerUser(RegisterRequest registerRequest) {
    // 1. Kiem tra email
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
//            System.out.println("Gửi email thành công đến: " + recipient);
//        } catch (Exception e) {
//            throw new RuntimeException("Failed to send welcome email: " + e.getMessage());
//        }
//        System.out.println("Send email thành công");
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
        User user = authenticationRepository.findByEmail(identify)
                .orElseThrow(() -> new UsernameNotFoundException("User not found with email: " + identify));
        if (!user.isActive()) throw new UsernameNotFoundException("This account with " + identify + " is locked");
        return user;
    }
}
