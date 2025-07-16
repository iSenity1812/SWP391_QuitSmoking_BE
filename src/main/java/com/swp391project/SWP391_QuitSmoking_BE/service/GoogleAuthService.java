package com.swp391project.SWP391_QuitSmoking_BE.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.swp391project.SWP391_QuitSmoking_BE.dto.GoogleUserInfo;
import com.swp391project.SWP391_QuitSmoking_BE.dto.response.AuthResponse;
import com.swp391project.SWP391_QuitSmoking_BE.entity.GoogleOAuthAccount;
import com.swp391project.SWP391_QuitSmoking_BE.entity.User;
import com.swp391project.SWP391_QuitSmoking_BE.enums.Role;
import com.swp391project.SWP391_QuitSmoking_BE.repository.AuthenticationRepository;
import com.swp391project.SWP391_QuitSmoking_BE.repository.GoogleOAuthAccountRepository;
import com.swp391project.SWP391_QuitSmoking_BE.util.JwtUtil;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashMap;
import java.util.Optional;
import java.util.UUID;

@Service
public class GoogleAuthService {
    
    private static final Logger log = LoggerFactory.getLogger(GoogleAuthService.class);
    
    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;
    
    @Autowired
    private AuthenticationRepository userRepository;
    
    @Autowired
    private GoogleOAuthAccountRepository googleOAuthAccountRepository;
    
    @Autowired
    private MemberService memberService;
    
    @Autowired
    private JwtUtil jwtUtil;
    
    @Autowired
    private ModelMapper modelMapper;
    
    public AuthResponse authenticateWithGoogle(String googleId, String email, String name, String picture) {
        try {
            // Create GoogleUserInfo from provided data
            GoogleUserInfo googleUserInfo = new GoogleUserInfo();
            googleUserInfo.setSub(googleId);
            googleUserInfo.setEmail(email);
            googleUserInfo.setName(name);
            googleUserInfo.setPicture(picture);
            googleUserInfo.setEmail_verified(true); // Assume verified since we got user info
            
            // Check if Google OAuth account exists
            Optional<GoogleOAuthAccount> existingGoogleAccount = googleOAuthAccountRepository.findByGoogleId(googleId);
            if (existingGoogleAccount.isPresent()) {
                // Google account exists, return user info
                GoogleOAuthAccount googleAccount = existingGoogleAccount.get();
                User user = googleAccount.getUser();
                
                // Update last login time
                googleAccount.setLastLoginAt(LocalDateTime.now());
                googleOAuthAccountRepository.save(googleAccount);
                
                String jwtToken = jwtUtil.generateToken(user);
                AuthResponse.UserInfo userInfo = AuthResponse.UserInfo.builder()
                        .userId(user.getUserId().toString())
                        .username(user.getUsername())
                        .email(user.getEmail())
                        .role(user.getRole().toString())
                        .profilePicture(user.getProfilePicture())
                        .build();
                
                AuthResponse authResponse = AuthResponse.builder()
                        .success(true)
                        .message("Đăng nhập Google thành công")
                        .token(jwtToken)
                        .userInfo(userInfo)
                        .build();
                
                log.info("Google user logged in: {}", user.getEmail());
                return authResponse;
            }
            
            // Check if email already exists in User table
            Optional<User> existingUserByEmail = userRepository.findByEmail(email);
            if (existingUserByEmail.isPresent()) {
                // Link Google account to existing user
                return linkGoogleToExistingUser(existingUserByEmail.get(), googleUserInfo);
            }
            
            // Create new user and Google OAuth account
            return createNewUserWithGoogleAccount(googleUserInfo);
            
        } catch (Exception e) {
            log.error("Google authentication failed: {}", e.getMessage(), e);
            throw new RuntimeException("Xác thực Google thất bại: " + e.getMessage());
        }
    }
    
    private GoogleUserInfo verifyGoogleIdToken(String idToken) {
        try {
            GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
                    .setAudience(Collections.singletonList(googleClientId))
                    .build();
            
            GoogleIdToken googleIdToken = verifier.verify(idToken);
            if (googleIdToken != null) {
                Payload payload = googleIdToken.getPayload();
                
                GoogleUserInfo userInfo = new GoogleUserInfo();
                userInfo.setSub(payload.getSubject());
                userInfo.setName((String) payload.get("name"));
                userInfo.setGiven_name((String) payload.get("given_name"));
                userInfo.setFamily_name((String) payload.get("family_name"));
                userInfo.setPicture((String) payload.get("picture"));
                userInfo.setEmail(payload.getEmail());
                userInfo.setEmail_verified(payload.getEmailVerified());
                userInfo.setLocale((String) payload.get("locale"));
                
                return userInfo;
            }
        } catch (Exception e) {
            log.error("Error verifying Google ID token: {}", e.getMessage(), e);
        }
        return null;
    }
    
    @Transactional
    private AuthResponse createNewUserWithGoogleAccount(GoogleUserInfo googleUserInfo) {
        // Create new User
        User newUser = new User();
        newUser.setEmail(googleUserInfo.getEmail());
        newUser.setUsername(generateUsername(googleUserInfo.getEmail()));
        newUser.setProfilePicture(googleUserInfo.getPicture());
        newUser.setRole(Role.NORMAL_MEMBER);
        newUser.setActive(true);
        newUser.setCreatedAt(LocalDateTime.now());
        newUser.setNotificationSetting(new HashMap<>());
        
        // Generate a random password for Google users (they won't use it)
        newUser.setPasswordHash("GOOGLE_AUTH_" + java.util.UUID.randomUUID().toString());
        
        User savedUser = userRepository.save(newUser);
        log.info("Created new user for Google OAuth: {}", savedUser.getEmail());
        
        // Create Google OAuth Account
        GoogleOAuthAccount googleAccount = new GoogleOAuthAccount();
        googleAccount.setGoogleId(googleUserInfo.getSub());
        googleAccount.setEmail(googleUserInfo.getEmail());
        googleAccount.setName(googleUserInfo.getName());
        googleAccount.setGivenName(googleUserInfo.getGiven_name());
        googleAccount.setFamilyName(googleUserInfo.getFamily_name());
        googleAccount.setPictureUrl(googleUserInfo.getPicture());
        googleAccount.setLocale(googleUserInfo.getLocale());
        googleAccount.setEmailVerified(googleUserInfo.isEmail_verified());
        googleAccount.setIsActive(true);
        googleAccount.setUser(savedUser);
        googleAccount.setLastLoginAt(LocalDateTime.now());
        
        googleOAuthAccountRepository.save(googleAccount);
        log.info("Created Google OAuth account for user: {}", savedUser.getEmail());
        
        // Create member record
        memberService.createMemberForUser(savedUser);
        
        String jwtToken = jwtUtil.generateToken(savedUser);
        AuthResponse.UserInfo userInfo = AuthResponse.UserInfo.builder()
                .userId(savedUser.getUserId().toString())
                .username(savedUser.getUsername())
                .email(savedUser.getEmail())
                .role(savedUser.getRole().toString())
                .profilePicture(savedUser.getProfilePicture())
                .build();
        
        AuthResponse authResponse = AuthResponse.builder()
                .success(true)
                .message("Tạo tài khoản Google thành công")
                .token(jwtToken)
                .userInfo(userInfo)
                .build();
        
        return authResponse;
    }
    
    @Transactional
    private AuthResponse linkGoogleToExistingUser(User existingUser, GoogleUserInfo googleUserInfo) {
        // Create Google OAuth Account for existing user
        GoogleOAuthAccount googleAccount = new GoogleOAuthAccount();
        googleAccount.setGoogleId(googleUserInfo.getSub());
        googleAccount.setEmail(googleUserInfo.getEmail());
        googleAccount.setName(googleUserInfo.getName());
        googleAccount.setGivenName(googleUserInfo.getGiven_name());
        googleAccount.setFamilyName(googleUserInfo.getFamily_name());
        googleAccount.setPictureUrl(googleUserInfo.getPicture());
        googleAccount.setLocale(googleUserInfo.getLocale());
        googleAccount.setEmailVerified(googleUserInfo.isEmail_verified());
        googleAccount.setIsActive(true);
        googleAccount.setUser(existingUser);
        googleAccount.setLastLoginAt(LocalDateTime.now());
        
        googleOAuthAccountRepository.save(googleAccount);
        log.info("Linked Google account to existing user: {}", existingUser.getEmail());
        
        // Update user profile picture if not set
        if (existingUser.getProfilePicture() == null && googleUserInfo.getPicture() != null) {
            existingUser.setProfilePicture(googleUserInfo.getPicture());
            existingUser.setUpdatedAt(LocalDateTime.now());
            userRepository.save(existingUser);
        }
        
        String jwtToken = jwtUtil.generateToken(existingUser);
        AuthResponse.UserInfo userInfo = AuthResponse.UserInfo.builder()
                .userId(existingUser.getUserId().toString())
                .username(existingUser.getUsername())
                .email(existingUser.getEmail())
                .role(existingUser.getRole().toString())
                .profilePicture(existingUser.getProfilePicture())
                .build();
        
        AuthResponse authResponse = AuthResponse.builder()
                .success(true)
                .message("Liên kết tài khoản Google thành công")
                .token(jwtToken)
                .userInfo(userInfo)
                .build();
        
        return authResponse;
    }
    
    private String generateUsername(String email) {
        String baseUsername = email.split("@")[0];
        String username = baseUsername;
        int counter = 1;
        
        while (userRepository.findByUsername(username).isPresent()) {
            username = baseUsername + counter;
            counter++;
        }
        
        return username;
    }
    
    /**
     * Link Google account to current user
     */
    @Transactional
    public AuthResponse linkGoogleAccount(String googleId) {
        try {
            // Check if Google account already exists
            Optional<GoogleOAuthAccount> existingAccount = googleOAuthAccountRepository.findByGoogleId(googleId);
            if (existingAccount.isPresent()) {
                throw new RuntimeException("Google account đã được liên kết với tài khoản khác");
            }
            
            // Get current user from security context (you'll need to implement this)
            // For now, we'll throw an exception
            throw new RuntimeException("Chưa implement lấy current user");
            
        } catch (Exception e) {
            log.error("Failed to link Google account: {}", e.getMessage(), e);
            throw new RuntimeException("Liên kết tài khoản Google thất bại: " + e.getMessage());
        }
    }
    
    /**
     * Unlink Google account from current user
     */
    @Transactional
    public AuthResponse unlinkGoogleAccount() {
        try {
            // Get current user from security context (you'll need to implement this)
            // For now, we'll throw an exception
            throw new RuntimeException("Chưa implement lấy current user");
            
        } catch (Exception e) {
            log.error("Failed to unlink Google account: {}", e.getMessage(), e);
            throw new RuntimeException("Hủy liên kết tài khoản Google thất bại: " + e.getMessage());
        }
    }
    
    /**
     * Unlink Google account from user by ID
     */
    @Transactional
    public void unlinkGoogleAccount(UUID userId) {
        Optional<GoogleOAuthAccount> googleAccount = googleOAuthAccountRepository.findByUser_UserId(userId);
        if (googleAccount.isPresent()) {
            googleOAuthAccountRepository.delete(googleAccount.get());
            log.info("Unlinked Google account for user ID: {}", userId);
        }
    }
    
    /**
     * Check if user has linked Google account
     */
    public boolean hasLinkedGoogleAccount(UUID userId) {
        return googleOAuthAccountRepository.findByUser_UserId(userId).isPresent();
    }
} 