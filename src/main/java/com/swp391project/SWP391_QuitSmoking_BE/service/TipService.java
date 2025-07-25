package com.swp391project.SWP391_QuitSmoking_BE.service;

import com.swp391project.SWP391_QuitSmoking_BE.dto.tip.*;
import com.swp391project.SWP391_QuitSmoking_BE.entity.Tip;
import com.swp391project.SWP391_QuitSmoking_BE.entity.User;
import com.swp391project.SWP391_QuitSmoking_BE.enums.Role;
import com.swp391project.SWP391_QuitSmoking_BE.exception.ResourceNotFoundException;
import com.swp391project.SWP391_QuitSmoking_BE.repository.TipRepository;
import com.swp391project.SWP391_QuitSmoking_BE.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class TipService {

    private final TipRepository tipRepository;
    private final UserRepository userRepository;

    @Autowired
    public TipService(TipRepository tipRepository, UserRepository userRepository) {
        this.tipRepository = tipRepository;
        this.userRepository = userRepository;
    }

    // Phương thức chuyển đổi Entity sang DTO
    private TipResponseDTO convertToResponseDto(Tip tip) {
        return new TipResponseDTO(tip.getTipId(), tip.getContent());
    }

    @Transactional
    public TipResponseDTO createTip(TipCreationRequestDTO request, UUID createdByUserId) {
        User user = userRepository.findById(createdByUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with ID: " + createdByUserId));

        if (user.getRole() != Role.SUPER_ADMIN && user.getRole() != Role.CONTENT_ADMIN) {
            throw new IllegalArgumentException("Chỉ quản trị viên mới có quyền tạo Tip.");
        }

        Tip tip = new Tip();
        tip.setContent(request.getContent());
        tip.setCreatedAt(LocalDateTime.now());
        tip.setUpdatedAt(LocalDateTime.now());
        tip.setCreatedByUser(user);

        Tip savedTip = tipRepository.save(tip);
        return convertToResponseDto(savedTip);
    }

    // --- PHƯƠNG THỨC LẤY TẤT CẢ TIP MỚI ---
    @Transactional
    public List<TipResponseDTO> getAllTips() {
        return tipRepository.findAll().stream()
                .map(this::convertToResponseDto)
                .collect(Collectors.toList());
    }

    @Transactional
    public TipResponseDTO getTipById(UUID tipId) {
        Tip tip = tipRepository.findById(tipId)
                .orElseThrow(() -> new ResourceNotFoundException("Tip not found with ID: " + tipId));
        return convertToResponseDto(tip);
    }

    @Transactional
    public TipResponseDTO updateTip(UUID tipId, TipCreationRequestDTO request, UUID updatedByUserId) {
        Tip existingTip = tipRepository.findById(tipId)
                .orElseThrow(() -> new ResourceNotFoundException("Tip not found with ID: " + tipId));

        User updater = userRepository.findById(updatedByUserId)
                .orElseThrow(() -> new ResourceNotFoundException("User updating Tip not found with ID: " + updatedByUserId));

        if (updater.getRole() != Role.SUPER_ADMIN && updater.getRole() != Role.CONTENT_ADMIN) {
            throw new IllegalArgumentException("Chỉ quản trị viên (SUPER_ADMIN hoặc CONTENT_ADMIN) mới có quyền cập nhật Tip.");
        }

        existingTip.setContent(request.getContent());
        existingTip.setUpdatedAt(LocalDateTime.now());

        Tip updatedTip = tipRepository.save(existingTip);
        return convertToResponseDto(updatedTip);
    }

    @Transactional
    public void deleteTip(UUID tipId) {
        Tip tipToDelete = tipRepository.findById(tipId)
                .orElseThrow(() -> new ResourceNotFoundException("Tip not found with ID: " + tipId));

        // Sau khi tất cả các liên kết đã được xóa, giờ an toàn để xóa Tip
        tipRepository.delete(tipToDelete);
    }
}