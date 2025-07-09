package com.swp391project.SWP391_QuitSmoking_BE.repository;

import com.swp391project.SWP391_QuitSmoking_BE.entity.Program;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ProgramRepository extends JpaRepository<Program, Integer> {

    // Tìm kiếm theo tiêu đề HOẶC tên program (có phân trang)
    Page<Program> findByProgramTitleContainingIgnoreCaseOrProgramNameContainingIgnoreCase(
            String titleKeyword, String nameKeyword, Pageable pageable);

    // Tìm kiếm program của một creator cụ thể (có phân trang)
    Page<Program> findByCreatedBy_UserId(UUID creatorId, Pageable pageable);

    // Tìm kiếm program theo type (có phân trang)
    Page<Program> findByProgramTypeContainingIgnoreCase(String programType, Pageable pageable);

    // Đếm số lượng programs của một creator
    @Query("SELECT COUNT(p) FROM Program p WHERE p.createdBy.userId = :creatorId")
    long countByCreatorId(@Param("creatorId") UUID creatorId);
}
