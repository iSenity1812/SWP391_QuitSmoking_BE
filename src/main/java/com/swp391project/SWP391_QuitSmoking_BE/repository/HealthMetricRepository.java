package com.swp391project.SWP391_QuitSmoking_BE.repository;

import com.swp391project.SWP391_QuitSmoking_BE.entity.HealthMetric;
import com.swp391project.SWP391_QuitSmoking_BE.entity.User;
import com.swp391project.SWP391_QuitSmoking_BE.enums.HealthMetricType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface HealthMetricRepository extends JpaRepository<HealthMetric, UUID> {
    
    List<HealthMetric> findByUserOrderByMetricType(User user);
    
    List<HealthMetric> findByUserAndIsCompletedOrderByMetricType(User user, Boolean isCompleted);
    
    Optional<HealthMetric> findByUserAndMetricType(User user, HealthMetricType metricType);
    
    @Query("SELECT hm FROM HealthMetric hm WHERE hm.user = :user AND hm.isCompleted = false ORDER BY hm.timeRemainingHours ASC")
    List<HealthMetric> findIncompleteMetricsByUserOrderByTargetHours(@Param("user") User user);
    
    @Query("SELECT hm FROM HealthMetric hm WHERE hm.user = :user AND hm.isCompleted = true ORDER BY hm.achievedDate DESC")
    List<HealthMetric> findCompletedMetricsByUserOrderByAchievedDate(@Param("user") User user);
    
    @Query("SELECT COUNT(hm) FROM HealthMetric hm WHERE hm.user = :user AND hm.isCompleted = true")
    Long countCompletedMetricsByUser(@Param("user") User user);
    
    @Query("SELECT COUNT(hm) FROM HealthMetric hm WHERE hm.user = :user")
    Long countTotalMetricsByUser(@Param("user") User user);
    
    @Query("SELECT hm FROM HealthMetric hm WHERE hm.user = :user AND hm.currentProgress > 0 ORDER BY hm.currentProgress DESC LIMIT 3")
    List<HealthMetric> findTopProgressMetricsByUser(@Param("user") User user);
    
    boolean existsByUserAndMetricType(User user, HealthMetricType metricType);
} 