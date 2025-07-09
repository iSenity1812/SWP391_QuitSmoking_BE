package com.swp391project.SWP391_QuitSmoking_BE.api;

import com.swp391project.SWP391_QuitSmoking_BE.entity.Follow;
import com.swp391project.SWP391_QuitSmoking_BE.service.FollowService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/follow")
@RequiredArgsConstructor
public class FollowController {
    private final FollowService followService;

    @PostMapping("/{userId}")
    public ResponseEntity<Void> follow(
            @RequestHeader("X-User-Id") UUID followerId,
            @PathVariable("userId") UUID followingId) {
        followService.follow(followerId, followingId);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<Void> unfollow(
            @RequestHeader("X-User-Id") UUID followerId,
            @PathVariable("userId") UUID followingId) {
        followService.unfollow(followerId, followingId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/followers/{userId}")
    public ResponseEntity<List<Follow>> getFollowers(@PathVariable UUID userId) {
        return ResponseEntity.ok(followService.getFollowers(userId));
    }

    @GetMapping("/following/{userId}")
    public ResponseEntity<List<PublicUserProfileDTO>> getFollowing(@PathVariable UUID userId) {
        List<Follow> follows = followService.getFollowing(userId);
        List<PublicUserProfileDTO> followingUsers = follows.stream()
            .map(f -> new PublicUserProfileDTO(f.getFollowing()))
            .collect(Collectors.toList());
        return ResponseEntity.ok(followingUsers);
    }

    @GetMapping("/is-following/{userId}")
    public ResponseEntity<Boolean> isFollowing(
            @RequestHeader("X-User-Id") UUID followerId,
            @PathVariable("userId") UUID followingId) {
        return ResponseEntity.ok(followService.isFollowing(followerId, followingId));
    }
} 