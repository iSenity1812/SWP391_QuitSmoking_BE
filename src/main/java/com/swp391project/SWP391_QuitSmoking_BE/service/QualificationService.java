package com.swp391project.SWP391_QuitSmoking_BE.service;

import com.swp391project.SWP391_QuitSmoking_BE.dto.coach.QualificationRequestDTO;
import com.swp391project.SWP391_QuitSmoking_BE.dto.coach.QualificationResponseDTO;
import com.swp391project.SWP391_QuitSmoking_BE.entity.Coach;
import com.swp391project.SWP391_QuitSmoking_BE.entity.Qualification;
import com.swp391project.SWP391_QuitSmoking_BE.entity.Qualification.QualificationId;
import com.swp391project.SWP391_QuitSmoking_BE.entity.User;
import com.swp391project.SWP391_QuitSmoking_BE.enums.Role;
import com.swp391project.SWP391_QuitSmoking_BE.repository.CoachRepository;
import com.swp391project.SWP391_QuitSmoking_BE.repository.QualificationRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class QualificationService {
    private final QualificationRepository qualificationRepository;
    private final CoachRepository coachRepository;

    public List<QualificationResponseDTO> getQualificationsByCoach(UUID coachId) {
        return qualificationRepository.findByCoach_CoachId(coachId)
                .stream()
                .map(this::toResponseDTO)
                .collect(Collectors.toList());
    }

    private void checkPermission(UUID coachId) {
        // Lấy user hiện tại từ context
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof User user) {
            boolean isAdmin = user.getRole() == Role.SUPER_ADMIN;
            boolean isCoachOwner = user.getCoach() != null && user.getCoach().getCoachId().equals(coachId);
            if (!isAdmin && !isCoachOwner) {
                throw new SecurityException("Bạn không có quyền thao tác với qualification này!");
            }
        } else {
            throw new SecurityException("Không xác định được người dùng!");
        }
    }

    @Transactional
    public QualificationResponseDTO createQualification(UUID coachId, QualificationRequestDTO dto) {
        checkPermission(coachId);
        Coach coach = coachRepository.findById(coachId)
                .orElseThrow(() -> new EntityNotFoundException("Coach not found"));
        Qualification qualification = new Qualification();
        qualification.setCoach(coach);
        qualification.setQualificationName(dto.getQualificationName());
        qualification.setIssuingOrganization(dto.getIssuingOrganization());
        qualification.setQualificationURL(dto.getQualificationURL());
        qualification.setIsApproved(false);
        qualification.setRequestUpdateDate(dto.getRequestUpdateDate() != null ? dto.getRequestUpdateDate() : java.time.LocalDateTime.now());
        qualification.setApproveBy(null);
        qualificationRepository.save(qualification);
        return toResponseDTO(qualification);
    }

    @Transactional
    public QualificationResponseDTO updateQualification(UUID coachId, String qualificationName, QualificationRequestDTO dto) {
        checkPermission(coachId);
        QualificationId id = new QualificationId();
        Coach coach = coachRepository.findById(coachId)
                .orElseThrow(() -> new EntityNotFoundException("Coach not found"));
        id.setCoach(coach);
        id.setQualificationName(qualificationName);
        Qualification qualification = qualificationRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Qualification not found"));
        qualification.setIssuingOrganization(dto.getIssuingOrganization());
        qualification.setQualificationURL(dto.getQualificationURL());
        qualification.setRequestUpdateDate(dto.getRequestUpdateDate() != null ? dto.getRequestUpdateDate() : java.time.LocalDateTime.now());
        qualificationRepository.save(qualification);
        return toResponseDTO(qualification);
    }

    @Transactional
    public void deleteQualification(UUID coachId, String qualificationName) {
        checkPermission(coachId);
        QualificationId id = new QualificationId();
        Coach coach = coachRepository.findById(coachId)
                .orElseThrow(() -> new EntityNotFoundException("Coach not found"));
        id.setCoach(coach);
        id.setQualificationName(qualificationName);
        qualificationRepository.deleteById(id);
    }

    @Transactional
    public QualificationResponseDTO approveQualification(UUID coachId, String qualificationName, String adminUserId) {
        // Chỉ SUPER_ADMIN mới được duyệt
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof User user && user.getRole() == Role.SUPER_ADMIN) {
            QualificationId id = new QualificationId();
            Coach coach = coachRepository.findById(coachId)
                    .orElseThrow(() -> new EntityNotFoundException("Coach not found"));
            id.setCoach(coach);
            id.setQualificationName(qualificationName);
            Qualification qualification = qualificationRepository.findById(id)
                    .orElseThrow(() -> new EntityNotFoundException("Qualification not found"));
            qualification.setIsApproved(true);
            qualification.setApproveBy(adminUserId);
            qualificationRepository.save(qualification);
            return toResponseDTO(qualification);
        } else {
            throw new SecurityException("Chỉ SUPER_ADMIN mới được duyệt qualification!");
        }
    }

    private QualificationResponseDTO toResponseDTO(Qualification q) {
        QualificationResponseDTO dto = new QualificationResponseDTO();
        dto.setQualificationName(q.getQualificationName());
        dto.setIssuingOrganization(q.getIssuingOrganization());
        dto.setQualificationURL(q.getQualificationURL());
        dto.setIsApproved(q.getIsApproved());
        dto.setRequestUpdateDate(q.getRequestUpdateDate());
        dto.setApproveBy(q.getApproveBy());
        return dto;
    }
} 