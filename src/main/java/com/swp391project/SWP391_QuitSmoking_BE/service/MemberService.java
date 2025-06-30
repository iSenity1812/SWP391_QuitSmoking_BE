package com.swp391project.SWP391_QuitSmoking_BE.service;

import com.swp391project.SWP391_QuitSmoking_BE.dto.normalMember.MemberResponse;
import com.swp391project.SWP391_QuitSmoking_BE.dto.normalMember.MemberSearchDTO;
import com.swp391project.SWP391_QuitSmoking_BE.entity.Member;
import com.swp391project.SWP391_QuitSmoking_BE.entity.User;
import com.swp391project.SWP391_QuitSmoking_BE.exception.ResourceNotFoundException;
import com.swp391project.SWP391_QuitSmoking_BE.repository.MemberRepository;
import com.swp391project.SWP391_QuitSmoking_BE.repository.UserRepository;
import jakarta.persistence.EntityManager;
import jakarta.transaction.Transactional;
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
        return response;
    }

    //được gọi sau khi một User vừa mới được đăng ký
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
                    userRepository.save(user); // Lưu User để cập nhật mối quan hệ trong DB
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
//            member.setMemberSubscriptions(new ArrayList<>());
            member.setQuitPlans(new ArrayList<>());
            log.debug("Set default member values");

            Member savedMember = memberRepository.save(member);
            log.info("Saved new member to repository with ID: {}", savedMember.getMemberId());
            user.setMember(savedMember);
            userRepository.save(user); // Lưu User để cập nhật mối quan hệ trong DB
            log.debug("Updated user with saved member");

            log.info("Member created successfully for user with ID: {}", user.getUserId());

        } catch (Exception e) {
            log.error("Exception occurred while creating member for user with ID: {}", user != null ? user.getUserId() : "null", e);
            throw e;
        }

        log.info("Member created for user with ID: {}", user.getUserId());
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

    // Search Member
    @Transactional
    public List<MemberSearchDTO> searchMembers(String query) {
        log.debug("Searching members with query: {}", query);
        if (query == null || query.trim().isEmpty()) {
            log.warn("Search query is empty or null, returning empty list");
            return List.of(); // Trả về danh sách rỗng nếu query trống

        }

        List<Member> members = memberRepository.searchByUsernameOrEmail(query);

        // Chuyển đổi danh sách Member sang danh sách MemberSearchDTO
        return members.stream().map(member -> {
            MemberSearchDTO dto = new MemberSearchDTO();
            dto.setMemberId(member.getMemberId());

            if (member.getUser() != null) {
                dto.setUsername(member.getUser().getUsername());
                dto.setEmail(member.getUser().getEmail());
            }
            return dto;
        })
                .collect(Collectors.toList());
    }
}
