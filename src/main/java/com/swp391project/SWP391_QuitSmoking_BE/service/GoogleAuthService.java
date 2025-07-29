package com.swp391project.SWP391_QuitSmoking_BE.service;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.swp391project.SWP391_QuitSmoking_BE.dto.GoogleUserInfo;
import com.swp391project.SWP391_QuitSmoking_BE.dto.response.AuthResponse;
import com.swp391project.SWP391_QuitSmoking_BE.entity.User;
import com.swp391project.SWP391_QuitSmoking_BE.entity.UserOauthAccount;
import com.swp391project.SWP391_QuitSmoking_BE.enums.Role;
import com.swp391project.SWP391_QuitSmoking_BE.repository.AuthenticationRepository;
import com.swp391project.SWP391_QuitSmoking_BE.repository.UserOauthAccountRepository;
import com.swp391project.SWP391_QuitSmoking_BE.util.JwtUtil;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class GoogleAuthService {

    private static final Logger log = LoggerFactory.getLogger(GoogleAuthService.class);

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;

    @Autowired
    private AuthenticationRepository userRepository;

    @Autowired
    private UserOauthAccountRepository userOauthAccountRepository;

    @Autowired
    private MemberService memberService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private UserService userService;

    /**
     * GoogleIdTokenVerifier để xác thực ID token từ Google
     * Sử dụng GsonFactory để parse JSON từ Google
     */

    private final GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(new NetHttpTransport(), new GsonFactory())
            .setAudience(Collections.singletonList(googleClientId))
            .build();

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
            Optional<UserOauthAccount> existingGoogleAccount = userOauthAccountRepository.findByOauthProviderAndOauthId("google", googleId);
            if (existingGoogleAccount.isPresent()) {
                // Google account exists, return user info
                UserOauthAccount googleAccount = existingGoogleAccount.get();
                User user = googleAccount.getUser();

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

    /**
     * Validate và xử lý Google profile picture URL
     * Trả về null nếu URL không hợp lệ để tránh lỗi validation
     */
    private String processGoogleProfilePicture(String pictureUrl) {
        if (pictureUrl == null || pictureUrl.trim().isEmpty()) {
            return null;
        }

        // Kiểm tra nếu là URL Google hợp lệ
        if (pictureUrl.startsWith("https://lh3.googleusercontent.com/") ||
                pictureUrl.startsWith("https://lh4.googleusercontent.com/") ||
                pictureUrl.startsWith("https://lh5.googleusercontent.com/") ||
                pictureUrl.startsWith("https://lh6.googleusercontent.com/")) {
            return pictureUrl;
        }

        // Nếu không phải URL Google hợp lệ, trả về null để tránh lỗi validation
        log.warn("Google profile picture URL không hợp lệ: {}", pictureUrl);
        return null;
    }

    private AuthResponse createNewUserWithGoogleAccount(GoogleUserInfo googleUserInfo) {
        // Create new User
        User newUser = new User();
        newUser.setEmail(googleUserInfo.getEmail());
        newUser.setUsername(generateUsername(googleUserInfo.getEmail()));

        // Xử lý profile picture từ Google một cách an toàn
        String processedPicture = processGoogleProfilePicture(googleUserInfo.getPicture());
        newUser.setProfilePicture(processedPicture);
        newUser.setRole(Role.NORMAL_MEMBER);
        newUser.setActive(true);
        newUser.setCreatedAt(LocalDateTime.now());
        newUser.setNotificationSetting(new HashMap<>());

        // Generate a random password for Google users (they won't use it)
        newUser.setPasswordHash("GOOGLE_AUTH_" + java.util.UUID.randomUUID().toString());

        User savedUser = userRepository.save(newUser);
        log.info("Created new user for Google OAuth: {}", savedUser.getEmail());

        // Create User Oauth Account
        UserOauthAccount userOauthAccount = new UserOauthAccount();
        userOauthAccount.setUserId(savedUser.getUserId());
        userOauthAccount.setOauthProvider("google");
        userOauthAccount.setOauthId(googleUserInfo.getSub());
        userOauthAccountRepository.save(userOauthAccount);

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


    private AuthResponse linkGoogleToExistingUser(User existingUser, GoogleUserInfo googleUserInfo) {
        // Create User Oauth Account for existing user
        UserOauthAccount userOauthAccount = new UserOauthAccount();
        userOauthAccount.setUserId(existingUser.getUserId());
        userOauthAccount.setOauthProvider("google");
        userOauthAccount.setOauthId(googleUserInfo.getSub());
        userOauthAccountRepository.save(userOauthAccount);

        log.info("Linked Google account to existing user: {}", existingUser.getEmail());

        // Update user profile picture if not set
        if (existingUser.getProfilePicture() == null && googleUserInfo.getPicture() != null) {
            String processedPicture = processGoogleProfilePicture(googleUserInfo.getPicture());
            if (processedPicture != null) {
                existingUser.setProfilePicture(processedPicture);
                existingUser.setUpdatedAt(LocalDateTime.now());
                userRepository.save(existingUser);
            }
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
    public AuthResponse linkGoogleAccount(String idTokenString, UUID userId) {
        try {
            GoogleIdToken idToken = verifier.verify(idTokenString);
            if (idToken == null) {
                throw new IllegalArgumentException("Invalid ID token.");
            }

            Payload payload = idToken.getPayload();
            String email = payload.getEmail();
            String name = (String) payload.get("name"); // Get full name from Google
            String pictureUrl = (String) payload.get("picture"); // Get profile picture URL from Google

            if (email == null) {
                throw new IllegalArgumentException("Email not found in Google ID token payload.");
            }

            // Use the new method in UserService to handle user creation/update.
            // This method will handle setting the username and profile picture from Google data.
            User user = userService.createOrUpdateUserFromGoogleOAuth(Map.of(
                    "email", email,
                    "name", name != null ? name : "", // Ensure name is not null for Map.of
                    "picture", pictureUrl != null ? pictureUrl : "" // Ensure pictureUrl is not null for Map.of
            ));

            // Check if UserOauthAccount already exists to prevent 409 Conflict
            Optional<UserOauthAccount> existingOauthAccount = userOauthAccountRepository.findByUserIdAndOauthProvider(user.getUserId(), "google");

            UserOauthAccount userOauthAccount;
            if (existingOauthAccount.isPresent()) {
                userOauthAccount = existingOauthAccount.get();
                // If it exists, update the creation time (or other fields if needed)
                userOauthAccount.setCreatedAt(LocalDateTime.now()); // Update last linked/login time
                userOauthAccountRepository.save(userOauthAccount); // Save the update
            } else {
                // Create a new UserOauthAccount if it doesn't exist
                userOauthAccount = UserOauthAccount.builder()
                        .user(user)
                        .oauthProvider("google")
                        .oauthId(payload.getSubject()) // Unique Google ID for this account
                        .createdAt(LocalDateTime.now())
                        .build();
                userOauthAccountRepository.save(userOauthAccount); // Save the new record
            }

            String jwtToken = jwtUtil.generateToken(user);

            // Build AuthResponse using UserProfile from UserService or directly from User entity
            com.swp391project.SWP391_QuitSmoking_BE.dto.response.AuthResponse.UserInfo userInfo = com.swp391project.SWP391_QuitSmoking_BE.dto.response.AuthResponse.UserInfo.builder()
                    .userId(user.getUserId().toString())
                    .username(user.getUsername())
                    .email(user.getEmail())
                    .role(user.getRole().toString())
                    .profilePicture(user.getProfilePicture())
                    .build();

            return AuthResponse.builder()
                    .success(true)
                    .message("Liên kết tài khoản Google thành công")
                    .token(jwtToken)
                    .userInfo(userInfo)
                    .build();

        } catch (GeneralSecurityException | IOException e) {
            log.error("Google ID token verification failed: {}", e.getMessage(), e);
            throw new RuntimeException("Xác thực ID token Google thất bại: " + e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("Invalid Google ID token or payload: {}", e.getMessage());
            throw new RuntimeException("Token Google không hợp lệ: " + e.getMessage());
        } catch (Exception e) {
            log.error("Failed to link Google account: {}", e.getMessage(), e);
            throw new RuntimeException("Liên kết tài khoản Google thất bại: " + e.getMessage());
        }
    }


    /**
     * Unlink Google account from user by I
     */
    @Transactional
    public void unlinkGoogleAccount(UUID userId) {
        Optional<UserOauthAccount> googleAccount = userOauthAccountRepository.findByUserIdAndOauthProvider(userId, "google");
        if (googleAccount.isPresent()) {
            userOauthAccountRepository.delete(googleAccount.get());
            log.info("Unlinked Google account for user ID: {}", userId);
        }
    }

    /**
     * Check if user has linked Google account
     */
    public boolean hasLinkedGoogleAccount(UUID userId) {
        return userOauthAccountRepository.findByUserIdAndOauthProvider(userId, "google").isPresent();
    }
} 