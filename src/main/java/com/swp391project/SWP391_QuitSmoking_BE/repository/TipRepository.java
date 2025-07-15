package com.swp391project.SWP391_QuitSmoking_BE.repository;

import com.swp391project.SWP391_QuitSmoking_BE.entity.Tip;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface TipRepository extends JpaRepository<Tip, UUID> {
    // Lấy 1 Tip ngẫu nhiên
    @Query(value = "SELECT * FROM Tip ORDER BY RANDOM() LIMIT 1", nativeQuery = true)
    Optional<Tip> findRandomTip();

    // Tìm N tip ngẫu nhiên (không loại trừ)
    @Query(value = "SELECT t.* FROM Tip t ORDER BY RANDOM() LIMIT :limit", nativeQuery = true)
    List<Tip> findNRandomTips(int limit);

    // Tìm N tip ngẫu nhiên, loại trừ các ID đã cho
    @Query(value = "SELECT t.* FROM Tip t WHERE t.TipID NOT IN :excludedIds ORDER BY RANDOM() LIMIT :limit", nativeQuery = true)
    List<Tip> findNRandomTipsExcludingIds(int limit, Collection<UUID> excludedIds);
}