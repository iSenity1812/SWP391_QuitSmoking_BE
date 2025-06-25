package com.swp391project.SWP391_QuitSmoking_BE.repository;

import com.swp391project.SWP391_QuitSmoking_BE.entity.Challenge;
import com.swp391project.SWP391_QuitSmoking_BE.entity.Member;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface ChallengeRepository extends JpaRepository<Challenge, Integer> {
    // Có thể thêm các phương thức tìm kiếm tùy chỉnh nếu cần
    // Sửa lại để sử dụng đúng tên property của User
    List<Challenge> findByMember_User_UserId(UUID userId);

    // Hoặc có thể dùng
    List<Challenge> findByMember(Member member);
}
