package com.swp391project.SWP391_QuitSmoking_BE.api;

import com.swp391project.SWP391_QuitSmoking_BE.entity.User;
import com.swp391project.SWP391_QuitSmoking_BE.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/public/profile")
@RequiredArgsConstructor
public class PublicProfileController {
    private final UserRepository userRepository;

    @GetMapping("/{userId}")
    public ResponseEntity<?> getPublicProfile(@PathVariable UUID userId) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isPresent()) {
            return ResponseEntity.ok(new PublicUserProfileDTO(user.get()));
        } else {
            return ResponseEntity.notFound().build();
        }
    }
} 