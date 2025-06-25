package com.swp391project.SWP391_QuitSmoking_BE.repository;

import com.swp391project.SWP391_QuitSmoking_BE.entity.Member;
import com.swp391project.SWP391_QuitSmoking_BE.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface MemberRepository extends JpaRepository<Member, UUID> {
//    Optional<Member> findByMemberId(UUID memberId);

    // Tìm Member theo User
    Optional<Member> findByUser(User user);

    // Tìm Member theo User ID - sửa lại để sử dụng đúng tên property
    Optional<Member> findByUser_UserId(UUID userId);
}
