package com.swp391project.SWP391_QuitSmoking_BE.repository;

import com.swp391project.SWP391_QuitSmoking_BE.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<User> findByUserId(UUID id);
    boolean existsByUsername(String username);
    boolean existsByEmail(String email);
    List<User> findAllByIsActiveTrue(); // Lấy tất cả người dùng đang hoạt động
    List<User> findAllByIsActiveFalse(); // Lấy tất cả người dùng không hoạt động
    List<User> findByEmailContainingIgnoreCaseOrUsernameContainingIgnoreCase(String email, String username);
}
