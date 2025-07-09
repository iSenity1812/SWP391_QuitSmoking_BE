package com.swp391project.SWP391_QuitSmoking_BE.service;

import com.swp391project.SWP391_QuitSmoking_BE.entity.Follow;
import com.swp391project.SWP391_QuitSmoking_BE.entity.User;
import com.swp391project.SWP391_QuitSmoking_BE.repository.FollowRepository;
import com.swp391project.SWP391_QuitSmoking_BE.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FollowService {
    private final FollowRepository followRepository;
    private final UserRepository userRepository;

    @Transactional
    public void follow(UUID followerId, UUID followingId) {
        User follower = userRepository.findByUserId(followerId).orElseThrow(() -> new IllegalArgumentException("Follower not found"));
        User following = userRepository.findByUserId(followingId).orElseThrow(() -> new IllegalArgumentException("Following not found"));
        if (!followRepository.existsByFollowerAndFollowing(follower, following)) {
            Follow follow = new Follow();
            follow.setFollower(follower);
            follow.setFollowing(following);
            follow.setCreatedAt(LocalDateTime.now());
            followRepository.save(follow);
        }
    }

    @Transactional
    public void unfollow(UUID followerId, UUID followingId) {
        User follower = userRepository.findByUserId(followerId).orElseThrow(() -> new IllegalArgumentException("Follower not found"));
        User following = userRepository.findByUserId(followingId).orElseThrow(() -> new IllegalArgumentException("Following not found"));
        followRepository.deleteByFollowerAndFollowing(follower, following);
    }

    public List<Follow> getFollowers(UUID userId) {
        User user = userRepository.findByUserId(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        return followRepository.findByFollowing(user);
    }

    public List<Follow> getFollowing(UUID userId) {
        User user = userRepository.findByUserId(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        return followRepository.findByFollower(user);
    }

    public boolean isFollowing(UUID followerId, UUID followingId) {
        User follower = userRepository.findByUserId(followerId).orElseThrow(() -> new IllegalArgumentException("Follower not found"));
        User following = userRepository.findByUserId(followingId).orElseThrow(() -> new IllegalArgumentException("Following not found"));
        return followRepository.existsByFollowerAndFollowing(follower, following);
    }
} 