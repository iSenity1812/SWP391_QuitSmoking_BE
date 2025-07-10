package com.swp391project.SWP391_QuitSmoking_BE.service;

import com.swp391project.SWP391_QuitSmoking_BE.dto.challenge.*;
import com.swp391project.SWP391_QuitSmoking_BE.entity.Challenge;
import com.swp391project.SWP391_QuitSmoking_BE.entity.Member;
import com.swp391project.SWP391_QuitSmoking_BE.entity.User;
import com.swp391project.SWP391_QuitSmoking_BE.enums.Role;
import com.swp391project.SWP391_QuitSmoking_BE.exception.*;
import com.swp391project.SWP391_QuitSmoking_BE.exception.ResourceNotFoundException;
import com.swp391project.SWP391_QuitSmoking_BE.repository.ChallengeRepository;
import com.swp391project.SWP391_QuitSmoking_BE.repository.MemberRepository;
import jakarta.validation.ValidationException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChallengeService {

    private final ChallengeRepository challengeRepository;
    private final MemberRepository memberRepository;
    private final ModelMapper modelMapper;

    // --- Phương thức chuyển đổi Entity sang DTO ---
    private ChallengeResponseDTO convertToDtoChallenge(Challenge challenge) {
        ChallengeResponseDTO dto = new ChallengeResponseDTO();
        dto.setChallengeID(challenge.getChallengeID());
        dto.setMemberID(challenge.getMember() != null ? challenge.getMember().getMemberId() : null);
        dto.setChallengeName(challenge.getChallengeName());
        dto.setDescription(challenge.getDescription());
        dto.setStartDate(challenge.getStartDate());
        dto.setEndDate(challenge.getEndDate());
        dto.setTargetValue(challenge.getTargetValue());
        dto.setUnit(challenge.getUnit());
        dto.setStatus(challenge.getStatus());
        return dto;
    }

    @Transactional
    public ChallengeResponseDTO createChallenge(UUID currentUserId, ChallengeRequestDTO request) {
        log.info("Attempting to create challenge for user ID: {}", currentUserId);

        // Tìm Member dựa trên UserID - sử dụng method name mới
        Member member = memberRepository.findByUser_UserId(currentUserId)
                .orElseThrow(() -> {
                    log.warn("Member not found with User ID: {}", currentUserId);
                    return new ResourceNotFoundException("Không tìm thấy thành viên với User ID: " + currentUserId);
                });

        User user = member.getUser();
        if (user == null || user.getRole() != Role.PREMIUM_MEMBER) {
            log.warn("User ID {} (Role: {}) is not a PREMIUM_MEMBER and cannot create a challenge (Service check).", currentUserId, user != null ? user.getRole() : "NULL");
            throw new ValidationException("Bạn không có quyền tạo thử thách. Chức năng này chỉ dành cho Premium Member.");
        }
        log.info("User ID {} (Role: {}) is authorized to create a challenge (Service check).", currentUserId, user.getRole());

        if (request.getStartDate() == null) {
            request.setStartDate(LocalDate.now());
        }
        if (request.getEndDate() != null && request.getEndDate().isBefore(request.getStartDate())) {
            log.error("Validation failed: End date {} is before start date {}", request.getEndDate(), request.getStartDate());
            throw new ValidationException("Ngày kết thúc không được trước ngày bắt đầu.");
        }

        Challenge newChallenge = new Challenge();
        newChallenge.setMember(member);
        newChallenge.setChallengeName(request.getChallengeName());
        newChallenge.setDescription(request.getDescription());
        newChallenge.setStartDate(request.getStartDate());
        newChallenge.setEndDate(request.getEndDate());
        newChallenge.setTargetValue(request.getTargetValue());
        newChallenge.setUnit(request.getUnit());
        newChallenge.setStatus("Active");

        Challenge savedChallenge = challengeRepository.save(newChallenge);
        log.info("Challenge created successfully with ID: {} for user ID: {}", savedChallenge.getChallengeID(), currentUserId);

        return convertToDtoChallenge(savedChallenge);
    }

    @Transactional(readOnly = true)
    public List<ChallengeResponseDTO> getChallengesByMemberId(UUID memberId) {
        log.info("Fetching challenges for member ID: {}", memberId);
        // Sử dụng method name mới
        List<Challenge> challenges = challengeRepository.findByMember_User_UserId(memberId);
        return challenges.stream()
                .map(this::convertToDtoChallenge)
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public ChallengeResponseDTO getChallengeById(Integer challengeId, UUID currentUserId) {
        log.info("Fetching challenge with ID: {} for user ID: {}", challengeId, currentUserId);
        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> {
                    log.warn("Challenge not found with ID: {}", challengeId);
                    return new ResourceNotFoundException("Không tìm thấy thử thách với ID: " + challengeId);
                });

        Member member = memberRepository.findByUser_UserId(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thành viên với User ID: " + currentUserId));
        User user = member.getUser();

        // Kiểm tra quyền truy cập
        if (!challenge.getMember().getMemberId().equals(currentUserId) &&
                !(user != null && (user.getRole() == Role.SUPER_ADMIN || user.getRole() == Role.CONTENT_ADMIN || user.getRole() == Role.COACH))) {
            log.warn("User ID {} is not authorized to view challenge ID {}", currentUserId, challengeId);
            throw new ValidationException("Bạn không có quyền xem thử thách này.");
        }
        return convertToDtoChallenge(challenge);
    }

    @Transactional
    public ChallengeResponseDTO updateChallenge(UUID currentUserId, Integer challengeId, ChallengeRequestDTO request) {
        log.info("Attempting to update challenge ID: {} by user ID: {}", challengeId, currentUserId);

        Challenge existingChallenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> {
                    log.warn("Challenge not found with ID: {}", challengeId);
                    return new ResourceNotFoundException("Không tìm thấy thử thách với ID: " + challengeId);
                });

        if (!existingChallenge.getMember().getMemberId().equals(currentUserId)) {
            log.warn("User ID {} is not authorized to update challenge ID {}", currentUserId, challengeId);
            throw new ValidationException("Bạn không có quyền cập nhật thử thách này.");
        }

        existingChallenge.setChallengeName(request.getChallengeName());
        existingChallenge.setDescription(request.getDescription());
        existingChallenge.setStartDate(request.getStartDate() != null ? request.getStartDate() : existingChallenge.getStartDate());
        existingChallenge.setEndDate(request.getEndDate());
        existingChallenge.setTargetValue(request.getTargetValue());
        existingChallenge.setUnit(request.getUnit());

        Challenge updatedChallenge = challengeRepository.save(existingChallenge);
        log.info("Challenge ID {} updated successfully by user ID: {}", challengeId, currentUserId);
        return convertToDtoChallenge(updatedChallenge);
    }

    @Transactional
    public void deleteChallenge(UUID currentUserId, Integer challengeId) {
        log.info("Attempting to delete challenge ID: {} by user ID: {}", challengeId, currentUserId);

        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> {
                    log.warn("Challenge not found with ID: {}", challengeId);
                    return new ResourceNotFoundException("Không tìm thấy thử thách với ID: " + challengeId);
                });

        Member member = memberRepository.findByUser_UserId(currentUserId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy thành viên với User ID: " + currentUserId));
        User user = member.getUser();

        if (!challenge.getMember().getMemberId().equals(currentUserId) &&
                (user == null || user.getRole() == null || user.getRole() != Role.SUPER_ADMIN)) {
            log.warn("User ID {} (Role: {}) is not authorized to delete challenge ID {}", currentUserId, user != null ? user.getRole() : "NULL", challengeId);
            throw new ValidationException("Bạn không có quyền xóa thử thách này.");
        }

        challengeRepository.delete(challenge);
        log.info("Challenge ID {} deleted successfully by user ID: {}", challengeId, currentUserId);
    }

    @Transactional(readOnly = true)
    public List<ChallengeResponseDTO> getAllChallengesForAdmin() {
        log.info("Fetching all challenges for admin access.");
        List<Challenge> challenges = challengeRepository.findAll();
        return challenges.stream()
                .map(this::convertToDtoChallenge)
                .collect(Collectors.toList());
    }

    @Transactional
    public ChallengeResponseDTO updateChallengeStatus(Integer challengeId, String newStatus, UUID adminUserId) {
        log.info("Admin user {} attempting to update status of challenge ID {} to {}", adminUserId, challengeId, newStatus);

        Challenge challenge = challengeRepository.findById(challengeId)
                .orElseThrow(() -> {
                    log.warn("Challenge not found with ID: {} for status update by admin.", challengeId);
                    return new ResourceNotFoundException("Không tìm thấy thử thách với ID: " + challengeId);
                });

        challenge.setStatus(newStatus);
        Challenge updatedChallenge = challengeRepository.save(challenge);
        log.info("Challenge ID {} status updated to {} by admin user {}", challengeId, newStatus, adminUserId);
        return convertToDtoChallenge(updatedChallenge);
    }
}
