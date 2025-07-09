package com.swp391project.SWP391_QuitSmoking_BE.service;

import com.swp391project.SWP391_QuitSmoking_BE.dto.coach.CoachProfile;
import com.swp391project.SWP391_QuitSmoking_BE.dto.normalMember.MemberResponse;
import com.swp391project.SWP391_QuitSmoking_BE.dto.request.AdminUserCreateRequest;
import com.swp391project.SWP391_QuitSmoking_BE.dto.request.UserProfile;
import com.swp391project.SWP391_QuitSmoking_BE.dto.request.UserUpdateRequest;
import com.swp391project.SWP391_QuitSmoking_BE.dto.response.UserQuitStatsResponse;
import com.swp391project.SWP391_QuitSmoking_BE.entity.User;
import com.swp391project.SWP391_QuitSmoking_BE.entity.QuitPlan;
import com.swp391project.SWP391_QuitSmoking_BE.entity.DailySummary;
import com.swp391project.SWP391_QuitSmoking_BE.enums.Role;
import com.swp391project.SWP391_QuitSmoking_BE.enums.QuitPlanStatus;
import com.swp391project.SWP391_QuitSmoking_BE.exception.DuplicateEmailException;
import com.swp391project.SWP391_QuitSmoking_BE.exception.DuplicateUsernameException;
import com.swp391project.SWP391_QuitSmoking_BE.exception.ResourceNotFoundException;
import com.swp391project.SWP391_QuitSmoking_BE.repository.UserRepository;
import com.swp391project.SWP391_QuitSmoking_BE.repository.QuitPlanRepository;
import com.swp391project.SWP391_QuitSmoking_BE.repository.DailySummaryRepository;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final MemberService memberService;
    private final CoachService coachService;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final QuitPlanRepository quitPlanRepository;
    private final DailySummaryRepository dailySummaryRepository;

    public List<UserProfile> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream().map(user -> {
            UserProfile userProfile = modelMapper.map(user, UserProfile.class);
            userProfile.setUserId(user.getUserId());
            return userProfile;
        }).toList();
    }

    //Lấy danh sách Member cho Admin
    public List<UserProfile> getAllMembers() {
        List<UserProfile> users = getAllUsers();
        return users.stream()
                .filter(user -> (user.getRole() == Role.NORMAL_MEMBER || user.getRole() == Role.PREMIUM_MEMBER))
                .collect(Collectors.toList());
    }

    //Lấy danh sách Coach cho Admin
    public List<CoachProfile> getAllCoaches() {
         List<User> users = userRepository.findAll();
         return users.stream()
                 .filter(user -> user.getRole() == Role.COACH)
                 .map(user -> {
                     CoachProfile coachProfile = modelMapper.map(user, CoachProfile.class);
                     // Đảm bảo coach được tải để có fullName và bio
                     if (user.getCoach() != null) {
                         coachProfile.setFullName(user.getCoach().getFullName());
                         coachProfile.setCoachBio(user.getCoach().getCoachBio());
                     }
                     return coachProfile;
                 })
                 .collect(Collectors.toList());
    }

    public UUID getUserIdFromUserDetails(UserDetails userDetails) {
        // Tìm User trong database bằng username từ UserDetails (ít hiệu quả hơn vì cần thêm DB lookup)
        // Đây là cách sẽ hoạt động với UserDetails mặc định của Spring.
        String username = userDetails.getUsername();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found for username: " + username + " in security context."));
        return user.getUserId(); // Giả định User entity của bạn có trường getUserId()
    }

    public UserProfile getUserById(UUID userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
        return modelMapper.map(user, UserProfile.class);
    }

    private User getUserEntity(UUID userId) {
        return userRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + userId));
    }

    // MODULAR UPDATE METHODS
    // 1. Update username
    @Transactional
    public UserProfile updateUsername(UUID userId, String newUsername) {
        User user = getUserEntity(userId);
        if (newUsername != null && !newUsername.trim().isEmpty()) {
            String trimmedUsername = newUsername.trim();
            if (!trimmedUsername.equalsIgnoreCase(user.getUsername())) {
                // Kiểm tra xem username đã tồn tại chưa
//                if (userRepository.existsByUsername(trimmedUsername)) {
//                    throw new DuplicateUsernameException("Username already exists: " + trimmedUsername);
//                }
                user.setUsername(trimmedUsername);
                user.setUpdatedAt(LocalDateTime.now());
            }
        }
        User updatedUser = userRepository.save(user);
        return modelMapper.map(updatedUser, UserProfile.class);
    }

    // 2. Update email
    @Transactional
    public UserProfile updateEmail(UUID userId, String newEmail) {
        User user = getUserEntity(userId);
        if (newEmail != null && !newEmail.trim().isEmpty()) {
            String trimmedEmail = newEmail.trim();
            if (!trimmedEmail.equalsIgnoreCase(user.getEmail())) {
                // Kiểm tra xem email đã tồn tại chưa
                if (userRepository.existsByEmail(trimmedEmail)) {
                    throw new DuplicateEmailException("Email already exists: " + trimmedEmail);
                }
                user.setEmail(trimmedEmail);
                user.setUpdatedAt(LocalDateTime.now());
            }
        }
        User updatedUser = userRepository.save(user);
        return modelMapper.map(updatedUser, UserProfile.class);
    }

    // Bulk Update
    @Transactional
    public UserProfile updateUserProfile(UUID userId, UserUpdateRequest userUpdateRequest) {
        User user = getUserEntity(userId);
        boolean hasChanges = false;

        // Update username
        if (userUpdateRequest.getUsername() != null && !userUpdateRequest.getUsername().trim().isEmpty()) {
            String newUsername = userUpdateRequest.getUsername().trim();
            if (!newUsername.equals(user.getUsername())) {
//                if (userRepository.existsByUsername(newUsername)) {
//                    throw new DuplicateUsernameException("Username already exists: " + newUsername);
//                }
                System.out.println("Username diff");
                user.setUsername(newUsername);
                hasChanges = true;
            }
        }
        // log
        System.out.println("Username updated");

        // Update email
        if (userUpdateRequest.getEmail() != null && !userUpdateRequest.getEmail().trim().isEmpty()) {
            String newEmail = userUpdateRequest.getEmail().trim();
            if (!newEmail.equals(user.getEmail())) {
                if (userRepository.existsByEmail(newEmail)) {
                    throw new DuplicateEmailException("Email already exists: " + newEmail);
                }
                user.setEmail(newEmail);
                hasChanges = true;
            }
        }

        System.out.println("Email updated");

        // Update avatar
        if (userUpdateRequest.getProfilePicture() != null && !userUpdateRequest.getProfilePicture().trim().isEmpty()) {
            user.setProfilePicture(userUpdateRequest.getProfilePicture());
            hasChanges = true;
        }

        System.out.println("picture updated");

        // Update password (no need to check confirm password here)
        if (userUpdateRequest.getPassword() != null && !userUpdateRequest.getPassword().trim().isEmpty()) {
            user.setPasswordHash(passwordEncoder.encode(userUpdateRequest.getPassword()));
            hasChanges = true;
        }

        System.out.println("password updated");

        // Only update timestamp if there are actual changes
        if (hasChanges) {
            user.setUpdatedAt(LocalDateTime.now());
        }

        User updatedUser = userRepository.save(user);
        System.out.println("saved change");
        return modelMapper.map(updatedUser, UserProfile.class);
    }


    // Create a new user with admin privileges
    @Transactional
    public UserProfile createUser(AdminUserCreateRequest request) {
        // Kiểm tra xem username hoặc email đã tồn tại chưa
        if (userRepository.existsByUsername(request.getUsername())) {
            throw new DuplicateUsernameException("Username already exists: " + request.getUsername());
        }
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new DuplicateEmailException("Email already exists: " + request.getEmail());
        }

        // Mã hóa mật khẩu
        User newUser = new User();
        newUser.setUsername(request.getUsername());
        newUser.setEmail(request.getEmail());
        newUser.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        newUser.setRole(request.getRole());
        newUser.setActive(true); // Mặc định là active
//        newUser.setProfilePicture(request.getAvatarUrl());
        newUser.setProfilePicture(null); // Mặc định không có ảnh đại diện
        newUser.setCreatedAt(LocalDateTime.now());
        newUser.setUpdatedAt(LocalDateTime.now());
        newUser.setNotificationSetting(new HashMap<>());

        User savedUser = userRepository.save(newUser);

        if (savedUser.getRole() == Role.NORMAL_MEMBER) {
            memberService.createMemberForUser(savedUser);
        } else if (savedUser.getRole() == Role.COACH) {
            coachService.createCoachForUser(savedUser, request.getFullName(), request.getCoachBio());
        }

        return modelMapper.map(savedUser, UserProfile.class);
    }

    // Vô hiệu hóa active = false
    @Transactional
    public UserProfile deactivateUser(UUID userId) {
        User userToDeactivate = userRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User with ID: " + userId + " is not exist."));
        if (!userToDeactivate.isActive()) {
            throw new IllegalStateException("User with ID: " + userId + " was not active anymore.");
        }
        userToDeactivate.setActive(false);
        userToDeactivate.setUpdatedAt(LocalDateTime.now());
        User updatedUser = userRepository.save(userToDeactivate);
        return modelMapper.map(updatedUser, UserProfile.class);
    }

    // Kích hoạt lại active = true
    @Transactional
    public UserProfile activateUser(UUID userId) {
        User userToActivate = userRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User with ID: " + userId + " is not exist."));
        if (userToActivate.isActive()) {
            throw new IllegalStateException("User with ID:: " + userId + " is activating.");
        }

        userToActivate.setActive(true);
        userToActivate.setUpdatedAt(LocalDateTime.now());
        User updatedUser = userRepository.save(userToActivate);
        return modelMapper.map(updatedUser, UserProfile.class);
    }

    // Chuyển đổi qua lại
    @Transactional
    public UserProfile toggleUserStatus(UUID userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User with ID: " + userId + " is not exist."));
        user.setActive(!user.isActive());
        user.setUpdatedAt(LocalDateTime.now());
        User updatedUser = userRepository.save(user);
        return modelMapper.map(updatedUser, UserProfile.class);
    }

    // Xóa người dùng
    @Transactional
    public void deleteUser(UUID userId) {
        User user = userRepository.findByUserId(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User with ID: " + userId + " is not exist."));
        userRepository.delete(user);
    }

    // Đổi mật khẩu cho user hiện tại
    @Transactional
    public void changePassword(UUID userId, String currentPassword, String newPassword) {
        User user = getUserEntity(userId);
        if (!passwordEncoder.matches(currentPassword, user.getPasswordHash())) {
            throw new org.springframework.security.authentication.BadCredentialsException("Mật khẩu hiện tại không đúng");
        }
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    public UserQuitStatsResponse getUserQuitStats(UUID memberId) {
        // 1. Lấy QuitPlan mới nhất (ưu tiên IN_PROGRESS, fallback gần nhất)
        QuitPlan plan = quitPlanRepository.findFirstByMember_MemberIdAndStatusOrderByCreatedAtDesc(memberId, QuitPlanStatus.IN_PROGRESS)
            .orElseGet(() -> quitPlanRepository.findByMember_MemberIdOrderByCreatedAtDesc(memberId).orElse(null));
        if (plan == null) {
            throw new ResourceNotFoundException("No quit plan found");
        }
        LocalDate startDate = plan.getStartDate().toLocalDate();
        int cigarettesPerDay = plan.getInitialSmokingAmount();
        int pricePerPack = plan.getPricePerPack().intValue();
        int cigarettesPerPack = plan.getCigarettesPerPack();
        int pricePerCigarette = pricePerPack / cigarettesPerPack;

        // 2. Số ngày không hút
        long days = ChronoUnit.DAYS.between(startDate, LocalDate.now());

        // 3. Tổng số điếu lẽ ra hút
        long totalShouldSmoke = days * cigarettesPerDay;

        // 4. Tổng số điếu thực tế hút
        List<DailySummary> summaries = dailySummaryRepository.findByQuitPlan_QuitPlanId(plan.getQuitPlanId());
        long totalActualSmoked = summaries.stream().mapToLong(DailySummary::getTotalSmokedCount).sum();

        // 5. Điếu đã tránh
        long avoided = totalShouldSmoke - totalActualSmoked;
        if (avoided < 0) avoided = 0;

        // 6. Tiền tiết kiệm
        long moneySaved = avoided * pricePerCigarette;

        return new UserQuitStatsResponse(days, avoided, moneySaved);
    }

    public List<UserProfile> searchUserByEmailOrUsername(String query) {
        List<User> users = userRepository.findByEmailContainingIgnoreCaseOrUsernameContainingIgnoreCase(query, query);
        return users.stream().map(user -> modelMapper.map(user, UserProfile.class)).toList();
    }
}
