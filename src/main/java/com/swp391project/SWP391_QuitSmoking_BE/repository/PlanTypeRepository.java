// Ví dụ về PlanTypeRepository.java (nếu chưa có)
package com.swp391project.SWP391_QuitSmoking_BE.repository;

import com.swp391project.SWP391_QuitSmoking_BE.entity.PlanType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlanTypeRepository extends JpaRepository<PlanType, String> {
    // Có thể thêm các phương thức tìm kiếm tùy chỉnh ở đây nếu cần
}