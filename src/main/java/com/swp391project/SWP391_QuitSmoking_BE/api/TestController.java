package com.swp391project.SWP391_QuitSmoking_BE.api;

import com.swp391project.SWP391_QuitSmoking_BE.entity.User;
import com.swp391project.SWP391_QuitSmoking_BE.event.UserResistedCravingEvent;
import com.swp391project.SWP391_QuitSmoking_BE.repository.UserRepository;
import com.swp391project.SWP391_QuitSmoking_BE.response.ApiResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/test")
@RequiredArgsConstructor
@SecurityRequirement(name = "user_api")
public class TestController {
    private final ApplicationEventPublisher eventPublisher;
    private final UserRepository userRepository;

    @Operation(summary = "Test trigger craving resisted event", 
               description = "Simulate user resisted craving event for testing achievement unlock")
    @PostMapping("/resist-craving")
    @PreAuthorize("hasRole('NORMAL_MEMBER') or hasRole('PREMIUM_MEMBER')")
    public ResponseEntity<ApiResponse<String>> triggerCravingResistedEvent(
            @AuthenticationPrincipal UserDetails userDetails) {
        try {
            // Lấy thông tin user từ authentication
            String username = userDetails.getUsername();
            User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found: " + username));

            // Publish event
            UserResistedCravingEvent event = new UserResistedCravingEvent(user);
            eventPublisher.publishEvent(event);

            return ResponseEntity.ok(
                ApiResponse.success(
                    "Craving resisted event triggered successfully", 
                    "Event published for user: " + user.getUsername()
                )
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, 
                    "Failed to trigger event: " + e.getMessage()));
        }
    }

    @Operation(summary = "Test trigger craving resisted event for specific user",
               description = "Admin can trigger craving resisted event for any user")
    @PostMapping("/resist-craving/{userId}")
    @PreAuthorize("hasRole('SUPER_ADMIN')")
    public ResponseEntity<ApiResponse<String>> triggerCravingResistedEventForUser(
            @PathVariable UUID userId) {
        try {
            User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));

            // Publish event
            UserResistedCravingEvent event = new UserResistedCravingEvent(user);
            eventPublisher.publishEvent(event);

            return ResponseEntity.ok(
                ApiResponse.success(
                    "Craving resisted event triggered successfully", 
                    "Event published for user: " + user.getUsername()
                )
            );
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ApiResponse.error(HttpStatus.INTERNAL_SERVER_ERROR, 
                    "Failed to trigger event: " + e.getMessage()));
        }
    }
}
