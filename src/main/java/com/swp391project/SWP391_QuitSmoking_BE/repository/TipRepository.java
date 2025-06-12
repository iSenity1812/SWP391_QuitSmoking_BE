package com.swp391project.SWP391_QuitSmoking_BE.repository;

import com.swp391project.SWP391_QuitSmoking_BE.entity.Tip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TipRepository extends JpaRepository<Tip, UUID> {
    // Lấy 1 Tip ngẫu nhiên
    @Query(value = "SELECT * FROM Tip ORDER BY RANDOM() LIMIT 1", nativeQuery = true)
    Optional<Tip> findRandomTip();
}