package com.swp391project.SWP391_QuitSmoking_BE.service;

import com.swp391project.SWP391_QuitSmoking_BE.dto.normalMember.MemberResponse;
import com.swp391project.SWP391_QuitSmoking_BE.entity.Member;
import com.swp391project.SWP391_QuitSmoking_BE.entity.User;
import com.swp391project.SWP391_QuitSmoking_BE.exception.ResourceNotFoundException;
import com.swp391project.SWP391_QuitSmoking_BE.repository.MemberRepository;
import com.swp391project.SWP391_QuitSmoking_BE.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class MemberService {
    private final MemberRepository memberRepository;
    private final UserRepository userRepository;

    @Autowired
    public MemberService(
            MemberRepository memberRepository,
            UserRepository userRepository
    ) {
        this.memberRepository = memberRepository;
        this.userRepository = userRepository;
    }

    private MemberResponse convertEntityToResponse(Member member) {
        MemberResponse response = new MemberResponse();
        response.setMemberId(member.getMemberId());
        response.setStreak(member.getStreak());
        return response;
    }

    //được gọi sau khi một User vừa mới được đăng ký
    @Transactional
    public void createMemberForUser(User user) {
        Member member = new Member();
        member.setMemberId(user.getUserId());
        member.setUser(user); // Member trỏ đến User
        user.setMember(member); // User trỏ đến Member
        member.setStreak(0);
        member.setMemberSubscriptions(new ArrayList<>());
        member.setQuitPlans(new ArrayList<>());
        memberRepository.save(member);
    }

    public Member getMemberById(UUID memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found with ID: " + memberId));
    }

    @Transactional
    public List<Member> getAllMembersWithUserDetails() {
        return memberRepository.findAllWithUser();
    }

    public MemberResponse getMemberResponseById(UUID memberId) {
        Member member = getMemberById(memberId);
        return convertEntityToResponse(member);
    }

    //Cập nhật Streak của Member
    @Transactional
    public MemberResponse updateMemberStreak(UUID memberId, int newStreak) {
        if (newStreak < 0) {
            throw new IllegalArgumentException("Streak không thể là số âm");
        }
        Member member = getMemberById(memberId);
        member.setStreak(newStreak);
        Member updatedMember = memberRepository.save(member);
        return convertEntityToResponse(updatedMember);
    }

    // Xóa Member
    @Transactional
    public void deleteMemberById(UUID memberId) {
        if (!memberRepository.existsById(memberId)) {
            throw new ResourceNotFoundException("Member not found with ID: " + memberId);
        }
        memberRepository.deleteById(memberId);
    }
}
