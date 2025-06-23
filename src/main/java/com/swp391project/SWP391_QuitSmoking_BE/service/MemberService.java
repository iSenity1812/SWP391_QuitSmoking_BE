package com.swp391project.SWP391_QuitSmoking_BE.service;

import com.swp391project.SWP391_QuitSmoking_BE.dto.normalMember.MemberResponse;
import com.swp391project.SWP391_QuitSmoking_BE.entity.Member;
import com.swp391project.SWP391_QuitSmoking_BE.entity.User;
import com.swp391project.SWP391_QuitSmoking_BE.exception.ResourceNotFoundException;
import com.swp391project.SWP391_QuitSmoking_BE.repository.MemberRepository;
import com.swp391project.SWP391_QuitSmoking_BE.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional; // Import đúng @Transactional từ jakarta.transaction
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class MemberService {
    private final MemberRepository memberRepository;
    private final UserRepository userRepository;
    private static final Logger log = org.slf4j.LoggerFactory.getLogger(MemberService.class);

    @Autowired
    private EntityManager entityManager;

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
        // Map các trường khác từ Member entity sang MemberResponse DTO nếu có
        return response;
    }

    // Được gọi sau khi một User vừa mới được đăng ký
    @Transactional
    public void createMemberForUser(User user) {
        try {
            if (user == null) {
                log.error("User is null");
                throw new IllegalArgumentException("User cannot be null");
            }

            // Kiểm tra xem Member đã tồn tại cho User này chưa
            Optional<Member> existingMemberOptional = memberRepository.findById(user.getUserId());

            if (existingMemberOptional.isPresent()) {
                log.warn("Member already exists for user with ID: {}. Skipping creation.", user.getUserId());
                Member existingMember = existingMemberOptional.get();
                if (user.getMember() == null || !user.getMember().getMemberId().equals(existingMember.getMemberId())) {
                    user.setMember(existingMember);
                    userRepository.save(user);
                    log.debug("Updated user with existing member reference.");
                }
                return;
            }

            // Member CHƯA TỒN TẠI
            log.debug("Initializing new member entity...");
            Member member = new Member();
            member.setUser(user);
            log.debug("Set user reference to member");

            member.setStreak(0);
            member.setMemberSubscriptions(new ArrayList<>());
            member.setQuitPlans(new ArrayList<>());
            log.debug("Set default member values");

            Member savedMember = memberRepository.save(member);
            log.info("Saved new member to repository with ID: {}", savedMember.getMemberId());
            user.setMember(savedMember);
            userRepository.save(user);
            log.debug("Updated user with saved member");

            log.info("Member created successfully for user with ID: {}", user.getUserId());

        } catch (Exception e) {
            log.error("Exception occurred while creating member for user with ID: {}", user != null ? user.getUserId() : "null", e);
            throw e;
        }
    }

    // Phương thức này trả về Member entity (đúng như API cần cho getAllMembers)
    public Member getMemberById(UUID memberId) {
        return memberRepository.findById(memberId)
                .orElseThrow(() -> new ResourceNotFoundException("Member not found with ID: " + memberId));
    }

    // Phương thức này lấy tất cả Member và User details (API sử dụng)
    public List<Member> getAllMembersWithUserDetails() {
        return memberRepository.findAllWithUser(); // Giả định findAllWithUser() trả về List<Member>
    }

    // Phương thức mới: Trả về MemberResponse DTO cho API
    public MemberResponse getMemberResponseById(UUID memberId) {
        Member member = getMemberById(memberId); // Sử dụng phương thức getMemberById đã có
        return convertEntityToResponse(member);
    }

    // Cập nhật Streak của Member
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
        Optional<Member> memberOptional = memberRepository.findById(memberId);
        if (!memberOptional.isPresent()) {
            throw new ResourceNotFoundException("Member not found with ID: " + memberId);
        }
        Member member = memberOptional.get();
        if (member.getUser() != null) {
            User user = member.getUser();
            user.setMember(null); // Gỡ bỏ liên kết từ User đến Member
            userRepository.save(user); // Lưu User để cập nhật
        }
        memberRepository.deleteById(memberId);
    }
}