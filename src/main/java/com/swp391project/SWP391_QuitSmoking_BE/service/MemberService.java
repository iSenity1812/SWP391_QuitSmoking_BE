// src/main/java/com/swp391project/SWP391_QuitSmoking_BE/service/MemberService.java
package com.swp391project.SWP391_QuitSmoking_BE.service;

import com.swp391project.SWP391_QuitSmoking_BE.entity.Member;
import com.swp391project.SWP391_QuitSmoking_BE.repository.MemberRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class MemberService {
    @Autowired
    private MemberRepository memberRepository;

    public List<Member> getAllMembers() {
        return memberRepository.findAll();
    }

    public Optional<Member> getMemberById(UUID id) {
        return memberRepository.findById(id);
    }

    public Member createMember(Member member) {
        return memberRepository.save(member);
    }

    public Member updateMember(UUID id, Member memberDetails) {
        return memberRepository.findById(id).map(member -> {
            // Các trường này vẫn có thể cập nhật qua DTO hoặc các phương thức service khác
            // Hiện tại, chỉ cập nhật User và Streak.
            // Các thông tin Subscription (gói đăng ký) sẽ được quản lý qua MemberSubscriptionService riêng biệt.
            member.setUser(memberDetails.getUser());
            // member.setSubscription(memberDetails.getSubscription()); // ĐÃ BỎ - Lỗi ở đây trước đó
            // member.setStartDate(memberDetails.getStartDate());     // ĐÃ BỎ - Lỗi ở đây trước đó
            // member.setEndDate(memberDetails.getEndDate());         // ĐÃ BỎ - Lỗi ở đây trước đó
            // member.setSubscriptionStatus(memberDetails.isSubscriptionStatus()); // ĐÃ BỎ - Lỗi ở đây trước đó
            member.setStreak(memberDetails.getStreak());
            return memberRepository.save(member);
        }).orElseThrow(() -> new RuntimeException("Member not found"));
    }

    public void deleteMember(UUID id) {
        memberRepository.deleteById(id);
    }
}