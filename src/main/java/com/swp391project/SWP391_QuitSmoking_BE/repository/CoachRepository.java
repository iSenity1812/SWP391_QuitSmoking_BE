package com.swp391project.SWP391_QuitSmoking_BE.repository;

import com.swp391project.SWP391_QuitSmoking_BE.entity.Coach;
import com.swp391project.SWP391_QuitSmoking_BE.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface CoachRepository extends JpaRepository<Coach, UUID> {
    Optional<Coach> findByUser(User user);

    @Query("SELECT c FROM Coach c JOIN FETCH c.user")
    List<Coach> findAllWithUser();
    // Truy vấn để lấy tất cả Coach và JOIN FETCH User của họ
//    @Query("SELECT c FROM Coach c JOIN FETCH c.user")
//    List<Coach> findAllWithUser();
//
//    @EntityGraph(value = "coach.with.user", type = EntityGraph.EntityGraphType.LOAD)
//    Optional<Coach> findByCoachId(UUID coachId);
//
//    @EntityGraph(value = "coach.with.user", type = EntityGraph.EntityGraphType.LOAD)
//    Optional<Coach> findByUser(User user);
//
//    @EntityGraph(value = "coach.with.user", type = EntityGraph.EntityGraphType.LOAD)
//    List<Coach> findAll();
}
