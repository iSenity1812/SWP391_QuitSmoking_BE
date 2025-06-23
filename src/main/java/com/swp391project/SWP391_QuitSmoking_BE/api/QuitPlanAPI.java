package com.swp391project.SWP391_QuitSmoking_BE.api;

import com.swp391project.SWP391_QuitSmoking_BE.entity.QuitPlan; // Giữ nếu có chỗ cần Entity
import com.swp391project.SWP391_QuitSmoking_BE.dto.quitplan.QuitPlanCreateRequestDTO;
import com.swp391project.SWP391_QuitSmoking_BE.dto.quitplan.QuitPlanUpdateRequestDTO;
import com.swp391project.SWP391_QuitSmoking_BE.dto.quitplan.QuitPlanResponseDTO; // Import DTO
import com.swp391project.SWP391_QuitSmoking_BE.dto.quitplan.QuitPlanAdminResponseDTO; // Import DTO Admin
import com.swp391project.SWP391_QuitSmoking_BE.service.QuitPlanService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.UUID; // Import UUID

@RestController
@RequestMapping("/api/quitplans")
public class QuitPlanAPI {
    @Autowired
    private QuitPlanService quitPlanService;

    // API để lấy tất cả kế hoạch cai thuốc (cho admin)
    @GetMapping("/admin") // Thêm path để phân biệt với API cho user
    public List<QuitPlanAdminResponseDTO> getAllQuitPlansForAdmin() {
        return quitPlanService.getAllQuitPlansForAdmin(); // Sửa: gọi đúng phương thức và trả về DTO Admin
    }

    // API để lấy tất cả kế hoạch cai thuốc của một thành viên (cho thành viên)
    @GetMapping("/member/{memberId}")
    public List<QuitPlanResponseDTO> getQuitPlansByMemberId(@PathVariable UUID memberId) {
        return quitPlanService.getQuitPlansByMemberId(memberId);
    }

    // API để lấy kế hoạch cai thuốc hiện tại của một thành viên
    @GetMapping("/member/{memberId}/current")
    public ResponseEntity<QuitPlanResponseDTO> getCurrentQuitPlanByMemberId(@PathVariable UUID memberId) {
        try {
            QuitPlanResponseDTO quitPlan = quitPlanService.getCurrentQuitPlanByMemberId(memberId);
            return ResponseEntity.ok(quitPlan);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // API để lấy một kế hoạch cai thuốc cụ thể (cho admin)
    @GetMapping("/{id}/admin")
    public ResponseEntity<QuitPlanAdminResponseDTO> getQuitPlanByIdForAdmin(@PathVariable Integer id) {
        QuitPlanAdminResponseDTO quitPlan = quitPlanService.getQuitPlanByIdForAdmin(id);
        return quitPlan != null ? ResponseEntity.ok(quitPlan) : ResponseEntity.notFound().build();
    }

    // API để tạo một kế hoạch cai thuốc (cần memberId từ PathVariable hoặc SecurityContext)
    @PostMapping("/member/{memberId}") // Thêm memberId vào path
    public ResponseEntity<QuitPlanResponseDTO> createQuitPlan(
            @PathVariable UUID memberId,
            @Valid @RequestBody QuitPlanCreateRequestDTO requestDTO) { // Sửa: nhận DTO
        QuitPlanResponseDTO created = quitPlanService.createQuitPlan(memberId, requestDTO); // Sửa: gọi đúng phương thức
        return ResponseEntity.ok(created);
    }

    // API để cập nhật kế hoạch cai thuốc (cần memberId để kiểm tra quyền)
    @PutMapping("/member/{memberId}/plan/{quitPlanId}") // Sửa: thêm memberId và tách quitPlanId
    public ResponseEntity<QuitPlanResponseDTO> updateQuitPlan(
            @PathVariable Integer quitPlanId,
            @PathVariable UUID memberId, // Thêm memberId
            @Valid @RequestBody QuitPlanUpdateRequestDTO updateRequestDTO) { // Sửa: nhận DTO
        try {
            QuitPlanResponseDTO updated = quitPlanService.updateQuitPlan(quitPlanId, memberId, updateRequestDTO); // Sửa: gọi đúng phương thức
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.notFound().build();
        }
    }

    // API để user bỏ cuộc kế hoạch cai thuốc
    @PutMapping("/member/{memberId}/plan/{quitPlanId}/giveup")
    public ResponseEntity<QuitPlanResponseDTO> giveUpQuitPlan(
            @PathVariable Integer quitPlanId,
            @PathVariable UUID memberId) {
        try {
            QuitPlanResponseDTO updated = quitPlanService.giveUpQuitPlan(quitPlanId, memberId);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(null); // Có thể trả về 400 hoặc thông báo lỗi
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteQuitPlan(@PathVariable Integer id) {
        quitPlanService.deleteQuitPlanById(id); // Sửa: gọi đúng phương thức
        return ResponseEntity.noContent().build();
    }
}