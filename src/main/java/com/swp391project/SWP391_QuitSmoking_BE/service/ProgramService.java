package com.swp391project.SWP391_QuitSmoking_BE.service;

import com.swp391project.SWP391_QuitSmoking_BE.dto.program.ProgramRequestDTO;
import com.swp391project.SWP391_QuitSmoking_BE.dto.program.ProgramResponseDTO;
import com.swp391project.SWP391_QuitSmoking_BE.dto.program.ProgramUpdateRequestDTO;
import com.swp391project.SWP391_QuitSmoking_BE.entity.Program;
import com.swp391project.SWP391_QuitSmoking_BE.entity.User;
import com.swp391project.SWP391_QuitSmoking_BE.enums.ProgramType;
import com.swp391project.SWP391_QuitSmoking_BE.exception.AppException;
import com.swp391project.SWP391_QuitSmoking_BE.exception.ErrorCode;
import com.swp391project.SWP391_QuitSmoking_BE.repository.ProgramRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProgramService {

    private final ProgramRepository programRepository;
    private final ModelMapper modelMapper;
    private final FileUploadService fileUploadService;

    // Helper method để chuyển đổi Entity sang DTO
    private ProgramResponseDTO convertToProgramResponseDTO(Program program) {
        if (program == null) return null;
        return modelMapper.map(program, ProgramResponseDTO.class);
    }

    // Helper method để kiểm tra MultipartFile có hợp lệ không
    private boolean isValidImageFile(org.springframework.web.multipart.MultipartFile file) {
        return file != null && !file.isEmpty() && file.getSize() > 0;
    }

    // --- Phương thức công khai cho PREMIUM_MEMBER (xem programs) ---
    @Transactional(readOnly = true)
    public Page<ProgramResponseDTO> getAllPrograms(String keyword, Pageable pageable) {
        Page<Program> programsPage;
        if (keyword != null && !keyword.trim().isEmpty()) {
            programsPage = programRepository.findByProgramTitleContainingIgnoreCaseOrProgramNameContainingIgnoreCase(
                    keyword, keyword, pageable);
        } else {
            programsPage = programRepository.findAll(pageable);
        }
        return programsPage.map(this::convertToProgramResponseDTO);
    }

    // Lấy thông tin một program cụ thể bằng ID
    @Transactional(readOnly = true)
    public ProgramResponseDTO getProgramById(Integer programId) {
        Program program = programRepository.findById(programId)
                .orElseThrow(() -> new AppException(ErrorCode.PROGRAM_NOT_FOUND));
        return convertToProgramResponseDTO(program);
    }

    // Tìm kiếm programs theo type - hỗ trợ cả enum và string để backward compatibility
    @Transactional(readOnly = true)
    public Page<ProgramResponseDTO> getProgramsByType(String programTypeStr, Pageable pageable) {
        // Thử chuyển đổi string thành enum trước
        ProgramType programType = ProgramType.fromString(programTypeStr);

        Page<Program> programsPage;
        if (programType != null) {
            // Nếu chuyển đổi thành công, sử dụng enum search
            programsPage = programRepository.findByProgramType(programType, pageable);
        } else {
            // Nếu không thể chuyển đổi, sử dụng string search để backward compatibility
            programsPage = programRepository.findByProgramTypeContainingIgnoreCase(programTypeStr, pageable);
        }

        return programsPage.map(this::convertToProgramResponseDTO);
    }

    // Tìm kiếm programs theo nhiều types
    @Transactional(readOnly = true)
    public Page<ProgramResponseDTO> getProgramsByTypes(List<String> programTypeStrs, Pageable pageable) {
        List<ProgramType> programTypes = programTypeStrs.stream()
                .map(ProgramType::fromString)
                .filter(type -> type != null)
                .collect(Collectors.toList());

        if (programTypes.isEmpty()) {
            return Page.empty(pageable);
        }

        Page<Program> programsPage = programRepository.findByProgramTypeIn(programTypes, pageable);
        return programsPage.map(this::convertToProgramResponseDTO);
    }

    // Lấy tất cả program types
    @Transactional(readOnly = true)
    public List<ProgramType> getAllProgramTypes() {
        return Arrays.asList(ProgramType.values());
    }

    // --- Phương thức cho CONTENT_ADMIN (tạo, sửa, xóa programs) ---

    @Transactional
    public ProgramResponseDTO createProgram(ProgramRequestDTO programRequest, User currentUser) {
        log.info("Creating new program: {} by user: {}", programRequest.getProgramTitle(), currentUser.getUsername());

        Program newProgram = new Program();
        newProgram.setProgramTitle(programRequest.getProgramTitle());
        newProgram.setProgramName(programRequest.getProgramName());

        // Chuyển đổi string thành enum
        ProgramType programType = ProgramType.fromString(programRequest.getProgramType());
        newProgram.setProgramType(programType);

        newProgram.setContentUrl(programRequest.getContentUrl());
        newProgram.setDescription(programRequest.getDescription());
        newProgram.setCreatedBy(currentUser);
        newProgram.setCreatedAt(LocalDateTime.now());

        // Upload image nếu có
        if (programRequest.getProgramImage() != null && !programRequest.getProgramImage().isEmpty()) {
            String imageUrl = fileUploadService.uploadImage(programRequest.getProgramImage());
            newProgram.setProgramImage(imageUrl);
        }

        newProgram = programRepository.save(newProgram);
        return convertToProgramResponseDTO(newProgram);
    }

    @Transactional
    public ProgramResponseDTO updateProgram(Integer id, ProgramUpdateRequestDTO programRequest, User currentUser) {
        Program existingProgram = programRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PROGRAM_NOT_FOUND));

        // Kiểm tra quyền (chỉ creator hoặc CONTENT_ADMIN mới được sửa)
        boolean isCreator = existingProgram.getCreatedBy().getUserId().equals(currentUser.getUserId());
        if (!isCreator) {
            throw new AccessDeniedException("Bạn không có quyền sửa program này.");
        }

        // Lưu URL image cũ để xóa nếu cần
        String oldImageUrl = existingProgram.getProgramImage();

        // Cập nhật thông tin program
        existingProgram.setProgramTitle(programRequest.getProgramTitle());
        existingProgram.setProgramName(programRequest.getProgramName());

        // Chuyển đổi string thành enum
        ProgramType programType = ProgramType.fromString(programRequest.getProgramType());
        existingProgram.setProgramType(programType);

        existingProgram.setContentUrl(programRequest.getContentUrl());
        existingProgram.setDescription(programRequest.getDescription());

        // Xử lý image với logic rõ ràng
        boolean shouldRemoveImage = programRequest.getRemoveImage() != null && programRequest.getRemoveImage();
        boolean hasNewImage = isValidImageFile(programRequest.getProgramImage());

        if (shouldRemoveImage) {
            // Trường hợp 1: Người dùng muốn xóa image hiện tại
            if (oldImageUrl != null) {
                fileUploadService.deleteImage(oldImageUrl);
            }
            existingProgram.setProgramImage(null);
        } else if (hasNewImage) {
            // Trường hợp 2: Người dùng muốn upload image mới
            String newImageUrl = fileUploadService.uploadImage(programRequest.getProgramImage());
            existingProgram.setProgramImage(newImageUrl);

            // Xóa image cũ nếu có
            if (oldImageUrl != null) {
                fileUploadService.deleteImage(oldImageUrl);
            }
        }
        // Trường hợp 3: Không có removeImage flag và không có image mới
        // → Giữ nguyên image cũ (không làm gì cả)

        existingProgram = programRepository.save(existingProgram);
        return convertToProgramResponseDTO(existingProgram);
    }

    @Transactional
    public void deleteProgram(Integer id, User currentUser) {
        log.info("Deleting program ID: {} by user: {}", id, currentUser.getUsername());

        Program existingProgram = programRepository.findById(id)
                .orElseThrow(() -> new AppException(ErrorCode.PROGRAM_NOT_FOUND));

        // Kiểm tra quyền (chỉ creator hoặc CONTENT_ADMIN mới được xóa)
        boolean isCreator = existingProgram.getCreatedBy().getUserId().equals(currentUser.getUserId());
        if (!isCreator) {
            throw new AccessDeniedException("Bạn không có quyền xóa program này.");
        }

        // Xóa image nếu có
        if (existingProgram.getProgramImage() != null) {
            fileUploadService.deleteImage(existingProgram.getProgramImage());
        }

        programRepository.delete(existingProgram);
        log.info("Program deleted successfully: {}", id);
    }

    // --- Phương thức cho Admin (lấy programs theo creator) ---
    @Transactional(readOnly = true)
    public Page<ProgramResponseDTO> getProgramsByCreator(UUID creatorId, Pageable pageable) {
        Page<Program> programsPage = programRepository.findByCreatedBy_UserId(creatorId, pageable);
        return programsPage.map(this::convertToProgramResponseDTO);
    }

    @Transactional(readOnly = true)
    public long countProgramsByCreator(UUID creatorId) {
        return programRepository.countByCreatorId(creatorId);
    }
}
