package com.swp391project.SWP391_QuitSmoking_BE.repository;

import com.swp391project.SWP391_QuitSmoking_BE.entity.Plan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PlanRepository extends JpaRepository<Plan, Integer> {
    // Define any custom query methods if needed
}
