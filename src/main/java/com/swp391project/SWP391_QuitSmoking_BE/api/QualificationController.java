package com.swp391project.SWP391_QuitSmoking_BE.api;

import com.swp391project.SWP391_QuitSmoking_BE.dto.coach.QualificationRequestDTO;
import com.swp391project.SWP391_QuitSmoking_BE.dto.coach.QualificationResponseDTO;
import com.swp391project.SWP391_QuitSmoking_BE.service.QualificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/coaches/{coachId}/qualifications")
@RequiredArgsConstructor
public class QualificationController {
    private final QualificationService qualificationService;

    @GetMapping
    public ResponseEntity<List<QualificationResponseDTO>> getQualifications(@PathVariable UUID coachId) {
        return ResponseEntity.ok(qualificationService.getQualificationsByCoach(coachId));
    }

    @PostMapping
    public ResponseEntity<QualificationResponseDTO> createQualification(@PathVariable UUID coachId, @RequestBody QualificationRequestDTO dto) {
        return ResponseEntity.ok(qualificationService.createQualification(coachId, dto));
    }

    @PutMapping("/{qualificationName}")
    public ResponseEntity<QualificationResponseDTO> updateQualification(@PathVariable UUID coachId, @PathVariable String qualificationName, @RequestBody QualificationRequestDTO dto) {
        return ResponseEntity.ok(qualificationService.updateQualification(coachId, qualificationName, dto));
    }

    @DeleteMapping("/{qualificationName}")
    public ResponseEntity<Void> deleteQualification(@PathVariable UUID coachId, @PathVariable String qualificationName) {
        qualificationService.deleteQualification(coachId, qualificationName);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{qualificationName}/approve")
    public ResponseEntity<QualificationResponseDTO> approveQualification(@PathVariable UUID coachId, @PathVariable String qualificationName, @RequestParam String adminUserId) {
        return ResponseEntity.ok(qualificationService.approveQualification(coachId, qualificationName, adminUserId));
    }
} 