package com.swp391project.SWP391_QuitSmoking_BE.repository;

import com.swp391project.SWP391_QuitSmoking_BE.entity.HealthMetric;
import com.swp391project.SWP391_QuitSmoking_BE.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface HealthMetricRepository extends JpaRepository<HealthMetric, String> {
    
    List<HealthMetric> findByUserOrderByMetricType(User user);
    
    Optional<HealthMetric> findByUserAndMetricType(User user, String metricType);
    
    boolean existsByUserAndMetricType(User user, String metricType);
    
    @Query("SELECT hm FROM HealthMetric hm WHERE hm.user = :user AND hm.isCompleted = true")
    List<HealthMetric> findCompletedMetricsByUser(@Param("user") User user);
    
    @Query("SELECT hm FROM HealthMetric hm WHERE hm.user = :user AND hm.hasRegressed = true")
    List<HealthMetric> findRegressedMetricsByUser(@Param("user") User user);
    
    @Query("SELECT COUNT(hm) FROM HealthMetric hm WHERE hm.user = :user")
    Long countByUser(@Param("user") User user);
    
    @Query("SELECT COUNT(hm) FROM HealthMetric hm WHERE hm.user = :user AND hm.isCompleted = true")
    Long countCompletedByUser(@Param("user") User user);
} 