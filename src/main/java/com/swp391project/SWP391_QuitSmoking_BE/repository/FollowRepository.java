package com.swp391project.SWP391_QuitSmoking_BE.repository;

import com.swp391project.SWP391_QuitSmoking_BE.entity.Follow;
import com.swp391project.SWP391_QuitSmoking_BE.entity.FollowId;
import com.swp391project.SWP391_QuitSmoking_BE.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface FollowRepository extends JpaRepository<Follow, FollowId> {
    List<Follow> findByFollower(User follower);

    boolean existsByFollowerIdAndFollowedId(UUID followerId, UUID followedId);

    // Tìm follow relationship
    Optional<Follow> findByFollowerIdAndFollowedId(UUID followerId, UUID followedId);

    // Lấy danh sách người đang follow một user (followers)
    @Query("SELECT f FROM Follow f JOIN FETCH f.follower WHERE f.followedId = :userId")
    Page<Follow> findFollowersByUserId(@Param("userId") UUID userId, Pageable pageable);

    // Lấy danh sách người mà một user đang follow (following)
    @Query("SELECT f FROM Follow f JOIN FETCH f.followed WHERE f.followerId = :userId")
    Page<Follow> findFollowingByUserId(@Param("userId") UUID userId, Pageable pageable);

    // Đếm số followers của một user
    long countByFollowedId(UUID followedId);

    // Đếm số người mà một user đang follow
    long countByFollowerId(UUID followerId);

    // Lấy danh sách user IDs mà current user đang follow
    @Query("SELECT f.followedId FROM Follow f WHERE f.followerId = :userId")
    List<UUID> findFollowedUserIdsByFollowerId(@Param("userId") UUID userId);

    // Xóa follow relationship
    void deleteByFollowerIdAndFollowedId(UUID followerId, UUID followedId);

    // Lấy mutual follows (người follow lẫn nhau)
    @Query("SELECT f1 FROM Follow f1 WHERE f1.followerId = :userId AND EXISTS " +
            "(SELECT f2 FROM Follow f2 WHERE f2.followerId = f1.followedId AND f2.followedId = :userId)")
    List<Follow> findMutualFollows(@Param("userId") UUID userId);
} 