package com.swp391project.SWP391_QuitSmoking_BE.service;

import com.swp391project.SWP391_QuitSmoking_BE.dto.follow.FollowResponseDTO;
import com.swp391project.SWP391_QuitSmoking_BE.dto.follow.UserFollowStatsDTO;
import com.swp391project.SWP391_QuitSmoking_BE.entity.Follow;
import com.swp391project.SWP391_QuitSmoking_BE.entity.User;
import com.swp391project.SWP391_QuitSmoking_BE.exception.ResourceNotFoundException;
import com.swp391project.SWP391_QuitSmoking_BE.repository.FollowRepository;
import com.swp391project.SWP391_QuitSmoking_BE.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FollowService {

    private final FollowRepository followRepository;
    private final UserRepository userRepository;

    // Follow một user
    @Transactional
    public FollowResponseDTO followUser(UUID currentUserId, UUID targetUserId) {
        log.info("User {} attempting to follow user {}", currentUserId, targetUserId);

        // Kiểm tra không thể follow chính mình
        if (currentUserId.equals(targetUserId)) {
            throw new IllegalArgumentException("Bạn không thể follow chính mình.");
        }

        // Kiểm tra target user có tồn tại không
        User targetUser = userRepository.findById(targetUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng với ID: " + targetUserId));

        // Kiểm tra target user có active không
        if (!targetUser.isActive()) {
            throw new IllegalArgumentException("Không thể follow người dùng đã bị vô hiệu hóa.");
        }

        // Kiểm tra đã follow chưa
        if (followRepository.existsByFollowerIdAndFollowedId(currentUserId, targetUserId)) {
            throw new IllegalArgumentException("Bạn đã follow người dùng này rồi.");
        }

        // Tạo follow relationship
        Follow follow = new Follow(currentUserId, targetUserId);
        follow = followRepository.save(follow);

        log.info("User {} successfully followed user {}", currentUserId, targetUserId);

        return convertToFollowResponseDTO(follow);
    }

    // Unfollow một user
    @Transactional
    public void unfollowUser(UUID currentUserId, UUID targetUserId) {
        log.info("User {} attempting to unfollow user {}", currentUserId, targetUserId);

        // Kiểm tra follow relationship có tồn tại không
        if (!followRepository.existsByFollowerIdAndFollowedId(currentUserId, targetUserId)) {
            throw new IllegalArgumentException("Bạn chưa follow người dùng này.");
        }

        // Xóa follow relationship
        followRepository.deleteByFollowerIdAndFollowedId(currentUserId, targetUserId);

        log.info("User {} successfully unfollowed user {}", currentUserId, targetUserId);
    }

    // Lấy danh sách followers của một user
    @Transactional(readOnly = true)
    public Page<FollowResponseDTO> getFollowers(UUID userId, Pageable pageable) {
        // Kiểm tra user có tồn tại không
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("Không tìm thấy người dùng với ID: " + userId);
        }

        Page<Follow> followers = followRepository.findFollowersByUserId(userId, pageable);
        return followers.map(this::convertToFollowResponseDTO);
    }

    // Lấy danh sách người mà user đang follow
    @Transactional(readOnly = true)
    public Page<FollowResponseDTO> getFollowing(UUID userId, Pageable pageable) {
        // Kiểm tra user có tồn tại không
        if (!userRepository.existsById(userId)) {
            throw new ResourceNotFoundException("Không tìm thấy người dùng với ID: " + userId);
        }

        Page<Follow> following = followRepository.findFollowingByUserId(userId, pageable);
        return following.map(this::convertToFollowResponseDTO);
    }

    // Lấy thống kê follow của một user
    @Transactional(readOnly = true)
    public UserFollowStatsDTO getUserFollowStats(UUID userId, UUID currentUserId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng với ID: " + userId));

        long followersCount = followRepository.countByFollowedId(userId);
        long followingCount = followRepository.countByFollowerId(userId);

        boolean isFollowedByCurrentUser = false;
        if (currentUserId != null && !currentUserId.equals(userId)) {
            isFollowedByCurrentUser = followRepository.existsByFollowerIdAndFollowedId(currentUserId, userId);
        }

        return UserFollowStatsDTO.builder()
                .userId(user.getUserId())
                .username(user.getUsername())
                .profilePicture(user.getProfilePicture())
                .followersCount(followersCount)
                .followingCount(followingCount)
                .isFollowedByCurrentUser(isFollowedByCurrentUser)
                .build();
    }

    // Kiểm tra user A có đang follow user B không
    @Transactional(readOnly = true)
    public boolean isFollowing(UUID followerId, UUID followedId) {
        return followRepository.existsByFollowerIdAndFollowedId(followerId, followedId);
    }

    // Lấy danh sách mutual follows (follow lẫn nhau)
    @Transactional(readOnly = true)
    public List<FollowResponseDTO> getMutualFollows(UUID userId) {
        List<Follow> mutualFollows = followRepository.findMutualFollows(userId);
        return mutualFollows.stream()
                .map(this::convertToFollowResponseDTO)
                .collect(Collectors.toList());
    }

    // Lấy danh sách user IDs mà current user đang follow (để filter content)
    @Transactional(readOnly = true)
    public List<UUID> getFollowedUserIds(UUID userId) {
        return followRepository.findFollowedUserIdsByFollowerId(userId);
    }

    // Helper method để convert Follow entity sang DTO
    private FollowResponseDTO convertToFollowResponseDTO(Follow follow) {
        return FollowResponseDTO.builder()
                .followerId(follow.getFollowerId())
                .followedId(follow.getFollowedId())
                .followerUsername(follow.getFollower() != null ? follow.getFollower().getUsername() : null)
                .followedUsername(follow.getFollowed() != null ? follow.getFollowed().getUsername() : null)
                .followerProfilePicture(follow.getFollower() != null ? follow.getFollower().getProfilePicture() : null)
                .followedProfilePicture(follow.getFollowed() != null ? follow.getFollowed().getProfilePicture() : null)
                .createdAt(follow.getCreatedAt())
                .build();
    }
}