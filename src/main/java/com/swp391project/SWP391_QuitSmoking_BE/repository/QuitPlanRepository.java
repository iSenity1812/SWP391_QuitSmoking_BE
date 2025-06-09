package com.swp391project.SWP391_QuitSmoking_BE.repository;

import com.swp391project.SWP391_QuitSmoking_BE.entity.QuitPlan;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface QuitPlanRepository extends JpaRepository<QuitPlan, Integer>  {

}
